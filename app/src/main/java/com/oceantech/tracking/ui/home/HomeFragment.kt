package com.oceantech.tracking.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.NotificationDto
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.TrackingDtoReq
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.utils.toLocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class HomeFragment @Inject constructor() : TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    private var currentUser: UserDto? = null
    private var todayTrackingId: Int? = null

    private lateinit var adapter: UserAdapter

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.tvLogout.setOnClickListener {
            viewModel.handle(HomeViewAction.Logout)
        }
        views.btnCheckin.setOnClickListener {
            checkIn()
        }
        views.btnTracking.setOnClickListener {
            if (todayTrackingId == null) {
                tracking()
            } else {
                updateTracking()
            }
        }
        adapter = UserAdapter(
            setTvGender = { tv, gender ->
                tv.text = when(gender) {
                    "M" -> getString(R.string.male)
                    "F" -> getString(R.string.female)
                    else -> getString(R.string.unknown)
                }
            },
            setTvCountDays = { tv, checkinDays, trackingDays ->
                tv.text = String.format(
                    getString(R.string.txtCountDays),
                    checkinDays ?: 0,
                    trackingDays ?: 0
                )
            },
            setOnItemClick = { userId ->
                // TODO set event on item click for list users
            }
        )
        views.rvUsers.adapter = adapter

        viewModel.observeViewEvents {
            handleEvent(it)
        }

        viewModel.handle(HomeViewAction.GetCurrentUser)
        // Get today tracking
        viewModel.handle(HomeViewAction.GetAllTrackingByUser)
        // Get today check in
        viewModel.handle(HomeViewAction.GetTimeSheetsByUser)
        // Get users
        viewModel.handle(HomeViewAction.SearchByPage(SearchDto("", 0, 100, 0)))

//        viewModel.getNotificationsByUser().enqueue(object: Callback<List<NotificationDto>> {
//            override fun onResponse(
//                call: Call<List<NotificationDto>>,
//                response: Response<List<NotificationDto>>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("Test Tracking", "onResponse: " + response.body()?.joinToString("\n"))
//                } else {
//                    Log.e("Test Tracking", "onResponse: " + response.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<List<NotificationDto>>, t: Throwable) {
//                Log.e("Test Tracking", "onResponse: " + t.toString())
//            }
//        })
    }

    private fun checkIn() {
        val ip = viewModel.getLocalIpAddress()

        if (ip.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Can't get local ip address!!!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        viewModel.handle(HomeViewAction.CheckIn(ip))
    }

    private fun tracking() = currentUser?.let {
        views.tracking.error = null
        val tracking = views.tracking.text.toString().trim()

        if (tracking.isNullOrEmpty()) {
            views.tracking.error = getString(R.string.username_not_empty)
            return@let
        }

        viewModel.handle(
            HomeViewAction.SaveTracking(
                TrackingDtoReq(
                    null, currentUser?.toReq(), Date.from(Instant.now()), tracking
                )
            )
        )
    }

    private fun updateTracking() = currentUser?.let {
        views.tracking.error = null
        val tracking = views.tracking.text.toString().trim()

        if (tracking.isNullOrEmpty()) {
            views.tracking.error = getString(R.string.username_not_empty)
            return@let
        }

        viewModel.handle(
            HomeViewAction.UpdateTracking(
                todayTrackingId!!, TrackingDtoReq(
                    null, currentUser?.toReq(), Date.from(Instant.now()), tracking
                )
            )
        )
    }

    private fun handleEvent(it: HomeViewEvent) {
        when (it) {
            is HomeViewEvent.ResetLanguage -> {
                views.tvWelcome.text = String.format(getString(R.string.welcome), currentUser?.displayName ?: "")
                views.tvLogout.text = getString(R.string.logout)
                views.tvUsersTitle.text = getString(R.string.users)
                views.btnCheckin.text = if (views.btnCheckin.isEnabled) {
                    getString(R.string.checkin)
                } else {
                    getString(R.string.checked)
                }
                views.rvUsers.adapter = adapter
            }
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {

        when (it.asyncLogout) {
            is Success -> {
                val sessionManager = context?.let { it1 -> SessionManager(it1.applicationContext) }
                sessionManager?.clearToken()
                viewModel.clearUserPreferences()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                activity?.finish()
            }

            is Fail -> {
                Log.d("Test Tracking", "invalidate: asyncLogout: " + it.asyncLogout.toString())
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.asyncLogout.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (it.asyncCheckIn) {
            is Success -> {
                it.asyncCheckIn.invoke().let { timeSheet ->
                    if (!views.btnCheckin.isEnabled) return@let

                    Toast.makeText(requireContext(), timeSheet.message, Toast.LENGTH_SHORT).show()
                    views.btnCheckin.text = getString(R.string.checked)
                    views.btnCheckin.isEnabled = false
                }
            }

            is Fail -> {
                Log.d("Test Tracking", "invalidate: asyncCheckIn: " + it.asyncCheckIn.toString())
            }
        }

        when (it.asyncSaveTracking) {
            is Success -> {
                it.asyncSaveTracking.invoke().let { tracking ->
                    if (todayTrackingId != null) return@let

                    views.btnTracking.text = "Update Tracking"
                    todayTrackingId = tracking.id
                    Toast.makeText(
                        requireContext(), "Save Tracking Successful!", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            is Fail -> {
                Log.d(
                    "Test Tracking",
                    "invalidate: asyncSaveTracking: " + it.asyncSaveTracking.toString()
                )
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.asyncSaveTracking.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (it.asyncUpdateTracking) {
            is Success -> {
                it.asyncUpdateTracking.invoke().let { trackingRes ->
                    if (trackingRes.content == views.tracking.text.toString()) return@let

                    Toast.makeText(
                        requireContext(), "Update Tracking Successful!", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.asyncUpdateTracking.error.message,
                    Toast.LENGTH_SHORT
                ).show()

                Log.d(
                    "Test Tracking",
                    "invalidate: asyncUpdateTracking: " + it.asyncUpdateTracking.toString()
                )
            }
        }

        when (it.userCurrent) {
            is Success -> {
                it.userCurrent.invoke().let { user ->
                    currentUser = user
                    views.tvWelcome.text =
                        String.format(getString(R.string.welcome), user.displayName?.ifEmpty { user.username })
                }
            }

            is Fail -> {
                Log.d("Test Tracking", "invalidate: userCurrent: " + it.userCurrent.toString())
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.userCurrent.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (it.trackings) {
            is Success -> {
                it.trackings.invoke().let { trackings ->
                    if (!views.tracking.text.isNullOrEmpty()) return@let

                    if (trackings.isEmpty()) return@let

                    val now = Date.from(Instant.now()).toLocalDate()

                    val recentTracking = trackings.last()
                    todayTrackingId = recentTracking.id
                    val dateTracking = recentTracking.date?.toLocalDate()

                    if (dateTracking == now) {
                        views.btnTracking.text = "Update Tracking"
                        views.tracking.setText(recentTracking.content)
                    }
                }
            }

            is Fail -> {
                Log.d("Test Tracking", "invalidate: trackings: " + it.trackings.toString())
            }
        }

        when (it.timeSheets) {
            is Success -> {
                it.timeSheets.invoke().let { timeSheets ->
                    if (!views.btnCheckin.isEnabled) return@let

                    if (timeSheets.isNullOrEmpty()) return@let

                    val recentCheckIn = timeSheets.last().dateAttendance?.toLocalDate()
                    val now = Date.from(Instant.now()).toLocalDate()
                    if (now == recentCheckIn) {
                        views.btnCheckin.isEnabled = false
                        views.btnCheckin.text = getString(R.string.checked)
                    }
                }
            }

            is Fail -> {
                Log.d("Test Tracking", "invalidate: timeSheets: " + it.timeSheets.toString())
            }
        }

        when (it.asyncPage) {
            is Success -> {
                it.asyncPage.invoke().let { page ->
                    adapter.submitList(page.content)
                }
            }

            is Fail -> {
                Log.e("Test Tracking", "invalidate: " + it.asyncPage.toString())
                Toast.makeText(requireContext(), "Can't load list users!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}