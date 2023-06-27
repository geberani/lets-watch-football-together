package com.ranicorp.letswatchfootballtogether.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.ViewLocationInfoBinding

class LocationInfoLayout(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val binding: ViewLocationInfoBinding

    init {
        binding = ViewLocationInfoBinding.inflate(LayoutInflater.from(context), this)
        context.obtainStyledAttributes(attrs, R.styleable.LocationInfoLayout)
            .use { array ->
                val labelResId =
                    array.getResourceId(R.styleable.LocationInfoLayout_locationInfoLabel, 0)
                val paddingEnd =
                    array.getResourceId(R.styleable.LocationInfoLayout_android_paddingEnd, 0)
                setInfoLabel(labelResId)
                setPaddingEnd(paddingEnd)
            }
    }

    fun setInfo(location: String) {
        binding.tvContent.text = location
    }

    fun getLocationInfo(): String {
        return binding.tvContent.text.toString()
    }

    private fun setInfoLabel(labelResId: Int) {
        binding.tvContent.text = context.getString(labelResId)
    }

    private fun setPaddingEnd(paddingEnd: Int) {
        binding.ivLocationPin.setPadding(0, 0, paddingEnd, 0)
    }
}