package com.example.instagr.screens.register

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.example.instagr.R
import com.example.instagr.screens.common.BaseActivity
import com.example.instagr.screens.common.setupAuthGuard
import com.example.instagr.screens.home.HomeActivity
import kotlinx.android.synthetic.main.activity_register.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class RegisterActivity : BaseActivity(), EmailFragment.Listener,
    NamePassFragment.Listener,
    KeyboardVisibilityEventListener {

    private lateinit var mViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        KeyboardVisibilityEvent.setEventListener(this, this)
        setupAuthGuard {
            mViewModel = initViewModel()
            mViewModel.goToNamePassScreen.observe(this, Observer { it?.let {
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,
                    NamePassFragment()
                )
                    .addToBackStack(null)
                    .commit()
            } })

            mViewModel.goToHomeScreen.observe(this, Observer { it?.let {
                startHomeActivity()
            } })

            mViewModel.goBackToEmailScreen.observe(this, Observer { it?.let {
                supportFragmentManager.popBackStack()
            } })
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout,
                EmailFragment()
            )
                .commit()
        }
    }

    override fun onNext(email: String) {
        mViewModel.onEmailEntered(email)
    }

    override fun onRegister(fullName: String, password: String) {
        mViewModel.onRegister(fullName, password)
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            scroll_view.scrollTo(0, scroll_view.bottom)
        } else {
            scroll_view.scrollTo(0, scroll_view.top)
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}