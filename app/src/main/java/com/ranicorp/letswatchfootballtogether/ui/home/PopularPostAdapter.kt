package com.ranicorp.letswatchfootballtogether.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.databinding.ItemPopularPostBinding
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener

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
            binding.root.setOnClickListener {
                clickListener.onPostClick(item.postUid)
            }
            bindPreviewImage(item)
            binding.tvPostTitle.text = item.title
            binding.dateTimeInfoLayout.setInfo(item.date + " " + item.time)
            binding.locationInfoLayout.setInfo(item.location)
        }

        private fun bindPreviewImage(item: Post) {
            val previewImageLocation = item.imageLocations.first()
            val imageRef = FirebaseStorage.getInstance().reference.child(previewImageLocation)
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                binding.ivPostPreview.load(uri) {
                    transformations(RoundedCornersTransformation(8f))
                    error(R.drawable.ic_image_not_supported)
                }
            }
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