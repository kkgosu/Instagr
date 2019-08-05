package com.example.instagr.screens.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.instagr.data.FeedPostsRepository
import com.example.instagr.data.UsersRepository
import com.example.instagr.models.Comment
import com.example.instagr.models.User
import com.example.instagr.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class CommentsViewModel(private val feedPostsRepo: FeedPostsRepository,
                        usersRepo: UsersRepository,
                        onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {

    private lateinit var postId: String
    val user: LiveData<User> = usersRepo.getUser()
    lateinit var comments: LiveData<List<Comment>>

    fun init(postId: String) {
        this.postId = postId
        comments = feedPostsRepo.getComments(postId)
    }

    fun createComment(text: String, user: User) {
        val comment = Comment(
            uid = user.uid,
            username = user.username,
            photo = user.photo,
            text = text
        )
        feedPostsRepo.createComment(postId, comment).addOnFailureListener(onFailureListener)
    }
}