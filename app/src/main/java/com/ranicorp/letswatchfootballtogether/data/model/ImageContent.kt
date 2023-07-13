package com.ranicorp.letswatchfootballtogether.data.model

import android.net.Uri

data class ImageContentHeader(
    val size: Int,
    val limit: Int
)

data class ImageContent(
    val uri: Uri
)