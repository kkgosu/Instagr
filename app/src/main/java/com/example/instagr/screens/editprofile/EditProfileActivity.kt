package com.example.instagr.screens.editprofile

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.instagr.R
import com.example.instagr.models.User
import com.example.instagr.screens.common.*
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : BaseActivity(), PasswordDialog.Listener {

    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var cameraHelper: CameraHelper


    private lateinit var mViewModel: EditProfileViewModel

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

        setupAuthGuard {
            mViewModel = initViewModel()
            mViewModel.user.observe(this, Observer {
                it.let {
                    mUser = it!!
                    name_input.setText(mUser.name)
                    username_input.setText(mUser.username)
                    bio_input.setText(mUser.bio)
                    website_input.setText(mUser.website)
                    email_input.setText(mUser.email)
                    phone_input.setText(mUser.phone?.toString())
                    profile_image.loadUserPhoto(mUser.photo)
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == cameraHelper.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mViewModel.uploadAndSetUserPhoto(cameraHelper.imageUri!!)
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
        mViewModel.updateProfile(
            currentUser = mUser,
            newUser = user
        )
            .addOnSuccessListener {
                showToast(getString(R.string.profile_updated))
                finish()
            }
    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty()     -> getString(R.string.please_enter_a_name)
            user.username.isEmpty() -> getString(R.string.please_enter_a_username)
            user.email.isEmpty()    -> getString(R.string.please_enter_an_email)
            else                    -> null
        }


    override fun onPasswordConfirm(password: String) {
        if (!password.isEmpty()) {
            mViewModel.updateEmail(
                currentEmail = mUser.email,
                newEmail = mPendingUser.email,
                password = password
            )
                .addOnSuccessListener { updateUser(mPendingUser) }

        } else {
            showToast(getString(R.string.you_should_enter_your_password))
        }
    }

    companion object {
        const val TAG = "EditProfileActivity"
    }
}
