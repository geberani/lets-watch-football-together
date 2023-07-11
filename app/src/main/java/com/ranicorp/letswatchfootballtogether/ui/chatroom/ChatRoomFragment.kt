package com.ranicorp.letswatchfootballtogether.ui.chatroom

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ranicorp.letswatchfootballtogether.data.model.ChatItem
import com.ranicorp.letswatchfootballtogether.data.model.ReceivedMessage
import com.ranicorp.letswatchfootballtogether.data.model.SentMessage
import com.ranicorp.letswatchfootballtogether.databinding.FragmentChatRoomBinding
import com.ranicorp.letswatchfootballtogether.ui.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private val args: ChatRoomFragmentArgs by navArgs()
    private val viewModel: ChatRoomViewModel by viewModels()
    private val messageAdapter = MessageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.title = args.postTitle
        viewModel.setPostUid(args.postUid)
        setLayout()
    }

    private fun setLayout() {
        binding.rvChat.adapter = messageAdapter
        viewModel.getAllChat()
        sendText()
        setData()
        viewModel.addChatEventListener()
    }

    private fun sendText() {
        binding.etWriteText.setOnEditorActionListener { _, actionId, event ->
            val enteredText = binding.etWriteText.text.toString()
            if (enteredText.isBlank()) return@setOnEditorActionListener true
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                viewModel.addChat(enteredText)
                return@setOnEditorActionListener true
            }
            false
        }
        viewModel.isSent.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                binding.etWriteText.text.clear()
            }
        })
    }

    private fun setData() {
        viewModel.allChat.observe(viewLifecycleOwner, EventObserver { messageList ->
            val chatItemList = mutableListOf<ChatItem>()
            for (item in messageList) {
                if (item.senderUid == viewModel.userUid) {
                    chatItemList.add(SentMessage(item))
                } else {
                    chatItemList.add(ReceivedMessage(item))
                }
            }
            messageAdapter.submitList(chatItemList)
            binding.rvChat.scrollToPosition(messageAdapter.itemCount - 1)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}