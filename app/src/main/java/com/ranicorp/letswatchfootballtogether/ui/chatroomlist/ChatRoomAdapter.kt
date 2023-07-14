package com.ranicorp.letswatchfootballtogether.ui.chatroomlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
import com.ranicorp.letswatchfootballtogether.databinding.ItemChatRoomBinding
import com.ranicorp.letswatchfootballtogether.util.DateFormatText

class ChatRoomAdapter : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    private val chatRooms = mutableListOf<ChatRoomInfo>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatRoomViewHolder {
        return ChatRoomViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(chatRooms[position])
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    fun submitList(list: List<ChatRoomInfo>) {
        chatRooms.clear()
        chatRooms.addAll(list)
        notifyDataSetChanged()
    }

    class ChatRoomViewHolder(private val binding: ItemChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatRoomInfo: ChatRoomInfo) {
            val imageRef = FirebaseStorage.getInstance().reference.child(chatRoomInfo.imageLocation)
            imageRef.downloadUrl.addOnSuccessListener {
                binding.ivPostPreview.load(it)
            }
            binding.tvLastMsg.text = chatRoomInfo.lastMsg
            binding.tvPostTitle.text = chatRoomInfo.title
            if (chatRoomInfo.lastSentTime.toInt() == 0) {
                binding.tvSentTime.text = ""
            } else {
                binding.tvSentTime.text =
                    DateFormatText.longToLastSentDate(chatRoomInfo.lastSentTime)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ChatRoomViewHolder {
                return ChatRoomViewHolder(
                    ItemChatRoomBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}