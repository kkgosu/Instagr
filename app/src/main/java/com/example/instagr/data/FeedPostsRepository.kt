package com.example.instagr.data

import android.arch.lifecycle.LiveData
import com.example.instagr.models.FeedPost
import com.google.android.gms.tasks.Task

interface FeedPostsRepository {

    fun deleteFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit>
    fun copyFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun toggleLike(postId: String, uid: String): Task<Unit>
    fun getLikes(postId: String) : LiveData<List<FeedPostLike>>
}

data class FeedPostLike(val userId: String)