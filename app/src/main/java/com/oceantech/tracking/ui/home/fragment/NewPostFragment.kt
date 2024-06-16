package com.oceantech.tracking.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.PostsDtoReq
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.databinding.FragmentNewPostBinding
import com.oceantech.tracking.ui.home.viewmodel.HomeViewAction
import com.oceantech.tracking.ui.home.viewmodel.HomeViewModel
import java.time.Instant
import java.util.Date

class NewPostFragment : TrackingBaseFragment<FragmentNewPostBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()
    private var currentUser: UserDto? = null

    private var toolbar: Toolbar? = null

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentNewPostBinding {
        return FragmentNewPostBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.btnMedia.setOnClickListener {
            // TODO handle event post media
        }

        views.btnPost.setOnClickListener {
            create()
        }

        toolbar = activity?.findViewById(R.id.toolbar)
        toolbar?.menu?.findItem(R.id.menu_notification)?.isVisible = false

        viewModel.handle(HomeViewAction.GetCurrentUser)
        viewModel.handle(HomeViewAction.SetNavUp)
    }

    private fun create() {
        val content = views.content.text.toString().trim()

        if (content.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Enter your content...", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO handle upload media

        currentUser?.let {
            viewModel.handle(
                HomeViewAction.createPost(
                    PostsDtoReq(
                        0,
                        user = it.toReq(),
                        comments = null,
                        content = content,
                        date = Date.from(Instant.now()),
                        likes = null,
                        media = null
                    )
                )
            )
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.userCurrent) {
            is Success -> {
                it.userCurrent.invoke().let { user ->
                    currentUser = user
                    if (user.hasPhoto == true) {
                        // TODO load avatar of current user
                    } else {
                        views.image.setImageResource(R.drawable.ic_person)
                    }

                    views.displayName.text = user.displayName.toString()
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(), "Has error: " + it.userCurrent.toString(), Toast.LENGTH_SHORT
                ).show()
                Log.e("Test Tracking", "invalidate: ${it.userCurrent}")
            }
        }

        when (it.asyncCreatePost) {
            is Success -> {
                it.asyncCreatePost.invoke().let { res ->
                    Toast.makeText(
                        requireContext(), res.message ?: "Upload successful!", Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: " + it.asyncCreatePost.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Test Tracking", "invalidate: ${it.asyncCreatePost}")
            }
        }
    }

    override fun onDestroy() {
        toolbar?.menu?.findItem(R.id.menu_notification)?.isVisible = true
        super.onDestroy()
    }

}