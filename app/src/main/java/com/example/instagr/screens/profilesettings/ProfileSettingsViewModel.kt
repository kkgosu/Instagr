package com.example.instagr.screens.profilesettings

import android.arch.lifecycle.ViewModel
import com.example.instagr.common.AuthManager

class ProfileSettingsViewModel(private val authManager: AuthManager) : ViewModel(),
    AuthManager by authManager