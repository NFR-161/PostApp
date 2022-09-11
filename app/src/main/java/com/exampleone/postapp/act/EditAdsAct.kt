package com.exampleone.postapp.act

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exampleone.postapp.R
import com.exampleone.postapp.adapters.ImageAdapter
import com.exampleone.postapp.data.Ad
import com.exampleone.postapp.database.DbManager
import com.exampleone.postapp.databinding.ActivityEditAdsBinding
import com.exampleone.postapp.dialogs.DialogSpinnerHelper
import com.exampleone.postapp.frag.FragmentCloseInterface
import com.exampleone.postapp.frag.ImageListFrag
import com.exampleone.postapp.utils.CityHelper
import com.exampleone.postapp.utils.ImageManager
import com.exampleone.postapp.utils.ImagePicker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.android.gms.ads.MobileAds


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    private val dialog = DialogSpinnerHelper()
    lateinit var rootElement: ActivityEditAdsBinding
    lateinit var imageAdapter: ImageAdapter
    var chooseImageFrag: ImageListFrag? = null
    var editImagePos = 0
    private val dbManager = DbManager(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImagePicker.showSelectedImages(resultCode, requestCode, data, this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)
                } else {
                    Toast.makeText(
                        this,
                        "Approve permissions to open Pix ImagePicker",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        rootElement.vpImages.adapter = imageAdapter

    }

    fun onClickSelectCountry(view: View) {
        val listOfCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listOfCountry, rootElement.tvCountry)
        if (rootElement.tvCity.text.toString() != getString(R.string.select_city)) {
            rootElement.tvCity.text = getString(R.string.select_city)
        }
    }


    fun onClickSelectCity(view: View) {
        val selectedCountry = rootElement.tvCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)) {
            val listOfCities = CityHelper.getAllCities(selectedCountry, this)
            dialog.showSpinnerDialog(this, listOfCities, rootElement.tvCity)
        } else {
            Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
        }
    }

    fun onClickSelectCat(view: View) {
        val listOfCities = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this, listOfCities, rootElement.tvCat)
    }


    fun onClickGetImages(view: View) {
        if (imageAdapter.mainArray.size == 0) {
            ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)

        } else {
            openChooseImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
    }


    fun onClickPublish(view: View) {
        dbManager.publishAd(fillAd())
    }

    fun fillAd(): Ad {
        val ad: Ad
        rootElement.apply {
            ad = Ad(
                tvCountry.text.toString(),
                tvCity.text.toString(),
                editTel.text.toString(),
                editIndex.text.toString(),
                checkBoxWithSending.isChecked.toString(),
                tvCat.text.toString(),
                edTitle.text.toString(),
                edPrice.text.toString(),
                edDescription.text.toString(),
                dbManager.db.push().key
            )
        }
        return ad
    }


    override fun onFragClose(list: ArrayList<Bitmap>) {
        rootElement.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.upDate(list)
        chooseImageFrag = null

    }

    fun openChooseImageFrag(newList: ArrayList<String>?) {
        chooseImageFrag = ImageListFrag(this, newList)
        rootElement.scrollViewMain.visibility = View.GONE
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.place_holder, chooseImageFrag!!)
            .commit()
    }


}

