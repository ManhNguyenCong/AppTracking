package com.oceantech.tracking.ui.home

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
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.PostsDto
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toPostDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.databinding.FragmentBlogBinding
import javax.inject.Inject

class BlogFragment @Inject constructor() : TrackingBaseFragment<FragmentBlogBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    private var adapter: PostAdapter? = null
    private var posts: List<PostsDto>? = null

    private var user: UserDto? = null

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBlogBinding {
        return FragmentBlogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TOdo handle event click in item list
        adapter = PostAdapter(
            onBtnLikeClick = {

            },
            postComment = { postId, comment ->
                viewModel.handle(
                    HomeViewAction.PostComment(
                        postId,
                        comment.copy(user = user?.toReq())
                    )
                )
            },
            showLikes = {

            },
            onMediaClick = {

            }
        )
        views.rvPosts.adapter = adapter

        viewModel.handle(HomeViewAction.GetNewPosts(SearchDto("", 0, 100, 0)))
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncNewPosts) {
            is Success -> {
                it.asyncNewPosts.invoke().let { page ->
                    adapter?.submitList(page.content.reversed())
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
                Log.d("Test", "invalidate: " + it.asyncCommentPosts)
                Toast.makeText(
                    requireContext(),
                    "Has error: ${it.asyncCommentPosts.error}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}