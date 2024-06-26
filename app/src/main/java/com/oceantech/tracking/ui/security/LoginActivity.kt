package com.oceantech.tracking.ui.security

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivityLoginBinding
import com.oceantech.tracking.ui.security.fragment.LoginFragment
import com.oceantech.tracking.ui.security.fragment.ResetPasswordFragment
import com.oceantech.tracking.ui.security.fragment.SigninFragment
import com.oceantech.tracking.ui.security.viewmodel.SecurityViewEvent
import com.oceantech.tracking.ui.security.viewmodel.SecurityViewModel
import com.oceantech.tracking.ui.security.viewmodel.SecurityViewState
import com.oceantech.tracking.utils.addFragmentToBackstack
import javax.inject.Inject

class LoginActivity : TrackingBaseActivity<ActivityLoginBinding>(), SecurityViewModel.Factory {

    val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityviewmodelFactory: SecurityViewModel.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        supportFragmentManager.commit {
            add<LoginFragment>(R.id.frame_layout)
        }
        viewModel.subscribe(this) {
            if (it.isLoading()) {
                views.progressBar.visibility = View.VISIBLE
            } else
                views.progressBar.visibility = View.GONE
        }
        viewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }
        print(viewModel.getString())

        val session = SessionManager(this)
        if (!session.fetchAuthToken().isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.login_session_expired))
                .setMessage(getString(R.string.login_session_expired_content))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    session.clearToken()
                }
                .setCancelable(false)
                .create()
                .show()
        }
        viewModel.clearUserPreferences()
    }

    private fun handleEvent(event: SecurityViewEvent) {
        when (event) {
            is SecurityViewEvent.ReturnSigninEvent -> {
                addFragmentToBackstack(R.id.frame_layout, SigninFragment::class.java)
            }

            is SecurityViewEvent.ReturnResetpassEvent -> {
                addFragmentToBackstack(R.id.frame_layout, ResetPasswordFragment::class.java)
            }

        }
    }

    override fun getBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityviewmodelFactory.create(initialState)
    }

}