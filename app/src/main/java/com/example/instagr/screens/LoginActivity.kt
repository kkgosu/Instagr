package com.example.instagr.screens

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.instagr.R
import com.example.instagr.screens.common.BaseActivity
import com.example.instagr.screens.common.coordinateBtnAndInputs
import com.example.instagr.screens.common.setupAuthGuard
import com.example.instagr.screens.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : BaseActivity(), KeyboardVisibilityEventListener, View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: ")

        coordinateBtnAndInputs(login_button, email_login_input, password_login_input)
        login_button.setOnClickListener(this)
        create_account_text.setOnClickListener(this)
        KeyboardVisibilityEvent.setEventListener(this, this)

        setupAuthGuard {
            mViewModel = initViewModel()
            mViewModel.goToHomeScreen.observe(this, Observer {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            })
            mViewModel.goToRegisterScreen.observe(this, Observer {
                startActivity(Intent(this, RegisterActivity::class.java))
            })
            mAuth = FirebaseAuth.getInstance()

        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_button ->
                mViewModel.onLoginClick(
                    email = email_login_input.text.toString(),
                    password = password_login_input.text.toString()
                )
            R.id.create_account_text ->
                mViewModel.onRegisterClick()
        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            scroll_view.scrollTo(0, scroll_view.bottom)
            create_account_text.visibility = View.GONE
        } else {
            scroll_view.scrollTo(0, scroll_view.top)
            create_account_text.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}
