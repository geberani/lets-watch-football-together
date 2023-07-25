package com.ranicorp.letswatchfootballtogether.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentProfileBinding
import com.ranicorp.letswatchfootballtogether.ui.common.EventObserver
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener
import com.ranicorp.letswatchfootballtogether.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

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
        setObservers()
        binding.btnDeleteAccount.setOnClickListener {
            viewModel.deleteAccount()
        }
    }

    private fun setObservers() {
        viewModel.isLoaded.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                Toast.makeText(
                    context,
                    getString(R.string.error_message_profile_loading_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        viewModel.eventsList.observe(viewLifecycleOwner, EventObserver {
            eventsAdapter.submitList(it)
        })
        viewModel.profileImage.observe(viewLifecycleOwner, EventObserver {
            val imageRef = FirebaseStorage.getInstance().reference.child(it)
            imageRef.downloadUrl.addOnSuccessListener {
                binding.imageUri = it.toString()
            }
        })
        viewModel.isDeleted.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                Toast.makeText(
                    context,
                    getString(R.string.guide_message_delete_user_successful),
                    Toast.LENGTH_SHORT
                ).show()
                val action = ProfileFragmentDirections.actionProfileFragmentToSignInFragment()
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.error_message_delete_user_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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