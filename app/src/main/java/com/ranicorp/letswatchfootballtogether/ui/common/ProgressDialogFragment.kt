package com.ranicorp.letswatchfootballtogether.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.ranicorp.letswatchfootballtogether.databinding.FragmentProgressDialogBinding

class ProgressDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProgressDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setOnKeyListener { _, keyCode, event ->
                return@setOnKeyListener keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP
            }
        }
    }

    override fun onResume() {
        super.onResume()
        context?.setDialogSize(this, 0.9f, 0.2f)
    }

    private fun Context.setDialogSize(
        dialogFragment: DialogFragment,
        widthRatio: Float,
        heightRatio: Float
    ) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)

            val window = dialogFragment.dialog?.window
            val x = (size.x * widthRatio).toInt()
            val y = (size.y * heightRatio).toInt()
            window?.setLayout(x, y)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = dialogFragment.dialog?.window
            val x = (rect.width() * widthRatio).toInt()
            val y = (rect.height() * heightRatio).toInt()
            window?.setLayout(x, y)
        }
    }
}