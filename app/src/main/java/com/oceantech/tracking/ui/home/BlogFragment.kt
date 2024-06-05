package com.oceantech.tracking.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.databinding.FragmentBlogBinding
import javax.inject.Inject

class BlogFragment @Inject constructor() : TrackingBaseFragment<FragmentBlogBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    private var adapter: PostAdapter? = null

    private var userCurrent: UserDto? = null

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBlogBinding {
        return FragmentBlogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.btnNewPost.setOnClickListener {
            // TODO handle event click button new post
        }

        // TOdo handle event click in item list
        adapter = PostAdapter(
            context = requireContext(),
            checkLiked = { users ->
                Log.d("Test", "onViewCreated: " + userCurrent.toString())
                users.contains(userCurrent)
            },
            onBtnLikeClick = { postId, like ->
                viewModel.handle(
                    HomeViewAction.LikePosts(
                        postId,
                        like.copy(user = userCurrent?.toReq())
                    )
                )
            },
            postComment = { postId, comment ->
                viewModel.handle(
                    HomeViewAction.CommentPosts(
                        postId,
                        comment.copy(user = userCurrent?.toReq())
                    )
                )
            },
            showLikes = {
                AlertDialog.Builder(requireContext())
                    .setTitle("Danh sách lượt thích")
                    .setMessage(it.joinToString("\n") { like -> like.user?.displayName ?: "" })
                    .create()
                    .show()
            },
            onMediaClick = {

            }
        )
        views.rvPosts.adapter = adapter

        viewModel.handle(HomeViewAction.GetCurrentUser)
        viewModel.handle(HomeViewAction.GetNewPosts(SearchDto("", 0, 100, 0)))
    }

    override fun invalidate(): Unit = withState(viewModel) {

        when (it.userCurrent) {
            is Success -> {
                it.userCurrent.invoke().let { user -> userCurrent = user }
            }

            is Fail -> {
                // TODO handle when get current user fail
                findNavController().navigateUp()
            }
        }

        when (it.asyncNewPosts) {
            is Success -> {
                it.asyncNewPosts.invoke().let { page ->
                    adapter?.submitList(page.content.reversed())
                    Log.d("Test", "invalidate: " + page.content.joinToString { post -> post.likes?.joinToString { like -> like.user.toString() } ?: "null" })
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: ${it.asyncNewPosts.error}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("Test", "invalidate: " + it.asyncNewPosts.toString())
            }
        }

        when (it.asyncCommentPosts) {
            is Success -> {
                it.asyncCommentPosts.invoke().let { commentRes ->
                    viewModel.handle(HomeViewAction.GetNewPosts(SearchDto("", 0, 100, 0)))
                }
            }

            is Fail -> {
                Log.d("Test", "invalidate: " + it.asyncCommentPosts.toString())
                Toast.makeText(
                    requireContext(),
                    "Has error: ${it.asyncCommentPosts.error}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (it.asyncLikePosts) {
            is Success -> {
                it.asyncLikePosts.invoke().let {
                    viewModel.handle(HomeViewAction.GetNewPosts(SearchDto("", 0, 100, 0)))
                }
            }

            is Fail -> {
                Log.d("Test", "invalidate: " + it.asyncLikePosts.toString())
                Toast.makeText(
                    requireContext(),
                    "Has error: ${it.asyncLikePosts.error}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}