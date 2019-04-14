package com.example.instagr.data.firebase

import android.arch.lifecycle.LiveData
import com.example.instagr.common.task
import com.example.instagr.common.toUnit
import com.example.instagr.data.FeedPostsRepository
import com.example.instagr.data.firebase.common.database
import com.example.instagr.common.TaskSourceOnCompleteListener
import com.example.instagr.common.ValueEventListenerAdapter
import com.example.instagr.data.FeedPostLike
import com.example.instagr.data.common.map
import com.example.instagr.data.firebase.common.FirebaseLiveData
import com.example.instagr.data.firebase.common.asFeedPost
import com.example.instagr.data.firebase.common.setValueTrueOrRemove
import com.example.instagr.models.FeedPost
import com.google.android.gms.tasks.Task

class FirebaseFeedPostsRepository : FeedPostsRepository {
    override fun getLikes(postId: String): LiveData<List<FeedPostLike>> =
        FirebaseLiveData(database.child("likes").child(postId)).map {
            it.children.map { FeedPostLike(it.key!!) }
        }


    override fun toggleLike(postId: String, uid: String): Task<Unit> {
        val reference = database.child("likes").child(postId).child(uid)
        return task { taskSource ->
            reference.addListenerForSingleValueEvent(ValueEventListenerAdapter {
                reference.setValueTrueOrRemove(!it.exists())
                taskSource.setResult(Unit)
            })
        }
    }

    override fun getFeedPosts(uid: String): LiveData<List<FeedPost>> =
        FirebaseLiveData(database.child("feed-posts").child(uid)).map {
            it.children.map { it.asFeedPost()!! }
        }


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
}