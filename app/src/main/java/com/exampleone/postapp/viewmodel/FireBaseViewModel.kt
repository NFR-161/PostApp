package com.exampleone.postapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exampleone.postapp.model.Ad
import com.exampleone.postapp.model.DbManager

class FireBaseViewModel : ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()

    fun loadAllAds(){
        dbManager.getAllAds(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                 liveAdsData.value = list
            }

        })
    }

    fun loadMyAds(){
        dbManager.getMyAds(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }

        })
    }
}