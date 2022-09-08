package com.exampleone.postapp.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exampleone.postapp.databinding.ImageAdapterItemBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {
    val mainArray = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {

        val binding = ImageAdapterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    class ImageHolder(val binding: ImageAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(bitmap: Bitmap) {
            binding.imItem.setImageBitmap(bitmap)
        }

    }

    fun upDate(newList: ArrayList<Bitmap>) {
        mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }


}