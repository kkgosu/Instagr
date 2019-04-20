package com.example.instagr.screens.common

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.instagr.common.firebase.FirebaseAuthManager
import com.example.instagr.screens.addfriends.AddFriendsViewModel
import com.example.instagr.data.firebase.FirebaseFeedPostsRepository
import com.example.instagr.screens.editprofile.EditProfileViewModel
import com.example.instagr.data.firebase.FirebaseUsersRepository
import com.example.instagr.screens.LoginViewModel
import com.example.instagr.screens.ProfileViewModel
import com.example.instagr.screens.RegisterViewModel
import com.example.instagr.screens.ShareViewModel
import com.example.instagr.screens.home.HomeViewModel
import com.example.instagr.screens.profilesettings.ProfileSettingsViewModel
import com.google.android.gms.tasks.OnFailureListener

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val app: Application,
    private val commonViewModel: CommonViewModel,
    private val onFailureListener: OnFailureListener
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
        val usersRepos by lazy { FirebaseUsersRepository() }
        val authManager by lazy { FirebaseAuthManager() }

        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(onFailureListener, usersRepos, feedPostsRepo) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(onFailureListener, usersRepos) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(onFailureListener, feedPostsRepo) as T
        } else if (modelClass.isAssignableFrom(ProfileSettingsViewModel::class.java)) {
            return ProfileSettingsViewModel(authManager) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authManager, app, commonViewModel, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(usersRepos) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(commonViewModel, app, usersRepos) as T
        } else if (modelClass.isAssignableFrom(ShareViewModel::class.java)) {
            return ShareViewModel(usersRepos, onFailureListener) as T
        } else {
            error("Unknown View Model class $modelClass")
        }
    }
}