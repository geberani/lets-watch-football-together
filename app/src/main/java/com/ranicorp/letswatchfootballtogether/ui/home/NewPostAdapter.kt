package com.ranicorp.letswatchfootballtogether.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.databinding.ItemNewPostBinding
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener
import com.ranicorp.letswatchfootballtogether.ui.common.PostDiffCallback

class NewPostAdapter(private val clickListener: PostClickListener) :
    ListAdapter<Post, NewPostAdapter.NewPostItemViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewPostItemViewHolder {
        return NewPostItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NewPostItemViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class NewPostItemViewHolder(private val binding: ItemNewPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post, clickListener: PostClickListener) {
            binding.clickListener = clickListener
            binding.postUid = item.postUid
            val previewImageLocation = item.imageLocations.first()
            val imageRef = FirebaseStorage.getInstance().reference.child(previewImageLocation)
            imageRef.downloadUrl.addOnSuccessListener {
                binding.previewImageUri = it.toString()
            }
            binding.location = item.location
        }

        companion object {
            fun from(parent: ViewGroup): NewPostItemViewHolder {
                return NewPostItemViewHolder(
                    ItemNewPostBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}