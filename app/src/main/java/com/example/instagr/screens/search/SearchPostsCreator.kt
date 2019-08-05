package com.example.instagr.screens.search

import android.util.Log
import androidx.lifecycle.Observer
import com.example.instagr.common.BaseEventListener
import com.example.instagr.common.firebase.Event
import com.example.instagr.common.firebase.EventBus
import com.example.instagr.data.SearchRepository
import com.example.instagr.models.SearchPost

class SearchPostsCreator(searchRepo: SearchRepository) : BaseEventListener() {
    init {
        EventBus.events.observe(this, Observer {
            it?.let { event ->
                when (event) {
                    is Event.CreateFeedPost -> {
                        val searchPost = with(event.post) {
                            SearchPost(image = image,
                                       caption = caption,
                                       postId = id)
                        }
                        searchRepo.createPost(searchPost).addOnFailureListener {
                            Log.d(TAG, "Failed to create Search post for event: $event", it )
                        }
                    } else -> {}
                }
            }
        })
    }

    companion object {
        const val TAG = "SearchPostsCreator"
    }
}