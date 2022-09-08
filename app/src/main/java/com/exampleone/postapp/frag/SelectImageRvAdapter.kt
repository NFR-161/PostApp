package com.exampleone.postapp.frag

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.exampleone.postapp.R
import com.exampleone.postapp.act.EditAdsAct
import com.exampleone.postapp.databinding.SelectImageFragItemBinding
import com.exampleone.postapp.utils.AdapterCallback
import com.exampleone.postapp.utils.ImageManager
import com.exampleone.postapp.utils.ImagePicker
import com.exampleone.postapp.utils.ItemTouchMoveCallback

class SelectImageRvAdapter(private val adapterCallback: AdapterCallback) :
    RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),
    ItemTouchMoveCallback.ItemTouchAdapter {


    val mainArray = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = SelectImageFragItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
//        adapterCallback.onItemDelete()
        return ImageHolder(binding, parent.context, this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {

        val targetItem = mainArray[targetPos]
        mainArray[targetPos] = mainArray[startPos]
        mainArray[startPos] = targetItem
        notifyItemMoved(startPos, targetPos)

    }

    override fun onClear() {
        notifyDataSetChanged()
    }

    class ImageHolder(
        val binding: SelectImageFragItemBinding,
        val context: Context,
        val adapter: SelectImageRvAdapter
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(bitMap: Bitmap) {

            binding.imEditImage.setOnClickListener {
                ImagePicker.getImages(
                    context as EditAdsAct,
                    1,
                    ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGE
                )
                context.editImagePos = adapterPosition

            }
            binding.imDeleteImage.setOnClickListener {
                adapter.mainArray.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                for (n in 0 until adapter.mainArray.size) adapter.notifyItemChanged(n)
                adapter.adapterCallback.onItemDelete()

            }

            binding.tvTitle.text =
                context.resources.getStringArray(R.array.title_array)[adapterPosition]
            ImageManager.chooseScaleType(binding.imageContent, bitMap)
            binding.imageContent.setImageBitmap(bitMap)

        }

    }

    fun updateAdapter(newList: List<Bitmap>, needClear: Boolean) {
        if (needClear) mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()

    }
}