package com.example.instagr.data.firebase

import androidx.lifecycle.LiveData
import com.example.instagr.common.toUnit
import com.example.instagr.data.SearchRepository
import com.example.instagr.data.common.map
import com.example.instagr.data.firebase.common.FirebaseLiveData
import com.example.instagr.data.firebase.common.database
import com.example.instagr.models.SearchPost
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot

class FirebaseSearchRepository : SearchRepository {
    override fun createPost(post: SearchPost): Task<Unit> =
        database.child("search-posts").push().setValue(post.copy(caption = post.caption.toLowerCase())).toUnit()

    override fun searchPosts(text: String): LiveData<List<SearchPost>> {
        val reference = database.child("search-posts")
        val query = if (text.isEmpty()) {
            reference
        } else {
            reference.orderByChild("caption")
                .startAt(text.toLowerCase()).endAt("${text.toLowerCase()}\\uf8ff")
        }
        return FirebaseLiveData(query).map {
            it.children.map { it.asSearchPost()!! }
        }
    }
}

private fun DataSnapshot.asSearchPost(): SearchPost? =
    getValue(SearchPost::class.java)?.copy(id = key!!)