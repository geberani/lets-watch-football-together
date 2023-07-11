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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentPostingBinding
import com.ranicorp.letswatchfootballtogether.ui.common.EventObserver
import com.ranicorp.letswatchfootballtogether.ui.common.ProgressDialogFragment
import com.ranicorp.letswatchfootballtogether.util.DateFormatText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
    private val viewModel: PostingViewModel by viewModels()
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
        attachedImageAdapter.submitList(viewModel.imageUriList.value?.content)
        attachedImageHeaderAdapter.submitList(
            getString(
                R.string.number_of_image_displayed,
                viewModel.imageUriList.value?.content?.size
            )
        )
        attachedImageHeaderAdapter.notifyDataSetChanged()
    }

    private fun setLayout() {
        setAdapter()
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

    private fun setAdapter() {
        binding.rvImage.adapter =
            ConcatAdapter(attachedImageHeaderAdapter, attachedImageAdapter)
        attachedImageHeaderAdapter.submitList(getString(R.string.number_of_image_displayed, 0))
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
                progressDialog.show(requireActivity().supportFragmentManager, null)
            } else {
                progressDialog.dismiss()
            }
        })
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

    override fun onDeleteClick(uri: String) {
        viewModel.removeImage(uri.toUri())
        updateAdapterData()
    }

    override fun onHeaderClick() {
        if (viewModel.imageUriList.value?.content?.size == 10) {
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