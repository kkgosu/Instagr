package com.example.instagr.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.instagr.R
import com.example.instagr.models.FeedPost
import com.example.instagr.models.User
import com.example.instagr.utils.CameraHelper
import com.example.instagr.utils.FirebaseHelper
import com.example.instagr.utils.GlideApp
import com.example.instagr.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate: ")

        mFirebaseHelper = FirebaseHelper(this)
        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()

        back_image.setOnClickListener { finish() }
        share_text.setOnClickListener { share() }

        mFirebaseHelper.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.asUser()!!
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(post_image)
            } else {
                finish()
            }
        }
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        if (imageUri != null) {
            val uid = mFirebaseHelper.currentUid()!!
            val ref = mFirebaseHelper.storage.child("users").child(uid).child("images")
                .child(imageUri.lastPathSegment!!)
            ref.putFile(imageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val photoUrl = it.result.toString()
                            mFirebaseHelper.database.child("images").child(uid).push()
                                .setValue(photoUrl).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        mFirebaseHelper.database.child("feed-posts").child(uid)
                                            .push()
                                            .setValue(mkFeedPost(uid, photoUrl)).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    startActivity(Intent(this, ProfileActivity::class.java))
                                                    finish()
                                                }
                                            }
                                    } else {
                                        showToast(it.exception!!.message!!)
                                    }
                                }
                        } else {
                            showToast(it.exception!!.message!!)
                        }
                    }
                } else {
                    showToast(it.exception!!.message!!)
                }
            }
        }
    }

    private fun mkFeedPost(uid: String, photoUrl: String): FeedPost {
        return FeedPost(
            uid = uid,
            username = mUser.username,
            image = photoUrl,
            caption = caption_input.text.toString(),
            photo = mUser.photo
        )
    }
}
