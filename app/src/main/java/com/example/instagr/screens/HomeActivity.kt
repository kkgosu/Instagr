package com.example.instagr.screens

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.instagr.R
import com.example.instagr.data.firebase.common.asFeedPost
import com.example.instagr.data.firebase.common.setValueTrueOrRemove
import com.example.instagr.models.FeedPost
import com.example.instagr.screens.common.*
import com.example.instagr.data.firebase.common.FirebaseHelper
import com.example.instagr.common.ValueEventListenerAdapter
import com.example.instagr.screens.common.setupBottomNavigation
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.feed_post.view.*

class HomeActivity : BaseActivity(), FeedAdapter.Listener {

    private val TAG = "HomeActivity"
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var mAdapter: FeedAdapter
    private var mLikesListener: Map<String, ValueEventListener> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(0)
        Log.d(TAG, "onCreate: ")

        mFirebaseHelper = FirebaseHelper(this)

        mFirebaseHelper.auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mFirebaseHelper.auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            mFirebaseHelper.database.child("feed-posts").child(currentUser.uid)
                .addValueEventListener(ValueEventListenerAdapter {
                    val posts = it.children.map { it.asFeedPost()!! }
                        .sortedByDescending { it.timestampDate() }
                    mAdapter = FeedAdapter(this, posts)
                    feed_recycler.adapter = mAdapter
                    feed_recycler.layoutManager = LinearLayoutManager(this)

                })
        }

    }

    override fun toggleLike(postId: String) {
        val reference = mFirebaseHelper.database.child("likes").child(postId).child(mFirebaseHelper.currentUid()!!)
        reference
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                reference.setValueTrueOrRemove(!it.exists())
            })
    }

    override fun loadLikes(postId: String, position: Int) {
        fun createListener() =
        mFirebaseHelper.database.child("likes").child(postId).addValueEventListener(
            ValueEventListenerAdapter {
                val userLikes = it.children.map { it.key }.toSet()
                val postLikes = FeedPostLikes(userLikes.size, userLikes.contains(mFirebaseHelper.currentUid()))
                mAdapter.updatePostLikes(position, postLikes)
            })

        if(mLikesListener[postId] == null) {
            mLikesListener += (postId to createListener())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLikesListener.values.forEach { mFirebaseHelper.database.removeEventListener(it) }
    }
}

data class FeedPostLikes(val likesCount: Int, val likedByUser: Boolean)

class FeedAdapter(private val listener: Listener, private val posts: List<FeedPost>) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    val defaultPostLikes = FeedPostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_post, parent, false)
        return ViewHolder(view)
    }

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedPost = posts[position]
        val likes = postLikes[position] ?: defaultPostLikes
        with(holder.view) {
            icon_image.loadUserPhoto(feedPost.photo)
            username_text.text = feedPost.username
            photo_image.loadImage(feedPost.image)
            if (likes.likesCount == 0) {
                likes_text.visibility = View.GONE
            } else {
                likes_text.visibility = View.VISIBLE
                val likesCountText =
                    context.resources.getQuantityString(R.plurals.likes_count, likes.likesCount, likes.likesCount)
                likes_text.text = likesCountText
            }
            caption_text.setCaptionText(feedPost.username, feedPost.caption)
            likes_image.setOnClickListener { listener.toggleLike(feedPost.id) }
            likes_image.setImageResource(
                if (likes.likedByUser) R.drawable.ic_likes_active
                else R.drawable.ic_likes_border
            )
            listener.loadLikes(feedPost.id, position)
        }
    }

    private fun TextView.setCaptionText(username: String, caption: String) {
        val usernameSpannable = SpannableString(username)
        usernameSpannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, usernameSpannable.length
            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        usernameSpannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.context.showToast(context.getString(R.string.username_is_clicked))
            }

            override fun updateDrawState(ds: TextPaint) {}
        }, 0, usernameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        text = SpannableStringBuilder()
            .append(usernameSpannable)
            .append(" ")
            .append(caption)
        movementMethod = LinkMovementMethod.getInstance()
    }

    override fun getItemCount(): Int = posts.size
}
