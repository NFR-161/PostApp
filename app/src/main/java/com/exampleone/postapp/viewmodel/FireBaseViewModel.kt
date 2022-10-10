package com.exampleone.postapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exampleone.postapp.model.Ad
import com.exampleone.postapp.model.DbManager

class FireBaseViewModel : ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()

    fun loadAllAds() {
        dbManager.getAllAds(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }

        })
    }

    fun loadMyAds() {
        dbManager.getMyAds(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }

        })
    }

    fun loadMyFavs() {
        dbManager.getMyFavs(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }

        })
    }

    fun adViewed(ad: Ad) {
        dbManager.adViewed(ad)
    }

    fun onFavClick(ad: Ad) {
        dbManager.onFavClick(ad, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                val pos = updatedList?.indexOf(ad)

                if (pos != -1) {
                    pos?.let {
                        val favCounter =
                            if (ad.isFav) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[it] = updatedList.get(it)
                            .copy(isFav = !ad.isFav, favCounter = favCounter.toString())

                    }
                }
                liveAdsData.postValue(updatedList)
            }

        })
    }

    fun deleteItem(ad: Ad) {
        dbManager.deleteAd(ad, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList)
            }
        })
    }
}