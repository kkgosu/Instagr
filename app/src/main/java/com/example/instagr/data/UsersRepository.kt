package com.example.instagr.data

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.example.instagr.models.FeedPost
import com.example.instagr.models.User
import com.google.android.gms.tasks.Task

interface UsersRepository {
    fun getUser(): LiveData<User>
    fun uploadUserPhoto(localImage: Uri): Task<Uri>
    fun updateUserPhoto(downloadUrl: Uri?): Task<Unit>
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit>
    fun getUsers(): LiveData<List<User>>
    fun currentUid(): String
    fun addFollow(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollow(fromUid: String, toUid: String): Task<Unit>
    fun addFollowers(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollowers(fromUid: String, toUid: String): Task<Unit>
    fun getImages(uid: String): LiveData<List<String>>
    fun isUserExistsForEmail(email: String): Task<Boolean>
    fun createUser(user: User, password: String): Task<Unit>
    fun uploadUserImage(uid: String, imageUri: Uri) : Task<Uri>
    fun setUserImage(uid: String, downloadUri: Uri): Task<Unit>
    fun createFeedPost(uid: String, feedPost: FeedPost): Task<Unit>
}