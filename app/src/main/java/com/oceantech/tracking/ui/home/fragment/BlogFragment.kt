package com.oceantech.tracking.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.SearchDto
import com.oceantech.tracking.data.model.UserDto
import com.oceantech.tracking.data.model.toReq
import com.oceantech.tracking.databinding.FragmentBlogBinding
import com.oceantech.tracking.ui.home.viewmodel.HomeViewAction
import com.oceantech.tracking.ui.home.viewmodel.HomeViewModel
import com.oceantech.tracking.ui.home.adapter.PostAdapter
import javax.inject.Inject

class BlogFragment @Inject constructor() : TrackingBaseFragment<FragmentBlogBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    private var adapter: PostAdapter? = null

    private var userCurrent: UserDto? = null
    private var pageIndex = 0
        set(value) {
            field = value
            searchDto = searchDto.copy(pageIndex = field + 1)
        }
    private var searchDto: SearchDto = SearchDto("", pageIndex + 1, 10, 0)


    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBlogBinding {
        return FragmentBlogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.btnNewPost.setOnClickListener {
            val action = BlogFragmentDirections.actionBlogFragmentToNewPostFragment()
            findNavController().navigate(action)
        }

        // TOdo handle event click in item list
        adapter = PostAdapter(
            context = requireContext(),
            checkLiked = { users ->
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
        views.btnPrevious.setOnClickListener {
            --pageIndex
            viewModel.handle(HomeViewAction.GetPosts(searchDto))
        }
        views.btnNext.setOnClickListener {
            ++pageIndex
            viewModel.handle(HomeViewAction.GetPosts(searchDto))
        }

        viewModel.handle(HomeViewAction.GetPosts(searchDto))
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

        when (it.asyncPosts) {
            is Success -> {
                it.asyncPosts.invoke().let { page ->
                    views.pageLayout.visibility = if (page.empty) View.GONE else View.VISIBLE
                    views.btnPrevious.visibility = if (page.first) View.GONE else View.VISIBLE
                    views.btnNext.visibility = if (page.last) View.GONE else View.VISIBLE
                    views.pageNumber.text = "${page.number + 1}/${page.totalPages}"

                    adapter?.submitList(page.content)
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    "Has error: ${it.asyncPosts.error}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("Test", "invalidate: " + it.asyncPosts.toString())
            }
        }

        when (it.asyncCommentPosts) {
            is Success -> {
                it.asyncCommentPosts.invoke().let {
                    viewModel.handle(HomeViewAction.GetPosts(searchDto))
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
                    viewModel.handle(HomeViewAction.GetPosts(SearchDto("", 0, 100, 0)))
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