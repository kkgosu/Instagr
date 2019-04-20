package com.example.instagr.screens.share

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.instagr.R
import com.example.instagr.models.User
import com.example.instagr.screens.common.BaseActivity
import com.example.instagr.screens.common.CameraHelper
import com.example.instagr.screens.common.loadImage
import com.example.instagr.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity() {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mUser: User
    private lateinit var mViewModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate: ")

        setupAuthGuard {
            mViewModel = initViewModel()
            mViewModel.user.observe(this, Observer {it?.let {
                mUser = it
            }})

            mCamera = CameraHelper(this)
            mCamera.takeCameraPicture()

            back_image.setOnClickListener { finish() }
            share_text.setOnClickListener { share() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                post_image.loadImage(mCamera.imageUri?.toString())
            } else {
                finish()
            }
        }
    }

    private fun share() {
        mViewModel.share(mUser, mCamera.imageUri, caption_input.text.toString())
    }
}
