package com.ranicorp.letswatchfootballtogether.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private val participantsAdapter = ParticipantsAdapter()
    private val bannerAdapter = BannerAdapter()

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
        setPostDetail()
        setParticipateBtn()
        setJoinChatBtn()
    }

    private fun setPostDetail() {
        viewModel.getPostDetail(args.postUid)
        viewModel.selectedPost.observe(viewLifecycleOwner) {
            binding.post = it
            binding.tvNumberOfParticipants.text = getString(
                R.string.label_number_of_participants,
                it.participantsUidList.size,
                it.maxParticipants
            )
            binding.locationInfoLayout.setInfo(it.location)
            bannerAdapter.submitBannersList(it.imageLocations)
        }
        viewModel.participantsInfo.observe(viewLifecycleOwner) {
            participantsAdapter.submitParticipantsList(it)
        }
    }

    private fun setParticipateBtn() {
        binding.btnParticipate.setOnClickListener {
            if (viewModel.isParticipated.value == true) {
                Toast.makeText(
                    context,
                    getString(R.string.guide_message_already_participated),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (viewModel.selectedPost.value?.maxParticipants == viewModel.selectedPost.value?.participantsUidList?.size) {
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
            if (viewModel.isParticipated.value == true) {
                TODO("채팅방으로 이동")
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