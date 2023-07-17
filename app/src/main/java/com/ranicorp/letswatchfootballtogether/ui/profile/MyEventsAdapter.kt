package com.ranicorp.letswatchfootballtogether.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.databinding.ItemAllPostBinding
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener
import com.ranicorp.letswatchfootballtogether.ui.common.PostDiffCallback
import com.ranicorp.letswatchfootballtogether.util.DateFormatText

class MyEventsAdapter(private val clickListener: PostClickListener) :
    ListAdapter<Post, MyEventsAdapter.MyEventsViewHolder>(
        PostDiffCallback()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyEventsViewHolder {
        return MyEventsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyEventsViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class MyEventsViewHolder(private val binding: ItemAllPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post, clickListener: PostClickListener) {
            binding.post = item
            val date = DateFormatText.longToDisplayDate(item.createdTimeMillis)
            binding.dateTimeInfoLayout.setInfo(date + " " + item.time)
            binding.locationInfoLayout.setInfo(item.location)
            binding.clickListener = clickListener
            binding.postUid = item.postUid
            val previewImageLocation = item.imageLocations.first()
            val imageRef = FirebaseStorage.getInstance().reference.child(previewImageLocation)
            imageRef.downloadUrl.addOnSuccessListener {
                binding.previewImageUri = it.toString()
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyEventsViewHolder {
                return MyEventsViewHolder(
                    ItemAllPostBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }
}