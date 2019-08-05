package com.example.instagr.screens

import android.app.Application
import com.example.instagr.common.firebase.FirebaseAuthManager
import com.example.instagr.data.firebase.FirebaseFeedPostsRepository
import com.example.instagr.data.firebase.FirebaseUsersRepository

class InstagramApp: Application() {
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val usersRepos by lazy { FirebaseUsersRepository() }
    val authManager by lazy { FirebaseAuthManager() }
}