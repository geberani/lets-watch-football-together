package com.ranicorp.letswatchfootballtogether.ui.posting

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ranicorp.letswatchfootballtogether.databinding.ItemAttachedImageBinding

class ImageAdapter(private val deleteClickListener: DeleteClickListener) :
    ListAdapter<Uri, ImageAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.from(parent, deleteClickListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageViewHolder(
        private val binding: ItemAttachedImageBinding,
        private val deleteClickListener: DeleteClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            binding.imageUri = uri.toString()
            binding.deleteClickListener = deleteClickListener
        }

        companion object {
            fun from(parent: ViewGroup, deleteClickListener: DeleteClickListener)
                    : ImageViewHolder {
                return ImageViewHolder(
                    ItemAttachedImageBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    ), deleteClickListener
                )
            }
        }
    }
}

class ImageDiffCallback : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem == newItem
    }
}

