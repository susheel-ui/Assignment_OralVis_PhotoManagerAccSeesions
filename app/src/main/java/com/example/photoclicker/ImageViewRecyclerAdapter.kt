package com.example.photoclicker

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photoclicker.databinding.ImageViewBinding

class ImageViewRecyclerAdapter(val imageList: ArrayList<Bitmap>): RecyclerView.Adapter<ImageViewRecyclerAdapter.ImageViewHolder>() {

   class ImageViewHolder(val binding: ImageViewBinding) : RecyclerView.ViewHolder(binding.root){

   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val binding = ImageViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = imageList[position]
        holder.binding.imageView.setImageBitmap(image)
    }

}