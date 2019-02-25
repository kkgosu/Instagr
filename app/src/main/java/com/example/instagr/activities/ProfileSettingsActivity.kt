package com.example.instagr.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.instagr.R
import com.example.instagr.utils.FirebaseHelper
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        mFirebaseHelper = FirebaseHelper(this)

        sign_out_text.setOnClickListener {
            mFirebaseHelper.auth.signOut()
        }

        back_image.setOnClickListener { finish() }
    }
}
