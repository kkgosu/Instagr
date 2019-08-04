package com.example.instagr.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.instagr.common.SingleLiveEvent
import com.example.instagr.data.FeedPostsRepository
import com.example.instagr.data.common.map
import com.example.instagr.models.FeedPost
import com.example.instagr.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class HomeViewModel(
    private val feedPostsRepo: FeedPostsRepository,
    onFailureListener: OnFailureListener
) : BaseViewModel(onFailureListener) {

    lateinit var uid: String
    lateinit var feedPosts: LiveData<List<FeedPost>>
    private var loadedLikes = mapOf<String, LiveData<FeedPostLikes>>()
    private val _goToCommentsScreen = SingleLiveEvent<String>()
    val goToCommentsScreen = _goToCommentsScreen

    fun init(uid: String) {
        this.uid = uid
        feedPosts = feedPostsRepo.getFeedPosts(uid).map {
            it.sortedByDescending { it.timestampDate() }
        }
    }

    fun toggleLike(postId: String) {
        feedPostsRepo.toggleLike(postId, uid).addOnFailureListener(onFailureListener)
    }

    fun getLikes(postId: String): LiveData<FeedPostLikes>? = loadedLikes[postId]

    fun loadLikes(postId: String): LiveData<FeedPostLikes> {
        val existingLoadedLikes = loadedLikes[postId]
        return if (existingLoadedLikes == null) {
            val liveData = feedPostsRepo.getLikes(postId).map { likes ->
                FeedPostLikes(
                    likesCount = likes.size,
                    likedByUser = likes.find { it.userId == uid } != null)
            }
            loadedLikes += postId to liveData
            liveData

        } else {
            existingLoadedLikes
        }
    }

    fun openComments(postId: String) {
        _goToCommentsScreen.value = postId
    }
}