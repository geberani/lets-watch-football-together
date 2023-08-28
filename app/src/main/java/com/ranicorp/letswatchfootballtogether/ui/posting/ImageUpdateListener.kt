package com.ranicorp.letswatchfootballtogether.ui.posting

import com.ranicorp.letswatchfootballtogether.data.model.ImageContent

interface ImageUpdateListener {

    fun removeImage(imageContent: ImageContent)
}