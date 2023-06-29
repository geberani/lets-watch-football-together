package com.ranicorp.letswatchfootballtogether.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.databinding.ItemPopularPostBinding
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener
import com.ranicorp.letswatchfootballtogether.util.DateFormatText

class PopularPostAdapter(private val clickListener: PostClickListener) :
    ListAdapter<Post, PopularPostAdapter.PopularPostItemViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularPostItemViewHolder {
        return PopularPostItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PopularPostItemViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class PopularPostItemViewHolder(private val binding: ItemPopularPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post, clickListener: PostClickListener) {
            binding.post = item
            binding.clickListener = clickListener
            binding.postUid = item.postUid
            val previewImageLocation = item.imageLocations.first()
            val imageRef = FirebaseStorage.getInstance().reference.child(previewImageLocation)
            imageRef.downloadUrl.addOnSuccessListener {
                binding.previewImageUri = it.toString()
            }
            val date = DateFormatText.longToDisplayDate(item.createdTimeMillis)
            binding.dateTimeInfoLayout.setInfo(date + " " + item.time)
            binding.locationInfoLayout.setInfo(item.location)
        }

        companion object {
            fun from(parent: ViewGroup): PopularPostItemViewHolder {
                return PopularPostItemViewHolder(
                    ItemPopularPostBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}