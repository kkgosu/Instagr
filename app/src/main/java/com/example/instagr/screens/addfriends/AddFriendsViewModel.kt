package com.example.instagr.screens.addfriends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.instagr.data.FeedPostsRepository
import com.example.instagr.data.UsersRepository
import com.example.instagr.data.common.map
import com.example.instagr.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth

class AddFriendsViewModel(private val onFailureListener: OnFailureListener,
                          private val usersRepos: UsersRepository,
                          private val feedPostsRepo: FeedPostsRepository) : ViewModel() {

    private val currentUid = FirebaseAuth.getInstance().currentUser!!.uid

    val userAndFriends: LiveData<Pair<User, List<User>>> =
        usersRepos.getUsers().map { allUsers ->
            val (userList, otherUsersList) = allUsers.partition {
                it.uid == usersRepos.currentUid()
            }
            userList.first() to otherUsersList
        }

    fun setFollow(currentUid: String,uid: String, follow: Boolean): Task<Void> {
        return (if (follow) {
            Tasks.whenAll(
                usersRepos.addFollow(currentUid, uid),
                usersRepos.addFollowers(currentUid, uid),
                feedPostsRepo.copyFeedPosts(postsAuthorUid = uid, toUid = currentUid)
            )
        } else {
            Tasks.whenAll(
                usersRepos.deleteFollow(currentUid, uid),
                usersRepos.deleteFollowers(currentUid, uid),
                feedPostsRepo.deleteFeedPosts(postsAuthorUid = uid, toUid = currentUid)
            )
        }).addOnFailureListener(onFailureListener)
    }
}