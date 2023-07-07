package com.ranicorp.letswatchfootballtogether.ui.databindings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ranicorp.letswatchfootballtogether.util.DateFormatText

@BindingAdapter("sentTime")
fun convertToSentTime(view: TextView, time: Long) {
    view.text = DateFormatText.longToHourMin(time)
}
