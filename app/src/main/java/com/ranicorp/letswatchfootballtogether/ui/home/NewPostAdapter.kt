package com.ranicorp.letswatchfootballtogether.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.databinding.ItemNewPostBinding
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener

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
            binding.root.setOnClickListener {
                clickListener.onPostClick(item.postUid)
            }
            bindPreviewImage(item)
            binding.tvPostLocation.text = item.location
        }

        private fun bindPreviewImage(item: Post) {
            val previewImageLocation = item.imageLocations.first()
            val imageRef = FirebaseStorage.getInstance().reference.child(previewImageLocation)
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                binding.ivPostPreview.load(uri) {
                    transformations(CircleCropTransformation())
                    error(R.drawable.ic_image_not_supported)
                }
            }
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