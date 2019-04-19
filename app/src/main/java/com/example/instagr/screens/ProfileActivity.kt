package com.example.instagr.screens

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
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
        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter()
        images_recycler.adapter = mAdapter


        setupAuthGuard {uid ->
            mViewModel = initViewModel()
            mViewModel.init(uid)
            mViewModel.user.observe(this, Observer { it?.let {user ->
                profile_image.loadUserPhoto(user.photo)
                username_text.text = user.username
            } })
            mViewModel.images.observe(this, Observer { it?.let {images ->
                mAdapter.updateImages(images)
            }})
        }

    }
}

class ImagesAdapter: RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)
    private var images = listOf<String>()

    fun updateImages(newImages: List<String>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(images,newImages) {it})
        this.images = newImages
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item, parent, false) as ImageView
        return ViewHolder(image)
    }


    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.image.loadImage(images[p1])
    }

    override fun getItemCount(): Int = images.size
}