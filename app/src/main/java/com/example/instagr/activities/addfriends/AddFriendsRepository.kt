package com.example.instagr.activities.addfriends

import android.arch.lifecycle.LiveData
import com.example.instagr.activities.asUser
import com.example.instagr.activities.map
import com.example.instagr.activities.task
import com.example.instagr.models.User
import com.example.instagr.utils.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

interface AddFriendsRepository {
    fun getUsers(): LiveData<List<User>>
    fun currentUid(): String
    fun addFollow(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollow(fromUid: String, toUid: String): Task<Unit>
    fun addFollowers(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollowers(fromUid: String, toUid: String): Task<Unit>
    fun deleteFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit>
    fun copyFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit>
}

class FirebaseAddFriendsRepository : AddFriendsRepository {

    override fun getUsers(): LiveData<List<User>> =
        database.child("users").liveData().map {
            it.children.map { it.asUser()!! }
        }

    override fun addFollow(fromUid: String, toUid: String): Task<Unit> =
        getFollows(fromUid, toUid).setValue(true).toUnit()

    override fun deleteFollow(fromUid: String, toUid: String): Task<Unit> =
        getFollows(fromUid, toUid).removeValue().toUnit()

    override fun addFollowers(fromUid: String, toUid: String): Task<Unit> =
        getFollowers(fromUid, toUid).setValue(true).toUnit()

    override fun deleteFollowers(fromUid: String, toUid: String): Task<Unit> =
        getFollowers(fromUid, toUid).removeValue().toUnit()

    override fun copyFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    database.child("feed-posts").child(toUid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))

                })
        }

    override fun deleteFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts").child(toUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to null }.toMap()
                    database.child("feed-posts").child(toUid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))

                })
        }

    private fun getFollows(fromUid: String, toUid: String) = database.child("users").child(fromUid)
        .child("follows").child(toUid)

    private fun getFollowers(fromUid: String, toUid: String) = database.child("users").child(toUid)
        .child("followers").child(fromUid)


    override fun currentUid() = FirebaseAuth.getInstance().currentUser!!.uid
}

fun Task<Void>.toUnit(): Task<Unit> = onSuccessTask { Tasks.forResult(Unit) }
