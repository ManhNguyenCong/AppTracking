package com.oceantech.tracking.ui.home.fragment

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.data.model.toUserDto
import com.oceantech.tracking.databinding.FragmentDetailUserBinding
import com.oceantech.tracking.ui.home.viewmodel.HomeViewAction
import com.oceantech.tracking.ui.home.viewmodel.HomeViewModel
import com.oceantech.tracking.utils.format
import com.oceantech.tracking.utils.toLocalDate
import java.util.Calendar
import java.util.Date

class DetailUserFragment : TrackingBaseFragment<FragmentDetailUserBinding>() {

    private val args: DetailUserFragmentArgs by navArgs()

    private val viewModel: HomeViewModel by activityViewModel()

    private var toolbar: Toolbar? = null
    private var user: UserDto? = null
    private var dob: Date? = null

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailUserBinding {
        return FragmentDetailUserBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = activity?.findViewById(R.id.toolbar)
        toolbar?.menu?.findItem(R.id.menu_notification)?.isVisible = false

        views.tvEdit.setOnClickListener {
            views.btnSave.visibility = View.VISIBLE

            views.edtFirstName.isEnabled = true
            views.edtLastName.isEnabled = true
            views.edtDisplayName.isEnabled = true
            views.edtBirthPlace.isEnabled = true
            views.edtUniversity.isEnabled = true
            views.edtYear.isEnabled = true
            views.rBtnMale.isEnabled = true
            views.rBtnFemale.isEnabled = true
            views.btnDOB.isEnabled = true
        }

        views.tvBlock.setOnClickListener {
            user?.let { viewModel.handle(HomeViewAction.Block(it.id!!)) }
        }

        views.btnDOB.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (dob == null) dob = user?.dob ?: return@setOnClickListener

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
        views.btnSave.setOnClickListener {
            update()
        }

        viewModel.handle(HomeViewAction.SetNavUp)
        viewModel.handle(HomeViewAction.GetUserById(args.id))
    }

    private fun update() {
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

        user?.let {
            viewModel.handle(
                HomeViewAction.Edit(
                    it.id!!,
                    it.toReq().copy(
                        firstName = firstName.ifEmpty { it.firstName },
                        lastName = lastName.ifEmpty { it.lastName },
                        displayName = displayName,
                        gender = gender ?: it.gender,
                        dob = dob ?: it.dob,
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
                    val isUser = user.roles?.get(0)?.id == 4

                    views.tvUploadImage.visibility = if (isUser) View.GONE else View.VISIBLE
                    views.tvEdit.visibility = if (isUser) View.GONE else View.VISIBLE
                    views.tvBlock.visibility = if (isUser) View.GONE else View.VISIBLE
                }
            }

            is Fail -> {
                // TODO handle when no current user
                Log.e("Test", "invalidate: currentUser: ${it.userCurrent}")
                findNavController().navigateUp()
            }
        }

        when (it.asyncUser) {
            is Success -> {
                with(it.asyncUser.invoke()) {
                    user = this.toUserDto()

                    if (hasPhoto == true) {
                        // TODO load image
                    } else {
                        views.image.setImageResource(R.drawable.ic_person)
                    }
                    views.edtFirstName.setText(firstName)
                    views.edtLastName.setText(lastName)
                    views.edtDisplayName.setText(displayName)
                    views.rgGender.check(
                        when (gender) {
                            "M" -> R.id.rBtnMale
                            "F" -> R.id.rBtnFemale
                            else -> -1
                        }
                    )
                    views.edtDob.setText(dob?.toLocalDate()?.format("dd/MM/yyyy") ?: "../../....")
                    views.edtBirthPlace.setText(birthPlace)
                    views.edtUniversity.setText(university)
                    views.edtYear.setText(year?.toString())
                    views.edtUsername.setText(username)
                    views.edtEmail.setText(email)
                }
            }

            is Fail -> {
                Log.e("Test Tracking", "invalidate: UserByID:  ${it.asyncUser}")
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.asyncUser.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (it.asyncBlock) {
            is Success -> {
                Toast.makeText(requireContext(), "Blocked!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }

            is Fail -> {
                Log.e("Test Tracking", "invalidate: Block: ${it.asyncBlock}")
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.asyncBlock.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (it.asyncEdit) {
            is Success -> {
                Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
                viewModel.handle(HomeViewAction.GetUserById(args.id))
            }

            is Fail -> {
                Log.e("Test Tracking", "invalidate: Edit: ${it.asyncEdit}")
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.asyncEdit.error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        toolbar?.menu?.findItem(R.id.menu_notification)?.isVisible = true
        super.onDestroy()
    }
}