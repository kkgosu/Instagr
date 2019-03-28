package com.example.instagr.activities.editprofile

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.example.instagr.activities.addfriends.toUnit
import com.example.instagr.activities.asUser
import com.example.instagr.activities.map
import com.example.instagr.activities.task
import com.example.instagr.models.User
import com.example.instagr.utils.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider

interface EditProfileRepository {
    fun getUser(): LiveData<User>
    fun uploadUserPhoto(localImage: Uri): Task<Uri>
    fun updateUserPhoto(downloadUrl: Uri?): Task<Unit>
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun updateUserPorile(currentUser: User, newUser: User): Task<Unit>
}

class FirebaseEditProfileRepository : EditProfileRepository {
    override fun updateUserPorile(currentUser: User, newUser: User): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()
        if (newUser.name != currentUser.name) updatesMap["name"] = newUser.name
        if (newUser.username != currentUser.username) updatesMap["username"] = newUser.username
        if (newUser.website != currentUser.website) updatesMap["website"] = newUser.website
        if (newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio
        if (newUser.email != currentUser.email) updatesMap["email"] = newUser.email
        if (newUser.phone != currentUser.phone) updatesMap["phone"] = newUser.phone

        return database.child("users").child(currentUid()!!).updateChildren(updatesMap).toUnit()
    }

    override fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentEmail, password)
            currentUser.reauthenticate(credential).onSuccessTask {
                currentUser.updateEmail(newEmail)
            }.toUnit()
        } else {
            Tasks.forException(IllegalStateException("User is not authenticated"))
        }
    }


    override fun uploadUserPhoto(localImage: Uri): Task<Uri> =
        task { taskSource ->
            val ref = storage.child("users/${currentUid()!!}/photo")
            ref.putFile(localImage).addOnSuccessListener {
                ref.downloadUrl.addOnCompleteListener {
                    taskSource.setResult(it.result!!)
                }
            }
        }


    override fun updateUserPhoto(downloadUrl: Uri?): Task<Unit> =
        database.child("users/${currentUid()!!}/photo").setValue(downloadUrl.toString()).toUnit()


    override fun getUser(): LiveData<User> =
        database.child("users").child(currentUid()!!).liveData().map {
            it.asUser()!!
        }
}