package com.ranicorp.letswatchfootballtogether.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.ranicorp.letswatchfootballtogether.data.model.Post

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.postUid == newItem.postUid
    }

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.description == newItem.description
    }
}