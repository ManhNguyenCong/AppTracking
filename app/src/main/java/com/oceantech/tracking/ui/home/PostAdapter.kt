package com.oceantech.tracking.ui.home

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
import com.oceantech.tracking.data.model.PostsDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.databinding.ItemBlogsBinding
import com.oceantech.tracking.utils.format
import java.time.Instant
import java.util.Date

class PostAdapter(
    private val onBtnLikeClick: () -> Unit,
    private val postComment: (Int, CommentsDtoReq) -> Unit,
    private val showLikes: () -> Unit,
    private val onMediaClick: () -> Unit
) : ListAdapter<PostsDto, PostAdapter.PostViewModel>(DiffCallback) {
    class PostViewModel(private val binding: ItemBlogsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            post: PostsDto,
            onBtnLikeClick: () -> Unit,
            postComment: (Int, CommentsDtoReq) -> Unit,
            showLiked: () -> Unit,
            onMediaClick: () -> Unit
        ) {
            post.user.let {
                binding.displayName.text = it?.displayName ?: "Unknown"
                if(it?.hasPhoto == true) {
                    // TODO load image
                } else {
                    binding.image.setImageResource(R.drawable.ic_person)
                }
            }

            binding.dateTime.text = post.date?.format() ?: "Unknown"
            binding.content.text = post.content ?: "Unknown"
            if (!post.media.isNullOrEmpty()) {
                // Todo set media
                binding.mediaLayout.visibility = View.VISIBLE
                binding.mediaLayout.setOnClickListener { onMediaClick() }
            } else {
                binding.mediaLayout.visibility = View.GONE
            }

            bindLikes(post.likes, showLiked)
            bindComments(post.comments)

            binding.btnLike.setOnClickListener {
                onBtnLikeClick()
            }
            binding.btnComment.setOnClickListener {
                binding.postCommentLayout.visibility = View.VISIBLE
            }

            binding.btnPostComment.setOnClickListener {
                binding.comment.error = null
                val comment = binding.comment.text?.toString()

                if (comment.isNullOrEmpty()) {
                    binding.comment.error = ""
                } else {

//                    postComment(
//                        post.id!!,
//                        CommentsDtoReq(
//                            0,
//                            content = comment,
//                            date = Date.from(Instant.now()),
//                            post = post.toReq(),
//                            user = null
//                        )
//                    )
                    binding.postCommentLayout.visibility = View.VISIBLE
                    binding.postCommentLayout.visibility = View.GONE
                }
            }
        }

        private fun bindComments(comments: List<CommentsDto>?) {
            Log.d("Test", "bindComments: " + comments?.size)
            binding.commentNum.text = if (comments.isNullOrEmpty()) {
                "0 comment"
            } else if (comments.size == 1) {
                "1 comment"
            } else {
                "${comments.size} comments"
            }

            if (comments.isNullOrEmpty()) {
                // TODO handle no comment
                return
            }

            binding.commentNum.setOnClickListener {
                binding.commentsLayout.visibility = View.VISIBLE
            }

            val adapter = CommentAdapter()
            binding.rvComments.adapter = adapter

            if (comments.size <= 3) {
                adapter.submitList(comments)
                binding.showMore.visibility = View.GONE
                return
            }

            adapter.submitList(comments.subList(0, 2))
            var isFull = false
            binding.showMore.visibility = View.VISIBLE
            binding.showMore.setOnClickListener {
                if (isFull) {
                    binding.showMore.text = "Show more"
                    adapter.submitList(comments.subList(0, 2))
                } else {
                    binding.showMore.text = "Show less"
                    adapter.submitList(comments)
                }
                isFull = !isFull
            }
        }

        private fun bindLikes(likes: List<LikesDto>?, showLiked: () -> Unit) {
            if (likes.isNullOrEmpty()) {
                binding.likeNum.text = "0 like"
                return
            }

            binding.likeNum.text = if (likes.size == 1) {
                "1 like"
            } else {
                "${likes.size} likes"
            }

            binding.likeNum.setOnClickListener { showLiked() }
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
        holder.bind(
            getItem(position), onBtnLikeClick, postComment, showLikes, onMediaClick
        )
    }
}