package com.example.instagr.screens.register

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.instagr.R
import com.example.instagr.common.SingleLiveEvent
import com.example.instagr.data.UsersRepository
import com.example.instagr.models.User
import com.example.instagr.screens.common.CommonViewModel

class RegisterViewModel(private val commonViewModel: CommonViewModel,
                        private val app: Application,
                        private val usersRepo: UsersRepository): ViewModel() {

    private val _goToNamePassScreen = SingleLiveEvent<Unit>()
    private val _goToHomeScreen = SingleLiveEvent<Unit>()
    private val _goBackToEmailScreen = SingleLiveEvent<Unit>()
    val goToNamePassScreen : LiveData<Unit> = _goToNamePassScreen
    val goToHomeScreen : LiveData<Unit> = _goToNamePassScreen
    val goBackToEmailScreen : LiveData<Unit> = _goBackToEmailScreen

    private var email: String? = null

    fun onEmailEntered(email: String) {
        if (email.isNotEmpty()) {
            this.email = email
            usersRepo.isUserExistsForEmail(email).addOnSuccessListener { exists ->
                if (!exists) {
                    _goToNamePassScreen.call()
                } else {
                    commonViewModel.setErrorMessage(app.getString(R.string.this_email_already_exists))
                }
            }
        } else {
            commonViewModel.setErrorMessage(app.getString(R.string.please_enter_an_email))
        }
    }

    fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            val localEmail = email
            if (localEmail != null) {
                usersRepo.createUser(mkUser(fullName, localEmail), password).addOnSuccessListener {
                    _goToHomeScreen.call()
                }
            } else {
                commonViewModel.setErrorMessage(app.getString(R.string.please_enter_an_email))
                _goBackToEmailScreen.call()
            }

        } else {
            commonViewModel.setErrorMessage(app.getString(R.string.please_enter_fullname_and_password))
        }
    }

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun mkUsername(fullName: String) =
        fullName.toLowerCase().replace(" ", ".")
}