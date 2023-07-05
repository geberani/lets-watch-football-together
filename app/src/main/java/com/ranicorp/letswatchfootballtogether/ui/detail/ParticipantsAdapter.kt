package com.ranicorp.letswatchfootballtogether.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.databinding.ItemParticipantsBinding

class ParticipantsAdapter :
    ListAdapter<User, ParticipantsAdapter.ParticipantsViewHolder>(ParticipantDiffUtil()) {

    private val participants = mutableListOf<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParticipantsViewHolder {
        val binding =
            ItemParticipantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParticipantsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ParticipantsViewHolder,
        position: Int
    ) {
        holder.bind(participants[position])
    }

    override fun getItemCount(): Int {
        return participants.size
    }

    override fun submitList(participantsList: MutableList<User>?) {
        super.submitList(participantsList)
        participants.clear()
        participants.addAll(participantsList?.toList() ?: emptyList())
    }

    class ParticipantsViewHolder(private val binding: ItemParticipantsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: User) {
            val imageRef = FirebaseStorage.getInstance().reference.child(participant.profileUri)
            imageRef.downloadUrl.addOnSuccessListener {
                binding.ivProfile.load(it)
            }
            binding.tvNickName.text = participant.nickName
        }
    }

    class ParticipantDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
}