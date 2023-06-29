package com.ranicorp.letswatchfootballtogether.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.ViewDateTimeInfoBinding

class DateTimeInfoLayout(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val binding: ViewDateTimeInfoBinding

    init {
        binding = ViewDateTimeInfoBinding.inflate(LayoutInflater.from(context), this)
        context.obtainStyledAttributes(attrs, R.styleable.DateTimeInfoLayout).use { array ->
            val paddingEnd =
                array.getResourceId(R.styleable.DateTimeInfoLayout_android_paddingEnd, 0)
            setPaddingEnd(paddingEnd)
        }
    }

    fun setInfo(dateTime: String) {
        binding.tvContent.text = dateTime
    }

    fun getDateTimeInfo(): String {
        return binding.tvContent.text.toString()
    }

    private fun setPaddingEnd(paddingEnd: Int) {
        binding.ivCalendar.setPadding(0, 0, paddingEnd, 0)
    }
}