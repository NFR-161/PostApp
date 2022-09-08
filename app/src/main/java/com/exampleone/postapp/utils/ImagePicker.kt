package com.exampleone.postapp.utils

import android.content.Intent
import android.graphics.Bitmap
import android.opengl.Visibility
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.exampleone.postapp.act.EditAdsAct
import com.fxn.pix.Options
import com.fxn.pix.Pix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 998

    fun getImages(context: AppCompatActivity, imageCounter: Int, rCode: Int) {
        val options: Options = Options.init()
            .setRequestCode(rCode) //Request code for activity results
            .setCount(imageCounter) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(30) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images") //Custom Path For media Storage


        Pix.start(context, options)
    }

    fun showSelectedImages(resultCode: Int, requestCode: Int, data: Intent?, edAct: EditAdsAct) {

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_GET_IMAGES) {
            if (data != null) {

                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                if (uris?.size!! > 1 && edAct.chooseImageFrag == null) {
                    edAct.openChooseImageFrag(uris)

                } else if (edAct.chooseImageFrag != null) {
                    edAct.chooseImageFrag?.updateAdapter(uris)

                } else if (uris.size == 1 && edAct.chooseImageFrag == null) {

                    CoroutineScope(Dispatchers.Main).launch {
                        edAct.rootElement.pBarLoad.visibility = View.VISIBLE
                        val bitMapArray = ImageManager.imageResize(uris) as ArrayList<Bitmap>
                        edAct.rootElement.pBarLoad.visibility = View.GONE
                        edAct.imageAdapter.upDate(bitMapArray)
                    }
                }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_GET_SINGLE_IMAGE) {
            if (data != null) {
                val uri = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                edAct.chooseImageFrag?.setSingleImage(uri?.get(0)!!, edAct.editImagePos)
            }
        }
    }
}

