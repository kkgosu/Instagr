package com.example.instagr.screens.profile

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.instagr.R
import com.example.instagr.screens.addfriends.AddFriendsActivity
import com.example.instagr.screens.editprofile.EditProfileActivity
import com.example.instagr.screens.common.*
import com.example.instagr.screens.profilesettings.ProfileSettingsActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    private lateinit var mAdapter: ImagesAdapter
    private val TAG = "ProfileActivity"
    private lateinit var mViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation(4)
        Log.d(TAG, "onCreate: ")

        edit_profile_button.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        settings_image.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }
        add_friends_image.setOnClickListener {
            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
        }
        images_recycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter()
        images_recycler.adapter = mAdapter


        setupAuthGuard { uid ->
            mViewModel = initViewModel()
            mViewModel.init(uid)
            mViewModel.user.observe(this, Observer {
                it?.let { user ->
                    profile_image.loadUserPhoto(user.photo)
                    username_text.text = user.username
                    followers_count_text.text = it.followers.size.toString()
                    following_count_text.text = it.follows.size.toString()
                }
            })
            mViewModel.images.observe(this, Observer {
                it?.let { images ->
                    mAdapter.updateImages(images)
                    posts_count_text.text = images.size.toString()
                }
            })

        }

    }
}