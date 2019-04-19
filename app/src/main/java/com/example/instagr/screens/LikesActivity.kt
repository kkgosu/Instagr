package com.example.instagr.screens

import android.os.Bundle
import android.util.Log
import com.example.instagr.R
import com.example.instagr.screens.common.BaseActivity
import com.example.instagr.screens.common.setupAuthGuard
import com.example.instagr.screens.common.setupBottomNavigation

class LikesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(3)
        Log.d(TAG, "onCreate: ")

        setupAuthGuard { }
    }

    companion object {
        const val TAG = "LikesActivity"
    }
}
