package com.oceantech.tracking.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.TrackingDtoReq
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.home.adapter.UserAdapter
import com.oceantech.tracking.ui.home.viewmodel.HomeViewAction
import com.oceantech.tracking.ui.home.viewmodel.HomeViewEvent
import com.oceantech.tracking.ui.home.viewmodel.HomeViewModel
import com.oceantech.tracking.ui.home.viewmodel.HomeViewState
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.utils.toLocalDate
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class HomeFragment @Inject constructor() : TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    private var currentUser: UserDto? = null
    private var todayTrackingId: Int? = null

    private lateinit var adapter: UserAdapter
    private var pageIndex = 0

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

        adapter = UserAdapter(setTvGender = { tv, gender ->
            tv.text = when (gender) {
                "M" -> getString(R.string.male)
                "F" -> getString(R.string.female)
                else -> getString(R.string.unknown)
            }
        }, setTvCountDays = { tv, checkinDays, trackingDays ->
            tv.text = String.format(
                getString(R.string.txtCountDays), checkinDays ?: 0, trackingDays ?: 0
            )
        }, setOnItemClick = { userId ->
            val action = HomeFragmentDirections.actionNavHomeFragmentToDetailUserFragment(userId)
            findNavController().navigate(action)
        })
        views.rvUsers.adapter = adapter
        views.btnPrevious.setOnClickListener {
            viewModel.handle(HomeViewAction.SearchByPage(SearchDto("", --pageIndex + 1, 10, 0)))
        }
        views.btnNext.setOnClickListener {
            viewModel.handle(HomeViewAction.SearchByPage(SearchDto("", ++pageIndex + 1, 10, 0)))
        }


        viewModel.observeViewEvents {
            handleEvent(it)
        }

        viewModel.handle(HomeViewAction.GetCurrentUser)
        // Get today tracking
        viewModel.handle(HomeViewAction.GetAllTrackingByUser)
        // Get today check in
        viewModel.handle(HomeViewAction.GetTimeSheetsByUser)
        // Get users
        viewModel.handle(HomeViewAction.SearchByPage(SearchDto("", pageIndex + 1, 10, 0)))
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
                views.tvWelcome.text =
                    String.format(getString(R.string.welcome), currentUser?.displayName ?: "")
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
                val session = SessionManager(requireContext())
                session.clearToken()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                activity?.finish()
            }

            is Fail -> {
                Log.e("Test Tracking", "invalidate: asyncLogout: ${it.asyncLogout}")
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.asyncLogout.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        checkinState(it)
        trackingState(it)

        when (it.userCurrent) {
            is Success -> {
                it.userCurrent.invoke().let { user ->
                    currentUser = user
                    views.tvWelcome.text = String.format(getString(R.string.welcome),
                        user.displayName?.ifEmpty { user.username })
                }
            }

            is Fail -> {
                Log.e("Test Tracking", "invalidate: userCurrent: ${it.userCurrent}")
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.userCurrent.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (it.asyncPage) {
            is Success -> {
                it.asyncPage.invoke().let { page ->
                    views.pageLayout.visibility = if (page.empty) View.GONE else View.VISIBLE
                    views.btnPrevious.visibility = if (page.first) View.GONE else View.VISIBLE
                    views.btnNext.visibility = if (page.last) View.GONE else View.VISIBLE
                    views.pageNumber.text = "${page.number + 1}/${page.totalPages}"

                    adapter.submitList(page.content)
                    Log.d("Test Tracking", "invalidate: ${page.content.joinToString("\n")}")
                }
            }

            is Fail -> {
                Log.e("Test Tracking", "invalidate: " + it.asyncPage.toString())
                Toast.makeText(requireContext(), "Can't load list users!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun trackingState(state: HomeViewState) {
        when (state.trackings) {
            is Success -> {
                state.trackings.invoke().let { trackings ->
                    if (trackings.isEmpty()) return@let

                    val now = Date.from(Instant.now()).toLocalDate()

                    val recentTracking = trackings.last()
                    todayTrackingId = recentTracking.id
                    val dateTracking = recentTracking.date?.toLocalDate()

                    if (dateTracking == now) {
                        views.btnTracking.text = "Update Tracking"
                        if (views.tracking.text.toString().isEmpty()) {
                            views.tracking.setText(recentTracking.content)
                        }
                    }
                }
            }

            is Fail -> {
                Log.e("Test Tracking", "invalidate: trackings: ${state.trackings}")
            }
        }

        when (state.asyncUpdateTracking) {
            is Success -> {
                Toast.makeText(
                    requireContext(), "Update Tracking Successful!", Toast.LENGTH_SHORT
                ).show()
                viewModel.handle(HomeViewAction.GetAllTrackingByUser)
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: " + state.asyncUpdateTracking.error.message,
                    Toast.LENGTH_SHORT
                ).show()

                Log.e(
                    "Test Tracking", "invalidate: asyncUpdateTracking: ${state.asyncUpdateTracking}"
                )
            }
        }

        when (state.asyncSaveTracking) {
            is Success -> {
                state.asyncSaveTracking.invoke().let { tracking ->
                    views.btnTracking.text = "Update Tracking"
                    todayTrackingId = tracking.id
                    Toast.makeText(
                        requireContext(), "Save Tracking Successful!", Toast.LENGTH_SHORT
                    ).show()
                    viewModel.handle(HomeViewAction.GetAllTrackingByUser)
                }
            }

            is Fail -> {
                Log.e(
                    "Test Tracking", "invalidate: asyncSaveTracking: ${state.asyncSaveTracking}"
                )
                Toast.makeText(
                    requireContext(),
                    "Has error: " + state.asyncSaveTracking.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkinState(state: HomeViewState) {
        when (state.asyncCheckIn) {
            is Success -> {
                state.asyncCheckIn.invoke().let { timeSheet ->
                    Toast.makeText(requireContext(), timeSheet.message, Toast.LENGTH_SHORT).show()
                    viewModel.handle(HomeViewAction.GetTimeSheetsByUser)
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: " + state.asyncCheckIn.error.message,
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Test Tracking", "invalidate: asyncCheckIn: ${state.asyncCheckIn}")
            }
        }

        when (state.timeSheets) {
            is Success -> {
                state.timeSheets.invoke().let { timeSheets ->
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
                Log.e("Test Tracking", "invalidate: timeSheets: ${state.timeSheets}")
                Toast.makeText(
                    requireContext(),
                    "Has error: " + state.timeSheets.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}