package com.ranicorp.letswatchfootballtogether.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    private fun setPostDetail() {
        viewModel.getPostDetail(args.postUid)
        viewModel.selectedPost.observe(viewLifecycleOwner) {
            binding.post = it
            binding.tvNumberOfParticipants.text = getString(
                R.string.label_number_of_participants,
                it.currentParticipants,
                it.maxParticipants
            )
            binding.locationInfoLayout.setInfo(it.location)
            bannerAdapter.submitBannersList(it.imageLocations)
        }
        viewModel.participantsInfo.observe(viewLifecycleOwner) {
            participantsAdapter.submitParticipantsList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}