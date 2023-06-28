package com.ranicorp.letswatchfootballtogether.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.ViewLocationInfoBinding

class LocationInfoLayout(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val binding: ViewLocationInfoBinding

    init {
        binding = ViewLocationInfoBinding.inflate(LayoutInflater.from(context), this)
        context.obtainStyledAttributes(attrs, R.styleable.LocationInfoLayout).use { array ->
            val paddingEnd =
                array.getResourceId(R.styleable.LocationInfoLayout_android_paddingEnd, 0)
            setPaddingEnd(paddingEnd)
        }
    }

    fun setInfo(location: String) {
        binding.tvContent.text = location
    }

    fun getLocationInfo(): String {
        return binding.tvContent.text.toString()
    }

    private fun setPaddingEnd(paddingEnd: Int) {
        binding.ivLocationPin.setPadding(0, 0, paddingEnd, 0)
    }
}