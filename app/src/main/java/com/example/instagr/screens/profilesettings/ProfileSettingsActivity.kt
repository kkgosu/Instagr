package com.example.instagr.screens.profilesettings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.instagr.R
import com.example.instagr.data.firebase.common.FirebaseHelper
import com.example.instagr.screens.common.BaseActivity
import com.example.instagr.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        setupAuthGuard {
            val viewModel = initViewModel<ProfileSettingsViewModel>()
            sign_out_text.setOnClickListener { viewModel.signOut() }
            back_image.setOnClickListener { finish() }
        }
    }
}
