package com.example.instagr.screens

import android.app.Application
import com.example.instagr.common.firebase.FirebaseAuthManager
import com.example.instagr.data.firebase.FirebaseFeedPostsRepository
import com.example.instagr.data.firebase.FirebaseNotificationsRepository
import com.example.instagr.data.firebase.FirebaseSearchRepository
import com.example.instagr.data.firebase.FirebaseUsersRepository
import com.example.instagr.screens.notifications.NotificationsCreator
import com.example.instagr.screens.search.SearchPostsCreator

class InstagramApp : Application() {
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val usersRepo by lazy { FirebaseUsersRepository() }
    val notificationsRepo by lazy { FirebaseNotificationsRepository() }
    val authManager by lazy { FirebaseAuthManager() }
    val searchRepo by lazy { FirebaseSearchRepository() }

    override fun onCreate() {
        super.onCreate()
        NotificationsCreator(notificationsRepo, usersRepo, feedPostsRepo)
        SearchPostsCreator(searchRepo)
    }
}