package com.example.instagr.data

import androidx.lifecycle.LiveData
import com.example.instagr.models.Comment
import com.example.instagr.models.FeedPost
import com.google.android.gms.tasks.Task

interface FeedPostsRepository {

    fun deleteFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit>
    fun copyFeedPosts(postsAuthorUid: String, toUid: String): Task<Unit>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun toggleLike(postId: String, uid: String): Task<Unit>
    fun getLikes(postId: String) : LiveData<List<FeedPostLike>>
    fun getComments(postId: String): LiveData<List<Comment>>
    fun createComment(postId: String, comment: Comment): Task<Unit>
    fun createFeedPost(uid: String, feedPost: FeedPost): Task<Unit>

}

data class FeedPostLike(val userId: String?)