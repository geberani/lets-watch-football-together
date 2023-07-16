package com.ranicorp.letswatchfootballtogether.ui.chatroom

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ranicorp.letswatchfootballtogether.R
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
        binding.chatRoomToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.rvChat.adapter = messageAdapter
        viewModel.getAllChat()
        sendText()
        setData()
        viewModel.addChatEventListener()
        viewModel.isLoaded.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                Toast.makeText(
                    context,
                    getString(R.string.error_message_chat_room_loading_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun sendText() {
        binding.etWriteText.setOnEditorActionListener { _, actionId, event ->
            val enteredText = binding.etWriteText.text.toString()
            if (enteredText.isBlank()) return@setOnEditorActionListener true
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                viewModel.sendChat(enteredText)
                return@setOnEditorActionListener true
            }
            false
        }
        viewModel.isSendingComplete.observe(viewLifecycleOwner, EventObserver {
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