package com.exampleone.postapp.database

import com.exampleone.postapp.data.Ad

interface ReadDataCallback {

    fun readData(list: List<Ad>)

}