package com.oceantech.tracking.ui.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.CommentsDto
import com.oceantech.tracking.data.model.CommentsDtoReq
import com.oceantech.tracking.data.model.LikesDto
import com.oceantech.tracking.data.model.LikesDtoReq
import com.oceantech.tracking.data.model.PostsDto
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.databinding.ItemBlogsBinding
import com.oceantech.tracking.utils.format
import java.time.Instant
import java.util.Date

class PostAdapter(
    private val context: Context,
    private val checkLiked: (List<UserDto>) -> Boolean,
    private val onBtnLikeClick: (Int, LikesDtoReq) -> Unit,
    private val showLikes: (List<LikesDto>) -> Unit,
    private val postComment: (Int, CommentsDtoReq) -> Unit,
    private val onMediaClick: () -> Unit
) : ListAdapter<PostsDto, PostAdapter.PostViewModel>(DiffCallback) {
    class PostViewModel(private val binding: ItemBlogsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            context: Context,
            post: PostsDto,
            checkLiked: (List<UserDto>) -> Boolean,
            onBtnLikeClick: (Int, LikesDtoReq) -> Unit,
            showLiked: (List<LikesDto>) -> Unit,
            postComment: (Int, CommentsDtoReq) -> Unit,
            onMediaClick: () -> Unit
        ) {
            post.user.let {
                binding.displayName.text = it?.displayName ?: context.getString(R.string.unknown)
                if (it?.hasPhoto == true) {
                    // TODO load image
                } else {
                    binding.image.setImageResource(R.drawable.ic_person)
                }
            }

            binding.dateTime.text = post.date?.format() ?: context.getString(R.string.unknown)
            binding.content.text = post.content ?: context.getString(R.string.unknown)
            if (!post.media.isNullOrEmpty()) {
                // Todo set media
                binding.mediaLayout.visibility = View.VISIBLE
                binding.mediaLayout.setOnClickListener { onMediaClick() }
            } else {
                binding.mediaLayout.visibility = View.GONE
            }

            binding.btnLike.text =
                if (post.likes?.mapNotNull { it.user }?.let { checkLiked(it) } == true) {
                    context.getString(R.string.liked)
                } else {
                    binding.btnLike.setOnClickListener {
                        onBtnLikeClick(
                            post.id!!,
                            LikesDtoReq(
                                0,
                                Date.from(Instant.now()),
                                0,
                                null,
                                post.toReq()
                            )
                        )
                    }
                    context.getString(R.string.like)
                }
            bindLikes(context, post.likes, showLiked)

            bindComments(context, post.comments)

            binding.btnComment.setOnClickListener {
                binding.postCommentLayout.visibility = View.VISIBLE
            }

            binding.btnPostComment.setOnClickListener {
                binding.comment.error = null
                val comment = binding.comment.text?.toString()

                if (comment.isNullOrEmpty()) {
                    binding.comment.error = ""
                } else {
                    postComment(
                        post.id!!, CommentsDtoReq(
                            0,
                            content = comment,
                            date = Date.from(Instant.now()),
                            post = post.toReq(),
                            user = null
                        )
                    )
                    binding.postCommentLayout.visibility = View.GONE
                }
            }
        }

        private fun bindComments(context: Context, comments: List<CommentsDto>?) {
            binding.showMore.visibility = View.GONE
            binding.commentNum.text = if (comments.isNullOrEmpty()) {
                String.format(context.getString(R.string.count_comment), 0)
            } else if (comments.size == 1) {
                String.format(context.getString(R.string.count_comment), 1)
            } else {
                String.format(context.getString(R.string.count_comments), comments.size)
            }

            val adapter = CommentAdapter()
            binding.rvComments.adapter = adapter

            if (comments.isNullOrEmpty()) {
                adapter.submitList(null)
                binding.tvNoComments.visibility = View.VISIBLE
                return
            }

            binding.tvNoComments.visibility = View.GONE

            if (comments.size <= 2) {
                adapter.submitList(comments)
                binding.showMore.visibility = View.GONE
                return
            }

            adapter.submitList(comments.subList(0, 2))
            var isFull = false
            binding.showMore.visibility = View.VISIBLE
            binding.showMore.setOnClickListener {
                if (isFull) {
                    binding.showMore.text = context.getString(R.string.show_more)
                    adapter.submitList(comments.subList(0, 2))
                } else {
                    binding.showMore.text = context.getString(R.string.show_less)
                    adapter.submitList(comments)
                }
                isFull = !isFull
            }
        }

        private fun bindLikes(context: Context, likes: List<LikesDto>?, showLiked: (List<LikesDto>) -> Unit) {
            if (likes.isNullOrEmpty()) {
                binding.likeNum.text = String.format(context.getString(R.string.count_like), 0)
                return
            }

            binding.likeNum.text = if (likes.size == 1) {
                String.format(context.getString(R.string.count_like), 1)
            } else {
                String.format(context.getString(R.string.count_likes), likes.size)
            }
            Log.d("Test", "bindLikes: " + likes.joinToString("\n") { it.user.toString() })
            binding.likeNum.setOnClickListener { showLiked(likes) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PostsDto>() {
            override fun areItemsTheSame(oldItem: PostsDto, newItem: PostsDto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PostsDto, newItem: PostsDto): Boolean {
                return oldItem.content == newItem.content && oldItem.user == newItem.user && oldItem.date == newItem.date && oldItem.comments?.joinToString() == newItem.comments?.joinToString() && oldItem.likes?.joinToString() == newItem.likes?.joinToString() && oldItem.media?.joinToString() == newItem.media?.joinToString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewModel {
        return PostViewModel(
            ItemBlogsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewModel, position: Int) {
        val current = getItem(position)
        holder.bind(
            context,
            current,
            checkLiked,
            onBtnLikeClick,
            showLikes,
            postComment,
            onMediaClick
        )
    }
}