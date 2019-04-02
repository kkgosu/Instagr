package com.example.instagr.data

import android.arch.lifecycle.LiveData
import com.example.instagr.models.User
import com.google.android.gms.tasks.Task

interface FeedPostsRepository {

    fun deleteFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit>
    fun copyFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit>
}