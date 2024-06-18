package com.oceantech.tracking.ui.home.fragment

import android.app.DatePickerDialog
import android.os.Build
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
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.databinding.FragmentPersonalInfoBinding
import com.oceantech.tracking.ui.home.viewmodel.HomeViewAction
import com.oceantech.tracking.ui.home.viewmodel.HomeViewModel
import com.oceantech.tracking.utils.format
import com.oceantech.tracking.utils.toLocalDate
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class PersonalInfoFragment @Inject constructor() :
    TrackingBaseFragment<FragmentPersonalInfoBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    private var user: UserDto? = null
    private var dob: Date? = null
        set(value) {
            value?.toLocalDate()?.let {
                views.edtDob.setText(
                    String.format("%02d/%02d/%d", it.dayOfMonth, it.month.value, it.year)
                )
            }
            field = value
        }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonalInfoBinding {
        return FragmentPersonalInfoBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.tvUploadImage.setOnClickListener {
            // Todo handle upload image
        }

        views.btnDOB.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (dob == null) dob = user?.dob

                val datePickerDialog = DatePickerDialog(requireContext())
                dob?.toLocalDate()?.let { ld ->
                    datePickerDialog.updateDate(ld.year, ld.month.value - 1, ld.dayOfMonth)
                }
                datePickerDialog.setOnDateSetListener { datePicker, _, _, _ ->
                    val calendar = Calendar.getInstance()
                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth, 0, 0)
                    dob = Date.from(calendar.toInstant())
                }
                datePickerDialog.show()
            }
        }

        views.save.setOnClickListener {
            updateMyself()
        }
    }

    private fun updateMyself() {
        val firstName = views.edtFirstName.text.toString().trim()
        val lastName = views.edtLastName.text.toString().trim()
        val displayName = views.edtDisplayName.text.toString().trim()
        val gender = when (views.rgGender.checkedRadioButtonId) {
            R.id.rBtnMale -> "M"
            R.id.rBtnFemale -> "F"
            else -> null
        }
        val birthPlace = views.edtBirthPlace.text.toString().trim()
        val university = views.edtUniversity.text.toString().trim()
        val year = views.edtYear.text.toString().trim().toIntOrNull()

        if (displayName.isNullOrEmpty()) {
            views.edtDisplayName.error = getString(R.string.username_not_empty)
            return
        }

        // Todo handle if upload image
        user?.let {
            viewModel.handle(
                HomeViewAction.UpdateMyself(
                    it.toReq().copy(
                        firstName = firstName.ifEmpty { it.firstName },
                        lastName = lastName.ifEmpty { it.lastName },
                        displayName = displayName,
                        gender = gender ?: it.gender,
                        dob = this.dob ?: it.dob,
                        birthPlace = birthPlace.ifEmpty { it.birthPlace },
                        university = university.ifEmpty { it.university },
                        year = year ?: it.year
                    )
                )
            )
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.userCurrent) {
            is Success -> {
                it.userCurrent.invoke().let { user ->
                    this.user = user
                    bind(user)
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: ${it.userCurrent.error}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Test", "invalidate: ${it.userCurrent}")
            }
        }

        when (it.asyncUpdateMyself) {
            is Success -> {
                Toast.makeText(requireContext(), "Update successful!!!", Toast.LENGTH_SHORT).show()
                viewModel.handle(HomeViewAction.GetCurrentUser)
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: ${it.asyncUpdateMyself.error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Test", "invalidate: ${it.asyncUpdateMyself}")
            }
        }
    }

    private fun bind(user: UserDto) {
        if (user.hasPhoto == true) {
            // Todo handle show image
        } else {
            views.image.setImageResource(R.drawable.ic_person)
        }

        views.edtFirstName.setText(user.firstName ?: "")
        views.edtLastName.setText(user.lastName ?: "")
        views.edtDisplayName.setText(user.displayName)

        if (user.gender == "M") {
            views.rBtnMale.isChecked = true
        } else if (user.gender == "F") {
            views.rBtnFemale.isChecked = true
        }

        user.dob?.let { dob ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                views.edtDob.setText(dob.format(null))
            } else {
                views.edtDob.setText(dob.toLocalDate().format(pattern = "dd/MM/yyyy"))
            }
        }

        views.edtBirthPlace.setText(user.birthPlace)
        views.edtUniversity.setText(user.university)
        views.edtYear.setText(user.year?.toString() ?: "")

        views.edtUsername.setText(user.username)
        views.edtEmail.setText(user.email)
        views.checkinDays.text = String.format(
            getString(R.string.txtCountDays),
            user.countDayCheckin ?: 0,
            user.countDayTracking ?: 0
        )
    }

}