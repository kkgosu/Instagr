package com.example.instagr.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.instagr.R
import com.example.instagr.activities.addfriends.AddFriendsActivity
import com.example.instagr.activities.editprofile.EditProfileActivity
import com.example.instagr.models.User
import com.example.instagr.utils.FirebaseHelper
import com.example.instagr.utils.ValueEventListenerAdapter
import com.example.instagr.view.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    private val TAG = "ProfileActivity"
    private lateinit var mFirebaseHelper: FirebaseHelper

    private lateinit var mUser: User

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

        mFirebaseHelper = FirebaseHelper(this)
        mFirebaseHelper.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.asUser()!!
            profile_image.loadUserPhoto(mUser.photo)
            username_text.text = mUser.username
        })

        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mFirebaseHelper.database.child("images").child(mFirebaseHelper.currentUid()!!)
            .addValueEventListener(ValueEventListenerAdapter {
                val images = it.children.map { it.getValue(String::class.java)!! }
                images_recycler.adapter = ImagesAdapter(images + images + images)
            })
    }
}

class ImagesAdapter(private val images: List<String>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

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

class SquareImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
