package com.oceantech.tracking.ui.security.fragment

import android.app.DatePickerDialog
import android.os.Build
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
import com.oceantech.tracking.data.model.UserDtoReq
import com.oceantech.tracking.data.model.UserDtoRes
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.ui.security.viewmodel.SecurityViewAction
import com.oceantech.tracking.ui.security.viewmodel.SecurityViewModel
import com.oceantech.tracking.utils.toLocalDate
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


class SigninFragment @Inject constructor() : TrackingBaseFragment<FragmentSigninBinding>() {

    private val viewModel: SecurityViewModel by activityViewModel()

    private var dob: Date? = null

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.send.setOnClickListener {
            submitSignIn()
        }

        views.btnDOB.isEnabled = false
        views.btnDOB.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val datePicker = DatePickerDialog(requireContext())

                dob?.toLocalDate()?.let {
                    datePicker.updateDate(it.year, it.month.value - 1, it.dayOfMonth)
                }

                datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                    views.dob.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year))

                    val instant = Calendar.getInstance()
                    instant.set(year, month, dayOfMonth, 0, 0, 0)
                    dob = Date.from(instant.toInstant())
                }
                datePicker.show()
            }
        }
    }

    private fun submitSignIn() {
        val lastName = views.lastName.text.toString().trim()
        val firstName = views.firstName.text.toString().trim()
        val username = views.username.text.toString().trim()
        val password = views.password.text.toString().trim()
        val passwordConfirm = views.passwordConfirm.text.toString().trim()
        val email = views.email.text.toString().trim()
        val gender = if (views.rgGender.checkedRadioButtonId == R.id.rBtnMale) "M" else "F"

        if (validateSignIn(username, password, passwordConfirm, email)) {
            val userReq = UserDtoReq(
                username = username,
                displayName = firstName.ifEmpty { username },
                gender = gender,
                dob = dob,
                email = email,
                password = password,
                confirmPassword = passwordConfirm,
                firstName = firstName,
                lastName = lastName,
                university = views.university.text.toString().trim(),
                year = views.year.text.toString().trim().toIntOrNull()
            )
            Log.d("Test Tracking", "submitSignIn: $userReq")
            viewModel.handle(SecurityViewAction.SignInAction(userReq))
        }
    }

    private fun validateSignIn(username: String?, password: String?, passwordConfirm: String?, email: String?): Boolean {
        if (username.isNullOrEmpty()) {
            views.username.error = getString(R.string.username_not_empty)
        }
        if (password.isNullOrEmpty()) {
            views.password.error = getString(R.string.username_not_empty)
        }
        if (passwordConfirm.isNullOrEmpty() || passwordConfirm != password) {
            views.passwordConfirm.error = getString(R.string.username_not_empty)
        }
        if (email.isNullOrEmpty()) {
            views.email.error = getString(R.string.username_not_empty)
        }
//        if (dob == null) {
//            Toast.makeText(requireContext(), getString(R.string.enter_birthday), Toast.LENGTH_SHORT)
//                .show()
//        }

        return !username.isNullOrEmpty() && !password.isNullOrEmpty()
                && !passwordConfirm.isNullOrEmpty() && passwordConfirm == password
                && !email.isNullOrEmpty()
//                && dob != null
    }

    override fun invalidate(): Unit = withState(viewModel) {
        Log.d("Test Tracking", "invalidate: " + it.asyncSignIn.toString())
        when (it.asyncSignIn) {
            is Success -> {
                Toast.makeText(requireContext(), "Sign in successful!!!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            is Fail -> {
                val error = (it.asyncSignIn as Fail<UserDtoRes>).error.message
                Toast.makeText(requireContext(), "Fail: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}