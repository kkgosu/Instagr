package com.example.instagr.screens.addfriends

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagr.R
import com.example.instagr.screens.common.loadUserPhoto
import com.example.instagr.models.User
import com.example.instagr.screens.common.SimpleCallback

import kotlinx.android.synthetic.main.add_friends_item.view.*

class FriendsAdapter(private val listener: Listener) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    private var mUsers = listOf<User>()
    private var mFollows = mapOf<String, Boolean>()
    private var mPositions = mapOf<String, Int>()

    interface Listener {
        fun follow(uid: String)
        fun unfollow(uid: String)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(p0.context).inflate(R.layout.add_friends_item, p0, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        with(p0.view) {
            val user = mUsers[p1]
            photo_image.loadUserPhoto(user.photo)
            username_text.text = user.username
            name_text.text = user.name
            val follows = mFollows[user.uid] ?: false
            follow_btn.setOnClickListener { listener.follow(user.uid) }
            unfollow_btn.setOnClickListener { listener.unfollow(user.uid) }
            if (follows) {
                follow_btn.visibility = View.GONE
                unfollow_btn.visibility = View.VISIBLE
            } else {
                follow_btn.visibility = View.VISIBLE
                unfollow_btn.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = mUsers.size

    fun update(users: List<User>, follows: Map<String, Boolean>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(mUsers, users) { it.uid})
        mUsers = users
        mPositions = users.withIndex().map { (idx, user) -> user.uid to idx }.toMap()
        mFollows = follows
        diffResult.dispatchUpdatesTo(this)
    }

    fun followed(uid: String) {
        mFollows += uid to true
        notifyItemChanged(mPositions[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows -= uid
        notifyItemChanged(mPositions[uid]!!)
    }
}
