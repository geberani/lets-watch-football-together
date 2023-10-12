package com.ranicorp.letswatchfootballtogether.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentSettingBinding
import com.ranicorp.letswatchfootballtogether.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val viewModel: SettingViewModel by viewModels()
    private val args: SettingFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.setProfileUri(uri.toString())
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.googleUid = args.googleUid
        binding.ivProfile.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        viewModel.updateExistingNickNameList()
        setSettingCompleteNavigation()
        setNickNameErrorMsg()
        setSettingErrorMsg()
        setProgressDialog()
    }

    private fun setSettingCompleteNavigation() {
        lifecycleScope.launch {
            viewModel.isSettingComplete
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isSettingComplete ->
                    if (isSettingComplete) {
                        findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToHomeFragment())
                    }
                }
        }
    }

    private fun setNickNameErrorMsg() {
        binding.etNickname.doAfterTextChanged {
            viewModel.setNickName(it.toString())
            viewModel.validateNickName(it.toString())
            lifecycleScope.launch {
                viewModel.nickNameErrorType
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .collect { errorMsg ->
                        if (!errorMsg.isNullOrEmpty()) {
                            setErrorMsg(errorMsg)
                        }
                    }
            }
        }
    }

    private fun setSettingErrorMsg() {
        lifecycleScope.launch {
            viewModel.settingErrorType
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { settingErrorType ->
                    if (settingErrorType != null) {
                        val errorMsg = when (settingErrorType) {
                            Constants.ERROR_SETTING_NOT_AVAILABLE -> getString(R.string.error_message_setting_not_available)
                            Constants.ERROR_SETTING_FAILED -> getString(R.string.error_message_setting_failed)
                            else -> getString(R.string.error_message_setting_input_not_complete)
                        }
                        Toast.makeText(
                            context,
                            errorMsg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun setErrorMsg(errorMsg: String?) {
        binding.etNickname.error = when (errorMsg) {
            Constants.ERROR_EMPTY_NICK_NAME -> getString(R.string.error_message_empty_nick_name)
            Constants.ERROR_MAX_LENGTH -> getString(R.string.error_message_max_length)
            Constants.ERROR_DUPLICATE_NICK_NAME -> getString(R.string.error_message_duplicate_nick_name)
            else -> null
        }
    }

    private fun setProgressDialog() {
        lifecycleScope.launch {
            viewModel.isLoading
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isLoading ->
                    if (isLoading == Constants.SETTING_LOADING) {
                        findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToProgressDialogFragment())
                    } else if (isLoading == Constants.SETTING_COMPLETE) {
                        findNavController().navigateUp()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}