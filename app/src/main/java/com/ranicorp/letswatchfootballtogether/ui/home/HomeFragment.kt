package com.ranicorp.letswatchfootballtogether.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ranicorp.letswatchfootballtogether.FootballApplication
import com.ranicorp.letswatchfootballtogether.HomeGraphDirections
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.databinding.FragmentHomeBinding
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener

class HomeFragment : Fragment(), PostClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModel.provideFactory(PostRepository(RemoteDataSource(FootballApplication.appContainer.provideApiClient())))
    }
    private val homeAdapter = HomeAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setLayout()
    }

    private fun setLayout() {
        binding.rvHome.adapter = homeAdapter
        binding.btnAddPost.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPostingFragment()
            findNavController().navigate(action)
        }
        updateData()
        binding.refreshLayout.setOnRefreshListener {
            updateData()
        }
    }

    private fun updateData() {
        viewModel.loadAllPosts()
        viewModel.allPosts.observe(viewLifecycleOwner) { allPosts ->
            homeAdapter.submitAllPostList(
                allPosts,
                getString(R.string.header_new_posts),
                getString(R.string.header_popular_posts),
                getString(R.string.header_all_posts)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPostClick(postUid: String) {
        val action = HomeGraphDirections.actionGlobalDetailFragment(postUid)
        findNavController().navigate(action)
    }
}