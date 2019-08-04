package com.example.instagr.screens.profile

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.instagr.R
import com.example.instagr.screens.common.SimpleCallback
import com.example.instagr.screens.common.loadImage

class ImagesAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(val image: ImageView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(image)
    private var images = listOf<String>()

    fun updateImages(newImages: List<String>) {
        val diffResult = DiffUtil.calculateDiff(
            SimpleCallback(
                images,
                newImages
            ) { it })
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