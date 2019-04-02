package com.example.instagr.activities.editprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.example.instagr.data.UsersRepository
import com.example.instagr.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task

class EditProfileViewModel(private val onFailureListener: OnFailureListener,
                           private val usersRepo: UsersRepository) : ViewModel() {
    val user: LiveData<User> = usersRepo.getUser()

    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
        usersRepo.uploadUserPhoto(localImage).onSuccessTask { downloadUrl ->
            usersRepo.updateUserPhoto(downloadUrl)
        }.addOnFailureListener(onFailureListener)

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
        usersRepo.updateEmail(currentEmail, newEmail, password)
            .addOnFailureListener(onFailureListener)

    fun updateProfile(currentUser: User, newUser: User): Task<Unit> =
        usersRepo.updateUserPorile(currentUser = currentUser, newUser = newUser)
            .addOnFailureListener(onFailureListener)


}