package com.example.instagr.common.firebase

import com.example.instagr.common.AuthManager
import com.example.instagr.data.firebase.common.auth

class FirebaseAuthManager : AuthManager {
    override fun signOut() {
        auth.signOut()
    }
}