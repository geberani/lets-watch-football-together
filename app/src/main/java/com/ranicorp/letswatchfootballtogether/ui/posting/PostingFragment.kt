package com.ranicorp.letswatchfootballtogether.ui.posting

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentPostingBinding
import com.ranicorp.letswatchfootballtogether.ui.common.EventObserver
import com.ranicorp.letswatchfootballtogether.util.DateFormatText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostingFragment : Fragment(), ImageRequestListener, ImageUpdateListener {

    private var _binding: FragmentPostingBinding? = null
    private val binding get() = _binding!!
    private val getMultipleContents =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            addImageList(it)
        }
    private val imageCountLimit = 10
    private val imageListAdapter = ImageAdapter(imageCountLimit, this)
    private val imageHeaderAdapter = HeaderAdapter(this)
    private val viewModel: PostingViewModel by viewModels()

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

    private fun setLayout() {
        setImageList()
        setObservers()
        setOnClickListeners()
        viewModel.isComplete.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.error_message_post_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setImageList() {
        imageHeaderAdapter.setImageHeader(imageCountLimit)
        binding.rvImage.adapter =
            ConcatAdapter(imageHeaderAdapter, imageListAdapter)
    }

    private fun setObservers() {
        viewModel.errorMsgResId.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(
                context,
                getString(it),
                Toast.LENGTH_SHORT
            ).show()
        })
        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                findNavController().navigate(PostingFragmentDirections.actionPostingFragmentToProgressDialogFragment())
            }
        })
    }

    private fun setOnClickListeners() {
        binding.btnAddPost.setOnClickListener {
            viewModel.complete()
        }
        binding.ivDate.setOnClickListener {
            chooseDate()
        }
        binding.etDate.setOnClickListener {
            chooseDate()
        }
        binding.ivTime.setOnClickListener {
            chooseTime()
        }
        binding.etTime.setOnClickListener {
            chooseTime()
        }
        binding.postingToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun chooseDate() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.title_set_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.show(childFragmentManager, null)
        datePicker.addOnPositiveButtonClickListener {
            binding.etDate.setText(DateFormatText.longToDateString(it))
        }
    }

    private fun chooseTime() {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText(getString(R.string.title_set_time))
                .build()
        timePicker.show(childFragmentManager, null)
        timePicker.addOnPositiveButtonClickListener {
            var hour = timePicker.hour
            val minute = timePicker.minute

            if (hour <= 12) {
                binding.etTime.setText(getString(R.string.label_am_time, hour, minute))
            } else {
                hour -= 12
                binding.etTime.setText(getString(R.string.label_pm_time, hour, minute))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addImageList(uriList: List<Uri>) {
        for (uri in uriList) {
            imageListAdapter.addImage(uri)
        }
        imageHeaderAdapter.updateImageHeader(imageListAdapter.itemCount)
        viewModel.updateImageList(imageListAdapter.getItems())
    }

    override fun onImageContentRequest() {
        if (imageListAdapter.itemCount == 10) {
            Toast.makeText(
                context,
                getString(R.string.guide_message_limit_max_images),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            getMultipleContents.launch("image/*")
        }
    }

    override fun removeImage(position: Int) {
        imageListAdapter.removeImage(position)
        imageHeaderAdapter.removeImage()
    }
}