package com.example.instagr.screens.profilesettings

import com.example.instagr.common.AuthManager
import com.example.instagr.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class ProfileSettingsViewModel(private val authManager: AuthManager,
                               onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener),
    AuthManager by authManager