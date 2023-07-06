package com.ranicorp.letswatchfootballtogether.ui.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ranicorp.letswatchfootballtogether.data.model.ChatItem
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.model.ReceivedMessage
import com.ranicorp.letswatchfootballtogether.data.model.SentMessage
import com.ranicorp.letswatchfootballtogether.databinding.ItemReceivedMessageBinding
import com.ranicorp.letswatchfootballtogether.databinding.ItemSentMessageBinding

private const val VIEW_TYPE_SENT_MESSAGE = 0
private const val VIEW_TYPE_RECEIVED_MESSAGE = 1

class MessageAdapter : ListAdapter<ChatItem, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT_MESSAGE -> SentMessageViewHolder.from(parent)
            else -> ReceivedMessageViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SentMessageViewHolder -> {
                val item = getItem(position) as SentMessage
                holder.bind(item)
            }
            is ReceivedMessageViewHolder -> {
                val item = getItem(position) as ReceivedMessage
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SentMessage -> VIEW_TYPE_SENT_MESSAGE
            is ReceivedMessage -> VIEW_TYPE_RECEIVED_MESSAGE
        }
    }

    override fun submitList(list: MutableList<ChatItem>?) {
        super.submitList(list)
    }

    fun submitList(messageList: List<Message>, userUid: String) {
        val result = mutableListOf<ChatItem>()
        for (item in messageList) {
            if (item.senderUid == userUid) {
                result.add(SentMessage(item))
            } else {
                result.add(ReceivedMessage(item))
            }
        }
        submitList(result)
    }

    class SentMessageViewHolder(private val binding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SentMessage) {
            binding.tvSentText.text = item.message.content
            binding.tvSentTime.text = item.message.sentTimeMillis.toString()
            //TODO 시간 형식 변경
        }

        companion object {
            fun from(parent: ViewGroup): SentMessageViewHolder {
                return SentMessageViewHolder(
                    ItemSentMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class ReceivedMessageViewHolder(private val binding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReceivedMessage) {
            binding.tvReceivedText.text = item.message.content
            binding.tvReceivedTime.text = item.message.sentTimeMillis.toString()
            //TODO 시간 형식 변경
        }

        companion object {
            fun from(parent: ViewGroup): ReceivedMessageViewHolder {
                return ReceivedMessageViewHolder(
                    ItemReceivedMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }

    }
}