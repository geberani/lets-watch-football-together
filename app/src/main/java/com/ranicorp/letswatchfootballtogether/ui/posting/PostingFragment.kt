package com.ranicorp.letswatchfootballtogether.ui.posting

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.FootballApplication
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.databinding.FragmentPostingBinding
import com.ranicorp.letswatchfootballtogether.ui.common.ProgressDialogFragment

class PostingFragment : Fragment(), DeleteClickListener, HeaderClickListener {

    private var _binding: FragmentPostingBinding? = null
    private val binding get() = _binding!!
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.addImage(uri)
                updateAdapterData()
            }
        }
    private val attachedImageAdapter = ImageAdapter(this)
    private val attachedImageHeaderAdapter = HeaderAdapter(this)
    private val viewModel by viewModels<PostingViewModel> {
        PostingViewModel.provideFactory(
            PostRepository(RemoteDataSource(FootballApplication.appContainer.provideApiClient())),
            UserPreferenceRepository(),
            FirebaseStorage.getInstance()
        )
    }
    private val progressDialog = ProgressDialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setLayout()
    }

    private fun updateAdapterData() {
        attachedImageAdapter.submitList(viewModel.imageUriList.value)
        attachedImageHeaderAdapter.submitList(
            getString(
                R.string.number_of_image_displayed,
                viewModel.imageUriList.value?.size
            )
        )
        attachedImageHeaderAdapter.notifyDataSetChanged()
    }

    private fun setLayout() {
        binding.imageRecyclerView.adapter =
            ConcatAdapter(attachedImageHeaderAdapter, attachedImageAdapter)
        attachedImageHeaderAdapter.submitList(getString(R.string.number_of_image_displayed, 0))
        viewModel.errorMsgResId.observe(viewLifecycleOwner) {
            Toast.makeText(
                context,
                getString(it),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnAddPost.setOnClickListener {
            viewModel.complete()
        }
        viewModel.isAdded.observe(viewLifecycleOwner) {
            if (it == true) {
                progressDialog.show(requireActivity().supportFragmentManager, null)
            } else {
                progressDialog.dismiss()
            }
        }
    }

    override fun onDeleteClick(uri: String) {
        viewModel.removeImage(uri.toUri())
        updateAdapterData()
    }

    override fun onHeaderClick() {
        if (viewModel.imageUriList.value?.size == 10) {
            Toast.makeText(
                context,
                getString(R.string.guide_message_limit_max_images),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            getContent.launch("image/*")
        }
    }
}