package com.example.instagr.screens.home

import android.graphics.Typeface
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.instagr.R
import com.example.instagr.models.FeedPost
import com.example.instagr.screens.common.SimpleCallback
import com.example.instagr.screens.common.loadImage
import com.example.instagr.screens.common.loadUserPhoto
import com.example.instagr.screens.common.showToast
import kotlinx.android.synthetic.main.feed_post.view.*

class FeedAdapter(private val listener: Listener) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var posts = listOf<FeedPost>()
    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    private val defaultPostLikes = FeedPostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_post, parent, false)
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

    fun updatePosts(newPosts: List<FeedPost>) {
        val diffResult = DiffUtil.calculateDiff(
            SimpleCallback(
                this.posts,
                newPosts
            ) { it.id })
        this.posts = newPosts
        diffResult.dispatchUpdatesTo(this)
    }
}