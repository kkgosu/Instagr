package com.example.instagr.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.instagr.data.UsersRepository
import com.example.instagr.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class ProfileViewModel(private val usersRepo: UsersRepository,
                       onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {
    val user = usersRepo.getUser()
    lateinit var images: LiveData<List<String>>

    fun init(uid: String) {
        if (!this::images.isInitialized) {
            images = usersRepo.getImages(uid)
        }
    }

}