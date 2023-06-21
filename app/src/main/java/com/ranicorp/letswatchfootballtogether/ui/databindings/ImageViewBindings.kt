package com.ranicorp.letswatchfootballtogether.ui.databindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.ranicorp.letswatchfootballtogether.R

@BindingAdapter("imageUri")
fun loadImage(view: ImageView, uri: String?) {
    if (uri != null) {
        view.load(uri) {
            transformations(CircleCropTransformation())
            error(R.drawable.ic_image_not_supported)
        }
    } else {
        //이미지를 선택하지 않았을 때에 대한 처리
    }
}