package com.example.instagr.activities.addfriends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.instagr.activities.map
import com.example.instagr.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth

class AddFriendsViewModel(private val repository: AddFriendsRepository) : ViewModel() {

    private val currentUid = FirebaseAuth.getInstance().currentUser!!.uid

    val userAndFriends: LiveData<Pair<User, List<User>>> =
        repository.getUsers().map { allUsers ->
            val (userList, otherUsersList) = allUsers.partition {
                it.uid == repository.currentUid()
            }
            userList.first() to otherUsersList
        }

    fun setFollow(currentUid: String,uid: String, follow: Boolean): Task<Void> {
        return if (follow) {
            Tasks.whenAll(
                repository.addFollow(currentUid, uid),
                repository.addFollowers(currentUid, uid),
                repository.copyFeedPosts(postsAuthorUid = uid, toUid = currentUid)
            )
        } else {
            Tasks.whenAll(
                repository.deleteFollow(currentUid, uid),
                repository.deleteFollowers(currentUid, uid),
                repository.deleteFeedPosts(postsAuthorUid = uid, toUid = currentUid)
            )
        }
    }
}