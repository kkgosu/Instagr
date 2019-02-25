package com.example.instagr.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagr.R
import com.example.instagr.models.User
import com.example.instagr.utils.FirebaseHelper
import com.example.instagr.utils.TaskSourceOnCompleteListener
import com.example.instagr.utils.ValueEventListenerAdapter
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.add_friends_item.view.*

class AddFriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {

    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var mUser: User
    private lateinit var mUsers: List<User>
    private lateinit var mAdapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        mFirebaseHelper = FirebaseHelper(this)
        mAdapter = FriendsAdapter(this)

        back_image.setOnClickListener { finish() }
        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)

        val uid = mFirebaseHelper.currentUid()!!
        mFirebaseHelper.database.child("users").addValueEventListener(ValueEventListenerAdapter {
            val allUsers = it.children.map { it.asUser()!! }
            val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
            mUser = userList.first()
            mUsers = otherUsersList
            mAdapter.update(mUsers, mUser.follows)
        })
    }

    override fun follow(uid: String) {
        setFollow(uid, true) {
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false) {
            mAdapter.unfollowed(uid)
        }
    }

    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {

        val followsTask = mFirebaseHelper.database.child("users").child(mUser.uid)
            .child("follows").child(uid).setValueTrueOrRemove(follow)
        val followersTask = mFirebaseHelper.database.child("users").child(uid)
            .child("followers").child(mUser.uid).setValueTrueOrRemove(follow)

        val feedPostsTask = task<Void> { taskSource ->
            mFirebaseHelper.database.child("feed-posts").child(uid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = if (follow) {
                        it.children.map { it.key to it.value }.toMap()
                    } else {
                        it.children.map { it.key to null }.toMap()
                    }
                    mFirebaseHelper.database.child("feed-posts").child(mUser.uid).updateChildren(postsMap)
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))

                })
        }

        Tasks.whenAll(followsTask, followersTask, feedPostsTask).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }

        followsTask.continueWith({ followersTask }).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }
}

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
        with(p0) {
            val user = mUsers[p1]
            view.photo_image.loadUserPhoto(user.photo)
            view.username_text.text = user.username
            view.name_text.text = user.name
            val follows = mFollows[user.uid] ?: false
            view.follow_btn.setOnClickListener { listener.follow(user.uid!!) }
            view.unfollow_btn.setOnClickListener { listener.unfollow(user.uid!!) }
            if (follows) {
                view.follow_btn.visibility = View.GONE
                view.unfollow_btn.visibility = View.VISIBLE
            } else {
                view.follow_btn.visibility = View.VISIBLE
                view.unfollow_btn.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = mUsers.size


    fun update(users: List<User>, follows: Map<String, Boolean>) {
        mUsers = users
        mPositions = users.withIndex().map { (idx, user) -> user.uid!! to idx }.toMap()
        mFollows = follows
        notifyDataSetChanged()
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
