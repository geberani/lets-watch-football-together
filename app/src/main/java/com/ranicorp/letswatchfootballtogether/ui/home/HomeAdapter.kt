package com.ranicorp.letswatchfootballtogether.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ranicorp.letswatchfootballtogether.data.model.*
import com.ranicorp.letswatchfootballtogether.databinding.ItemAllPostSectionBinding
import com.ranicorp.letswatchfootballtogether.databinding.ItemHomeHeaderBinding
import com.ranicorp.letswatchfootballtogether.databinding.ItemNewPostSectionBinding
import com.ranicorp.letswatchfootballtogether.databinding.ItemPopularPostSectionBinding
import com.ranicorp.letswatchfootballtogether.ui.common.PostClickListener

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_NEW_POST = 1
private const val VIEW_TYPE_POPULAR_POST = 2
private const val VIEW_TYPE_ALL_POST = 3


class HomeAdapter(private val clickListener: PostClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<HomeItem>()
    private var allPostsList = listOf<Post>()
    private var newPostsList = listOf<Post>()
    private var popularPostsList = listOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HomeHeaderViewHolder.from(parent)
            VIEW_TYPE_NEW_POST -> HomeNewPostSectionViewHolder.from(parent, clickListener)
            VIEW_TYPE_POPULAR_POST -> HomePopularPostSectionViewHolder.from(parent, clickListener)
            else -> HomeAllPostSectionViewHolder.from(parent, clickListener)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeHeaderViewHolder -> {
                val item = items[position] as HomeHeader
                holder.bind(item)
            }
            is HomeNewPostSectionViewHolder -> {
                val item = items[position] as HomeNewPost
                holder.bind(item)
            }
            is HomePopularPostSectionViewHolder -> {
                val item = items[position] as HomePopularPost
                holder.bind(item)
            }
            is HomeAllPostSectionViewHolder -> {
                val item = items[position] as HomeAllPost
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeHeader -> VIEW_TYPE_HEADER
            is HomeNewPost -> VIEW_TYPE_NEW_POST
            is HomePopularPost -> VIEW_TYPE_POPULAR_POST
            is HomeAllPost -> VIEW_TYPE_ALL_POST
        }
    }

    fun submitAllPostList(
        Posts: List<Post>,
        newPostsHeader: String,
        popularPostsHeader: String,
        allPostsHeader: String
    ) {
        newPostsList = Posts.sortedByDescending { it.createdTimeMillis }.take(10)
        popularPostsList =
            Posts.sortedByDescending { it.currentParticipants }.take(10)
        allPostsList = Posts
        items.add(HomeHeader(newPostsHeader))
        items.add(HomeNewPost(newPostsList))
        items.add(HomeHeader(popularPostsHeader))
        items.add(HomePopularPost(popularPostsList))
        items.add(HomeHeader(allPostsHeader))
        items.add(HomeAllPost(allPostsList))
        notifyDataSetChanged()
    }

    class HomeHeaderViewHolder(private val binding: ItemHomeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeHeader) {
            binding.header = item.header
        }

        companion object {
            fun from(parent: ViewGroup): HomeHeaderViewHolder {
                return HomeHeaderViewHolder(
                    ItemHomeHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class HomeNewPostSectionViewHolder(
        private val binding: ItemNewPostSectionBinding,
        clickListener: PostClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val nestedAdapter = NewPostAdapter(clickListener)

        init {
            binding.rvNewPostSection.adapter = nestedAdapter
        }

        fun bind(item: HomeNewPost) {
            nestedAdapter.submitList(item.posts)
        }

        companion object {
            fun from(
                parent: ViewGroup,
                clickListener: PostClickListener
            ): HomeNewPostSectionViewHolder {
                return HomeNewPostSectionViewHolder(
                    ItemNewPostSectionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    clickListener
                )
            }
        }
    }

    class HomePopularPostSectionViewHolder(
        private val binding: ItemPopularPostSectionBinding,
        clickListener: PostClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val nestedAdapter = PopularPostAdapter(clickListener)

        init {
            binding.rvPopularPostSection.adapter = nestedAdapter
        }

        fun bind(item: HomePopularPost) {
            nestedAdapter.submitList(item.posts)
        }

        companion object {
            fun from(
                parent: ViewGroup,
                clickListener: PostClickListener
            ): HomePopularPostSectionViewHolder {
                return HomePopularPostSectionViewHolder(
                    ItemPopularPostSectionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    clickListener
                )
            }
        }
    }

    class HomeAllPostSectionViewHolder(
        private val binding: ItemAllPostSectionBinding,
        clickListener: PostClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val nestedAdapter = AllPostAdapter(clickListener)

        init {
            binding.rvAllPostSection.adapter = nestedAdapter
        }

        fun bind(item: HomeAllPost) {
            nestedAdapter.submitList(item.posts)
        }

        companion object {
            fun from(
                parent: ViewGroup,
                clickListener: PostClickListener
            ): HomeAllPostSectionViewHolder {
                return HomeAllPostSectionViewHolder(
                    ItemAllPostSectionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    clickListener
                )
            }
        }
    }
}