package com.ranicorp.letswatchfootballtogether.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentProfileBinding
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener
import com.ranicorp.letswatchfootballtogether.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(), PostClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val eventsAdapter = MyEventsAdapter(this)
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setLayout()
    }

    private fun setLayout() {
        viewModel.getUserInfo()
        binding.rvMyEvent.adapter = eventsAdapter
        binding.nickName = viewModel.userNickName
        setSubscribers()
        binding.btnDeleteAccount.setOnClickListener {
            viewModel.deleteAccount()
        }
    }

    private fun setSubscribers() {
        lifecycleScope.launch {
            viewModel.guideMsgResId
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { guideMsgResId ->
                    if (guideMsgResId != null) {
                        Toast.makeText(
                            context,
                            getString(guideMsgResId),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (guideMsgResId == R.string.guide_message_delete_user_successful) {
                        val action =
                            ProfileFragmentDirections.actionProfileFragmentToSignInFragment()
                        findNavController().navigate(action)
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.eventsList
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { eventsList ->
                    eventsAdapter.submitList(eventsList)
                }
        }

        lifecycleScope.launch {
            viewModel.profileImage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { profileImage ->
                    binding.imageUri = profileImage
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPostClick(postUid: String) {
        val action = HomeFragmentDirections.actionGlobalDetailFragment(postUid)
        findNavController().navigate(action)
    }
}