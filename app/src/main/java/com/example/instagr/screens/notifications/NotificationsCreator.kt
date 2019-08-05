package com.example.instagr.screens.notifications

import android.util.Log
import androidx.lifecycle.*
import com.example.instagr.common.firebase.Event
import com.example.instagr.common.firebase.EventBus
import com.example.instagr.data.FeedPostsRepository
import com.example.instagr.data.NotificationsRepository
import com.example.instagr.data.UsersRepository
import com.example.instagr.data.common.observeFirstNotNull
import com.example.instagr.data.common.zip
import com.example.instagr.models.Notification
import com.example.instagr.models.NotificationType
import com.example.instagr.models.User

class NotificationsCreator(private val notificationsRepo: NotificationsRepository,
                           private val usersRepo: UsersRepository,
                           private val feedPostsRepo: FeedPostsRepository) : LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        lifecycleRegistry.markState(Lifecycle.State.STARTED)

        EventBus.events.observe(this, Observer {
            it?.let { event ->
                when (event) {
                    is Event.CreateFollow  -> {
                        getUser(event.fromUid).observeFirstNotNull(this) { user ->
                            val notification = Notification(uid = user.uid,
                                                            username = user.username,
                                                            photo = user.photo,
                                                            type = NotificationType.Follow)
                            notificationsRepo.createNotification(event.toUid, notification)
                                .addOnFailureListener {
                                    Log.d(TAG, "Failed to create notification", it)
                                }
                        }
                    }
                    is Event.CreateLike    -> {
                        val userData = usersRepo.getUser(event.uid)
                        val postData = feedPostsRepo.getFeedPost(event.uid, event.postId)
                        userData.zip(postData).observeFirstNotNull(this) { (user, post) ->
                            val notification = Notification(uid = user.uid,
                                                            username = user.username,
                                                            photo = user.photo,
                                                            postId = post.id,
                                                            postImage = post.image,
                                                            type = NotificationType.Like)
                            notificationsRepo.createNotification(post.uid, notification)
                                .addOnFailureListener {
                                    Log.d(TAG, "Failed to create notification", it)
                                }
                        }
                    }
                    is Event.CreateComment -> {
                        feedPostsRepo.getFeedPost(event.comment.uid, event.postId)
                            .observeFirstNotNull(this) { post ->
                                val notification = Notification(uid = event.comment.uid,
                                                                username = event.comment.username,
                                                                photo = event.comment.photo,
                                                                postId = event.postId,
                                                                postImage = post.image,
                                                                commentText = event.comment.text,
                                                                type = NotificationType.Comment)
                                notificationsRepo.createNotification(post.uid, notification)
                                    .addOnFailureListener {
                                        Log.d(TAG, "Failed to create notification", it)
                                    }
                            }
                    }
                }
            }
        })
    }

    private fun getUser(uid: String): LiveData<User> = usersRepo.getUser(uid)

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    companion object {
        const val TAG = "NotificationsCreator"

    }
}