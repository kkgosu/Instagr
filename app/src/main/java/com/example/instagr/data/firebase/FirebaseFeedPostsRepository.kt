package com.example.instagr.data.firebase

import com.example.instagr.utils.toUnit
import com.example.instagr.activities.task
import com.example.instagr.data.FeedPostsRepository
import com.example.instagr.utils.TaskSourceOnCompleteListener
import com.example.instagr.utils.ValueEventListenerAdapter
import com.example.instagr.utils.database
import com.google.android.gms.tasks.Task

class FirebaseFeedPostsRepository : FeedPostsRepository {


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