package com.ranicorp.letswatchfootballtogether.ui.detail

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
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private val participantsAdapter = ParticipantsAdapter()
    private val bannerAdapter = BannerAdapter()
    private var maxParticipants = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setLayout()
    }

    private fun setLayout() {
        binding.rvParticipantsList.adapter = participantsAdapter
        binding.imageViewPager.adapter = bannerAdapter
        TabLayoutMediator(binding.indicator, binding.imageViewPager) { tab, position ->

        }.attach()
        binding.detailToolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setPostDetail()

        lifecycleScope.launch {
            viewModel.isLoaded
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isLoaded ->
                    if (isLoaded == false) {
                        Toast.makeText(
                            context,
                            getString(R.string.error_message_not_loaded),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.isParticipateCompleted
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isParticipateCompleted ->
                    if (isParticipateCompleted == false) {
                        Toast.makeText(
                            context,
                            getString(R.string.error_message_participate_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        setParticipateBtn()
        setJoinChatBtn()
    }

    private fun setPostDetail() {
        viewModel.getPostDetail(args.postUid)
        lifecycleScope.launch {
            viewModel.selectedPost
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { selectedPost ->
                    binding.post = selectedPost
                    maxParticipants = selectedPost.maxParticipants
                    binding.locationInfoLayout.setInfo(selectedPost.location)
                    bannerAdapter.submitBannersList(selectedPost.imageLocations)
                }
        }

        lifecycleScope.launch {
            viewModel.participantsInfo
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { participantsInfo ->
                    participantsAdapter.submitList(participantsInfo)
                    binding.tvNumberOfParticipants.text = getString(
                        R.string.label_number_of_participants,
                        participantsInfo.size,
                        maxParticipants
                    )
                }
        }
    }

    private fun setParticipateBtn() {
        binding.btnParticipate.setOnClickListener {
            if (viewModel.isParticipated.value) {
                Toast.makeText(
                    context,
                    getString(R.string.guide_message_already_participated),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (viewModel.selectedPost.value.maxParticipants == viewModel.selectedPost.value.participantsUidList?.size) {
                Toast.makeText(
                    context,
                    getString(R.string.guide_message_exceed_maximum_participants),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.participate()
                Toast.makeText(
                    context,
                    getString(R.string.guide_message_participate_succeeded),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setJoinChatBtn() {
        binding.btnJoinChat.setOnClickListener {
            if (viewModel.isParticipated.value) {
                val action = DetailFragmentDirections.actionDetailFragmentToChatRoomFragment(
                    viewModel.selectedPost.value.postUid,
                    viewModel.selectedPost.value.title
                )
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.guide_message_join_restricted),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}