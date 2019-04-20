package com.example.instagr.common.firebase

import com.example.instagr.common.AuthManager
import com.example.instagr.common.toUnit
import com.example.instagr.data.firebase.common.auth
import com.google.android.gms.tasks.Task

class FirebaseAuthManager : AuthManager {

    override fun signIn(email: String, password: String): Task<Unit> =
        auth.signInWithEmailAndPassword(email, password).toUnit()

    override fun signOut() {
        auth.signOut()
    }
}