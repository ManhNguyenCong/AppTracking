package com.oceantech.tracking.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.databinding.ItemUsersListBinding

class UserAdapter(
    private val setTvGender: (TextView, String?) -> Unit,
    private val setTvCountDays: (TextView, Int?, Int?) -> Unit,
    private val setOnItemClick: (Int) -> Unit
): ListAdapter<UserDto, UserAdapter.UserViewHolder>(DiffCallback) {
    class UserViewHolder(private val binding: ItemUsersListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDto, setTvGender: (TextView, String?) -> Unit, setTvCountDays: (TextView, Int?, Int?) -> Unit) {
            binding.displayName.text = user.displayName
            setTvGender(binding.gender, user.gender)
            setTvCountDays(binding.checkinDays, user.countDayCheckin, user.countDayTracking)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemUsersListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            setOnItemClick(current.id!!)
        }
        holder.bind(current, setTvGender, setTvCountDays)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<UserDto>() {
            override fun areItemsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
                return oldItem.firstName == newItem.firstName &&
                        oldItem.lastName == newItem.lastName &&
                        oldItem.displayName == newItem.displayName &&
                        oldItem.gender == newItem.gender &&
                        oldItem.dob == newItem.dob &&
                        oldItem.birthPlace == newItem.birthPlace &&
                        oldItem.hasPhoto == newItem.hasPhoto &&
                        oldItem.image == newItem.image &&
                        oldItem.email == newItem.email &&
                        oldItem.university == newItem.university &&
                        oldItem.countDayCheckin == newItem.countDayCheckin &&
                        oldItem.countDayTracking == newItem.countDayTracking &&
                        oldItem.active == newItem.active &&
                        oldItem.tokenDevice == newItem.tokenDevice &&
                        oldItem.year == newItem.year
            }

        }
    }
}