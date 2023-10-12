package com.ranicorp.letswatchfootballtogether.ui.posting

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ranicorp.letswatchfootballtogether.data.model.ImageContent
import com.ranicorp.letswatchfootballtogether.databinding.ItemAttachedImageBinding

class ImageAdapter(private val limit: Int, private val listener: ImageUpdateListener) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val items = mutableListOf<ImageContent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(position, items[position], listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItems(): List<ImageContent> {
        return items
    }

    fun addImage(uri: Uri) {
        val position = items.size
        if (position < limit) {
            items.add(ImageContent(uri))
            notifyItemInserted(position)
        }
    }

    fun removeImage(imageContent: ImageContent) {
        items.remove(imageContent)
        notifyDataSetChanged()
    }

    class ImageViewHolder(private val binding: ItemAttachedImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pos: Int, item: ImageContent, updateListener: ImageUpdateListener) {
            with(binding) {
                position = pos
                imageContent = item
                listener = updateListener
            }
        }

        companion object {
            fun from(parent: ViewGroup): ImageViewHolder {
                return ImageViewHolder(
                    ItemAttachedImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

