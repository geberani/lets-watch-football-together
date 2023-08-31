package com.ranicorp.letswatchfootballtogether.ui.chatroomlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.databinding.FragmentChatRoomListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatRoomListFragment : Fragment(), ChatRoomClickListener {

    private var _binding: FragmentChatRoomListBinding? = null
    private val binding get() = _binding!!
    private val chatRoomAdapter = ChatRoomAdapter(this)
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
        binding.lifecycleOwner = viewLifecycleOwner
        setLayout()
    }

    private fun setLayout() {
        binding.rvChatRoomList.adapter = chatRoomAdapter
        viewModel.getParticipatingEventList()
        findNavController().navigate(
            ChatRoomListFragmentDirections.actionChatRoomListFragmentToProgressDialogFragment()
        )
        lifecycleScope.launch {
            viewModel.isLoaded
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { isLoaded ->
                    if (isLoaded == false) {
                        findNavController().navigateUp()
                        Toast.makeText(
                            context,
                            getString(R.string.error_message_chat_room_list_loading_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (isLoaded == true) {
                        lifecycleScope.launch {
                            viewModel.latestChatList
                                .flowWithLifecycle(
                                    viewLifecycleOwner.lifecycle,
                                    Lifecycle.State.STARTED
                                )
                                .collectLatest { latestChatList ->
                                    findNavController().navigateUp()
                                    if (latestChatList.isEmpty()) {
                                        binding.guideMessage =
                                            getString(R.string.guide_message_participated_in_no_chat_room)
                                    } else {
                                        binding.guideMessage = ""
                                    }
                                    chatRoomAdapter.submitList(latestChatList)
                                }
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChatRoomClick(postUid: String, postTitle: String) {
        val action = ChatRoomListFragmentDirections.actionChatRoomListFragmentToChatRoomFragment(
            postUid,
            postTitle
        )
        findNavController().navigate(action)
    }
}