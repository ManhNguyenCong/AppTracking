package com.oceantech.tracking.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentNotificationsBinding
import javax.inject.Inject

class NotificationsFragment @Inject constructor() :
    TrackingBaseFragment<FragmentNotificationsBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    private lateinit var adapter: NotificationAdapter

    private var toolbar: Toolbar? = null


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationsBinding {
        return FragmentNotificationsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NotificationAdapter()
        views.rvNotifications.adapter = adapter

        viewModel.handle(HomeViewAction.GetNotificationsByUser)

        toolbar = activity?.findViewById(R.id.toolbar)
        toolbar?.menu?.findItem(R.id.menu_notification)?.isVisible = false
    }

    override fun onDestroy() {
        toolbar?.menu?.findItem(R.id.menu_notification)?.isVisible = true
        super.onDestroy()
    }

    override fun invalidate(): Unit = withState(viewModel) {

        when (it.asyncNotifications) {
            is Success -> {
                it.asyncNotifications.invoke().let { notifications ->
                    if (notifications.isEmpty()) {
                        views.rvNotifications.visibility = View.GONE
                        views.tvNoNotification.visibility = View.VISIBLE
                        return@let
                    }

                    views.rvNotifications.visibility = View.VISIBLE
                    views.tvNoNotification.visibility = View.GONE
                    adapter.submitList(notifications)
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: ${it.asyncNotifications.error}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("Test", "invalidate: " + it.asyncNotifications.toString())
            }
        }

    }
}