package com.example.instagr.screens.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.instagr.screens.addfriends.AddFriendsViewModel
import com.example.instagr.data.firebase.FirebaseFeedPostsRepository
import com.example.instagr.screens.editprofile.EditProfileViewModel
import com.example.instagr.data.firebase.FirebaseUsersRepository
import com.google.android.gms.tasks.OnFailureListener

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val onFailureListener: OnFailureListener) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(onFailureListener, FirebaseUsersRepository(), FirebaseFeedPostsRepository()) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(onFailureListener, FirebaseUsersRepository()) as T
        } else {
            error("Unknown View Model class $modelClass")
        }

    }
}