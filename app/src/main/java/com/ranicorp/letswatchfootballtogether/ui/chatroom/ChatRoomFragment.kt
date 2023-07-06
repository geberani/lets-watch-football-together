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
import com.ranicorp.letswatchfootballtogether.databinding.FragmentChatRoomBinding
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
        //viewModel.setPostUid(args.postUid)
        viewModel.setPostUid("sample")
        setLayout()
    }

    private fun setLayout() {
        binding.rvChat.adapter = messageAdapter
        viewModel.getAllChat()
        viewModel.allChat.observe(viewLifecycleOwner) {
            messageAdapter.submitList(it, viewModel.userUid)
        }
        binding.etWriteText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val enteredText = binding.etWriteText.text.toString()
                viewModel.addChat(enteredText)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}