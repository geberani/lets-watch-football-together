package com.ranicorp.letswatchfootballtogether.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentSettingBinding
import com.ranicorp.letswatchfootballtogether.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.setProfileUri(uri.toString())
                } else {
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
        binding.ivProfile.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        viewModel.updateExistingNickNameList()
        binding.etNickname.doAfterTextChanged {
            viewModel.setNickName(it.toString())
            viewModel.validateNickName(it.toString())
            viewModel.errorMsg.observe(viewLifecycleOwner) { errorMsg ->
                setErrorMsg(errorMsg)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}