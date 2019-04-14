package com.example.instagr.screens.home

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.instagr.R
import com.example.instagr.data.firebase.common.asFeedPost
import com.example.instagr.data.firebase.common.setValueTrueOrRemove
import com.example.instagr.screens.common.*
import com.example.instagr.data.firebase.common.FirebaseHelper
import com.example.instagr.common.ValueEventListenerAdapter
import com.example.instagr.screens.LoginActivity
import com.example.instagr.screens.common.setupBottomNavigation
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), FeedAdapter.Listener {

    private lateinit var mAdapter: FeedAdapter
    private lateinit var mViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(0)
        Log.d(TAG, "onCreate: ")
        mAdapter = FeedAdapter(this)
        feed_recycler.adapter = mAdapter
        feed_recycler.layoutManager = LinearLayoutManager(this)

        setupAuthGuard { uid ->
            mViewModel = initViewModel()
            mViewModel.init(uid)
            mViewModel.feedPosts.observe(this, Observer {
                it?.let {
                    mAdapter.updatePosts(it)
                }
            })
        }
    }

    override fun toggleLike(postId: String) {
        mViewModel.toggleLike(postId)
    }

    override fun loadLikes(postId: String, position: Int) {
        if (mViewModel.getLikes(postId) == null) {
            mViewModel.loadLikes(postId).observe(this, Observer {
                it?.let { postsLikes ->
                    mAdapter.updatePostLikes(position, postsLikes)
                }
            })
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}

