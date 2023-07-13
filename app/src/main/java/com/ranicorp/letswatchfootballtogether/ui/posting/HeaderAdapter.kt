package com.ranicorp.letswatchfootballtogether.ui.posting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ranicorp.letswatchfootballtogether.data.model.ImageContentHeader
import com.ranicorp.letswatchfootballtogether.databinding.ItemImageHeaderBinding

class HeaderAdapter(private val listener: ImageRequestListener) :
    RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {

    private val items = mutableListOf<ImageContentHeader>()
    private var itemLimit = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeaderViewHolder {
        return HeaderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setImageHeader(limit: Int) {
        itemLimit = limit
        items.add(ImageContentHeader(0, limit))
        notifyItemChanged(0)
    }

    fun updateImageHeader(currentSize: Int) {
        items[0] = ImageContentHeader(currentSize, itemLimit)
        notifyItemChanged(0)
    }

    fun removeImage() {
        val currentSize = items[0].size
        items[0] = ImageContentHeader(currentSize - 1, itemLimit)
        notifyItemChanged(0)
    }

    class HeaderViewHolder(
        private val binding: ItemImageHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: ImageContentHeader, listener: ImageRequestListener) {
            binding.header = header
            binding.imageRequestListener = listener
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                return HeaderViewHolder(
                    ItemImageHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}