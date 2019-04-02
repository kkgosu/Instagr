package com.example.instagr.activities

import android.os.Bundle
import android.util.Log
import com.example.instagr.R
import com.example.instagr.view.setupBottomNavigation

class LikesActivity : BaseActivity() {
    private val TAG = "LikesActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(3)
        Log.d(TAG, "onCreate: ")
    }
}
