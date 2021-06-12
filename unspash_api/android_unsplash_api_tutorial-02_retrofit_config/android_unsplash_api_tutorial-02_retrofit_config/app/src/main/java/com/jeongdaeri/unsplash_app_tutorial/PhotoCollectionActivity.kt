package com.jeongdaeri.unsplash_app_tutorial

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jeongdaeri.unsplash_app_tutorial.model.Photo
import com.jeongdaeri.unsplash_app_tutorial.utils.Constants.TAG

class PhotoCollectionActivity : AppCompatActivity() {

    var photoList = ArrayList<Photo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)

        val bundle = intent.getBundleExtra("array_bundle")
        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>
        val searchTerm = intent.getStringExtra("search_term")
        Log.d(TAG, "searchTerm : ${searchTerm}, photolist size : ${photoList.size}")


    }
}