package com.ranicorp.letswatchfootballtogether.ui.posting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ranicorp.letswatchfootballtogether.databinding.ItemImageHeaderBinding

class HeaderAdapter(private val headerClickListener: HeaderClickListener) :
    RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {

    private var numberOfImage = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeaderViewHolder {
        return HeaderViewHolder.from(parent, headerClickListener)
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
        private val headerClickListener: HeaderClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(numberOfImage: String) {
            binding.numberOfImage = numberOfImage
            binding.headerClickListener = headerClickListener
        }

        companion object {
            fun from(
                parent: ViewGroup,
                headerClickListener: HeaderClickListener
            ): HeaderViewHolder {
                return HeaderViewHolder(
                    ItemImageHeaderBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    ), headerClickListener
                )
            }
        }
    }
}