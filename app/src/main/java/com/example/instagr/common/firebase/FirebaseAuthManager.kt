package com.example.instagr.common.firebase

import com.example.instagr.common.AuthManager
import com.example.instagr.common.task
import com.example.instagr.data.firebase.common.auth
import com.google.android.gms.tasks.Task

class FirebaseAuthManager : AuthManager {

    override fun signIn(email: String, password: String): Task<Unit> =
        task {
            auth.signInWithEmailAndPassword(email, password)
        }


    override fun signOut() {
        auth.signOut()
    }


}