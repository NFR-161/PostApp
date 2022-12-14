package com.exampleone.postapp.utils

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.exampleone.postapp.act.EditAdsAct
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Array


object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 998

    private fun getOptions(imageCounter: Int): Options {

        return Options.init()
            .setCount(imageCounter) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(30) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images")

    }

    fun launcher(
        edAct: EditAdsAct,
        launcher: ActivityResultLauncher<Intent>?,
        imageCounter: Int
    ) {
        PermUtil.checkForCamaraWritePermissions(edAct) {
            val intent = Intent(edAct, Pix::class.java).apply {
                putExtra("options", getOptions(imageCounter))
            }
            launcher?.launch(intent)
        }
    }

    fun getLauncherForMultiSelectImages(edAct: EditAdsAct): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
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
            }
        }
    }

    fun getLauncherForSingleImage(edAct: EditAdsAct): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val uri = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    edAct.chooseImageFrag?.setSingleImage(uri?.get(0)!!, edAct.editImagePos)
                }
            }
        }
    }
}

