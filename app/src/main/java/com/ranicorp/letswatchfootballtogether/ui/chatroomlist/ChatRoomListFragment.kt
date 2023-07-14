package com.ranicorp.letswatchfootballtogether.ui.chatroomlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentChatRoomListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatRoomListFragment : Fragment() {

    private var _binding: FragmentChatRoomListBinding? = null
    private val binding get() = _binding!!
    private val chatRoomAdapter = ChatRoomAdapter()
    private val viewModel: ChatRoomListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    private fun setLayout() {
        binding.rvChatRoomList.adapter = chatRoomAdapter
        viewModel.getParticipatingEventList()
        viewModel.isLoaded.observe(viewLifecycleOwner) { event ->
            if (!event.content) {
                Toast.makeText(
                    context,
                    getString(R.string.error_message_chat_room_list_loading_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.latestChatList.observe(viewLifecycleOwner) {
            chatRoomAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}