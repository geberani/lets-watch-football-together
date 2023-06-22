package com.ranicorp.letswatchfootballtogether.ui.posting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ranicorp.letswatchfootballtogether.databinding.ItemImageHeaderBinding

class HeaderAdapter(private val onHeaderClick: HeaderClickListener) :
    RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {

    private var numberOfImage = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeaderViewHolder {
        return HeaderViewHolder.from(parent, onHeaderClick)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(numberOfImage)
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun submitList(sizeOfImage: String) {
        numberOfImage = sizeOfImage
    }

    class HeaderViewHolder(
        private val binding: ItemImageHeaderBinding,
        private val onHeaderClick: HeaderClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(numberOfImage: String) {
            binding.tvNumberOfAttachedImages.text = numberOfImage
            binding.root.setOnClickListener {
                onHeaderClick.onHeaderClick()
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onHeaderClick: HeaderClickListener
            ): HeaderViewHolder {
                return HeaderViewHolder(
                    ItemImageHeaderBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    ), onHeaderClick
                )
            }
        }
    }
}