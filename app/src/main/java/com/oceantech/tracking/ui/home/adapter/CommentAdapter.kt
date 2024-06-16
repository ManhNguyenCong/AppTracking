package com.oceantech.tracking.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.CommentsDto
import com.oceantech.tracking.databinding.ItemCommentsBinding
import com.oceantech.tracking.utils.format

class CommentAdapter: ListAdapter<CommentsDto, CommentAdapter.CommentViewHolder>(DiffCallback) {
    class CommentViewHolder(private val binding: ItemCommentsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentsDto) {
            binding.displayName.text = comment.user?.displayName ?: "Unknown"
            binding.comment.text = comment.content ?: "Unknown"
            if (comment.user?.hasPhoto == true) {
                // TOdo load image
            } else {
                binding.image.setImageResource(R.drawable.ic_person)
            }
            binding.datetime.text = comment.date?.format() ?: "Unknown"
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CommentsDto>() {
            override fun areItemsTheSame(oldItem: CommentsDto, newItem: CommentsDto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CommentsDto, newItem: CommentsDto): Boolean {
                return oldItem.content == newItem.content &&
                        oldItem.date == newItem.date &&
                        oldItem.user == newItem.user &&
                        oldItem.post == newItem.post
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}