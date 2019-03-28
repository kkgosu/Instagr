package com.example.instagr.activities

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.instagr.activities.addfriends.AddFriendsViewModel
import com.example.instagr.activities.addfriends.FirebaseAddFriendsRepository
import com.example.instagr.activities.editprofile.EditProfileViewModel
import com.example.instagr.activities.editprofile.FirebaseEditProfileRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(FirebaseAddFriendsRepository()) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(FirebaseEditProfileRepository()) as T
        } else {
            error("Unknown View Model class $modelClass")
        }

    }
}