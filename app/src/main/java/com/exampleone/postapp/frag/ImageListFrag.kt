package com.exampleone.postapp.frag

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampleone.postapp.R
import com.exampleone.postapp.act.EditAdsAct
import com.exampleone.postapp.databinding.ListImageFragBinding
import com.exampleone.postapp.dialoghelper.ProgressDialog
import com.exampleone.postapp.utils.AdapterCallback
import com.exampleone.postapp.utils.ImageManager
import com.exampleone.postapp.utils.ImagePicker
import com.exampleone.postapp.utils.ItemTouchMoveCallback
import kotlinx.coroutines.*

class ImageListFrag(
    private val fragCloseInterface: FragmentCloseInterface,
    private val newList: ArrayList<String>?
) :
    BaseAdsFrag(), AdapterCallback {

    val adapter = SelectImageRvAdapter(this)

    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val itemTouchHelper = ItemTouchHelper(dragCallback)
    private var addImageItem: MenuItem? = null

    val coroutineScope = CoroutineScope(Dispatchers.Main)

    lateinit var binding: ListImageFragBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ListImageFragBinding.inflate(layoutInflater)
        adView = binding.adView
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolBar()

        binding.apply {
            itemTouchHelper.attachToRecyclerView(rcViewSelectImage)
            rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
            rcViewSelectImage.adapter = adapter
        }


        if (newList != null) resizeSelectedImages(newList, true)
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
        coroutineScope.cancel()
    }

    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFrag)
            ?.commit()
    }


    fun resizeSelectedImages(newList: ArrayList<String>, needClear: Boolean) {

        coroutineScope.launch {
            val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainArray.size > 2) addImageItem?.isVisible = false
        }
    }

    private fun setUpToolBar() {
        binding.apply {

            binding.tb.inflateMenu(R.menu.menu_choose_image)
            val deleteItem = binding.tb.menu.findItem(R.id.id_delete_image)
            addImageItem = binding.tb.menu.findItem(R.id.id_add_image)

            binding.tb.setNavigationOnClickListener {
                showInterAd()
            }

            deleteItem.setOnMenuItemClickListener {
                adapter.updateAdapter(ArrayList(), true)
                addImageItem?.isVisible = true
                true
            }
            addImageItem?.setOnMenuItemClickListener {
                val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
                ImagePicker.getImages(
                    context as AppCompatActivity,
                    imageCount,
                    ImagePicker.REQUEST_CODE_GET_IMAGES
                )
                true
            }
        }
    }

    fun updateAdapter(newList: ArrayList<String>) {
        resizeSelectedImages(newList, false)
    }

    fun setSingleImage(uri: String, pos: Int) {
        val pBar = binding.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)
        coroutineScope.launch {
            Log.d("MyLog", "1")
            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(listOf(uri))
            Log.d("MyLog", "2")
            pBar.visibility = View.GONE
            adapter.mainArray[pos] = bitmapList[0]
            Log.d("MyLog", "3")
            adapter.notifyItemChanged(pos)
        }
    }
}


