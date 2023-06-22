package com.ranicorp.letswatchfootballtogether.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ranicorp.letswatchfootballtogether.databinding.ViewProgressIndicatorBinding

class ProgressIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        ViewProgressIndicatorBinding.inflate(LayoutInflater.from(context), this)
    }
}