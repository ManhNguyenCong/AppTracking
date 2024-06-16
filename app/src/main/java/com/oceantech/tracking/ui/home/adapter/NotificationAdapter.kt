package com.oceantech.tracking.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.NotificationDto
import com.oceantech.tracking.databinding.ItemNotificationsBinding
import com.oceantech.tracking.utils.format

class NotificationAdapter: ListAdapter<NotificationDto, NotificationAdapter.NotificationViewHolder>(
    DiffCallback
) {
    class NotificationViewHolder(private val binding: ItemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationDto) {
            binding.title.text = notification.title ?: "Notification title"
            binding.content.text = notification.body ?: "Notification content"
            binding.datetime.text = notification.date?.format() ?: "__:__, __/__/____"
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<NotificationDto>() {
            override fun areItemsTheSame(
                oldItem: NotificationDto,
                newItem: NotificationDto
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: NotificationDto,
                newItem: NotificationDto
            ): Boolean {
                return oldItem.date == newItem.date && oldItem.title == newItem.title
                        && oldItem.body == newItem.body && oldItem.type == newItem.type
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}