package com.example.instagr.screens.common

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instagr.common.firebase.FirebaseAuthManager
import com.example.instagr.screens.addfriends.AddFriendsViewModel
import com.example.instagr.data.firebase.FirebaseFeedPostsRepository
import com.example.instagr.screens.editprofile.EditProfileViewModel
import com.example.instagr.data.firebase.FirebaseUsersRepository
import com.example.instagr.screens.comments.CommentsViewModel
import com.example.instagr.screens.login.LoginViewModel
import com.example.instagr.screens.profile.ProfileViewModel
import com.example.instagr.screens.register.RegisterViewModel
import com.example.instagr.screens.share.ShareViewModel
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
            return HomeViewModel(feedPostsRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(ProfileSettingsViewModel::class.java)) {
            return ProfileSettingsViewModel(authManager, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authManager, app, commonViewModel, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(usersRepos, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(commonViewModel, app, usersRepos, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(ShareViewModel::class.java)) {
            return ShareViewModel(usersRepos, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            return CommentsViewModel(feedPostsRepo, usersRepos, onFailureListener) as T
        } else {
            error("Unknown View Model class $modelClass")
        }
    }
}