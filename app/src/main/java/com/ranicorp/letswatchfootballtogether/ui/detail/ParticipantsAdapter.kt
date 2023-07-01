package com.ranicorp.letswatchfootballtogether.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.databinding.ItemParticipantsBinding

class ParticipantsAdapter : RecyclerView.Adapter<ParticipantsAdapter.ParticipantsViewHolder>() {

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

    fun submitParticipantsList(participantsList: List<User>) {
        participants.addAll(participantsList)
    }

    class ParticipantsViewHolder(private val binding: ItemParticipantsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: User) {
            binding.ivProfile.setImageURI(participant.profileUri.toUri())
            binding.tvNickName.text = participant.nickName
        }
    }
}