package com.ranicorp.letswatchfootballtogether.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.databinding.ItemViewpagerBannerBinding

class BannerAdapter : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    private val banners = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding =
            ItemViewpagerBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return banners.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position])
    }

    fun submitBannersList(bannersList: List<String>) {
        banners.addAll(bannersList)
        notifyDataSetChanged()
    }

    class BannerViewHolder(private val binding: ItemViewpagerBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: String) {
            val imageRef = FirebaseStorage.getInstance().reference.child(location)
            imageRef.downloadUrl.addOnSuccessListener {
                binding.ivBannerImage.load(it)
            }
        }
    }
}