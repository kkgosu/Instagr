package com.example.instagr.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.instagr.R
import com.example.instagr.models.User
import com.example.instagr.utils.CameraHelper
import com.example.instagr.utils.FirebaseHelper
import com.example.instagr.utils.ValueEventListenerAdapter
import com.example.instagr.view.PasswordDialog
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var cameraHelper: CameraHelper
    private lateinit var mFirebaseHelper: FirebaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate: ")
        close_image.setOnClickListener {
            finish()
        }

        change_photo_text.setOnClickListener { cameraHelper.takeCameraPicture() }
        save_image.setOnClickListener { updateProfile() }
        cameraHelper = CameraHelper(this)

        mFirebaseHelper = FirebaseHelper(this)
        mFirebaseHelper.currentUserReference()
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                mUser = it.asUser()!!
                name_input.setText(mUser.name)
                username_input.setText(mUser.username)
                bio_input.setText(mUser.bio)
                website_input.setText(mUser.website)
                email_input.setText(mUser.email)
                phone_input.setText(mUser.phone?.toString())
                profile_image.loadUserPhoto(mUser.photo)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == cameraHelper.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val ref = mFirebaseHelper.currentUserReferenceStorage()
            mFirebaseHelper.uploadUserPhoto(cameraHelper.imageUri!!) {
                ref.downloadUrl.addOnCompleteListener {
                    val photoUrl = it.result.toString()
                    mFirebaseHelper.updateUserPhoto(photoUrl) {
                        mUser = mUser.copy(photo = photoUrl)
                        profile_image.loadUserPhoto(mUser.photo)
                    }
                }
            }
        }
    }

    private fun updateProfile() {
        mPendingUser = readInput()

        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                PasswordDialog().show(supportFragmentManager, "password_dialog")
            }
        } else {
            showToast(error)
        }

    }

    private fun readInput(): User {
        return User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            email = email_input.text.toString(),
            bio = bio_input.text.toStringOrNull(),
            website = website_input.text.toStringOrNull(),
            phone = phone_input.text.toString().toLongOrNull()
        )
    }


    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone
        mFirebaseHelper.updateUser(updatesMap) {
            showToast(getString(R.string.profile_updated))
            finish()
        }
    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty() -> getString(R.string.please_enter_a_name)
            user.username.isEmpty() -> getString(R.string.please_enter_a_username)
            user.email.isEmpty() -> getString(R.string.please_enter_an_email)
            else -> null
        }


    override fun onPasswordConfirm(password: String) {
        if (!password.isEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebaseHelper.reauthenticate(credential) {
                mFirebaseHelper.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        } else {
            showToast(getString(R.string.you_should_enter_your_password))
        }
    }
}
