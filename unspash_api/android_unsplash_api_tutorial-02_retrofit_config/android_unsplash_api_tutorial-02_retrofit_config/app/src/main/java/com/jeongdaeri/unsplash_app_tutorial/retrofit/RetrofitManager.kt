package com.jeongdaeri.unsplash_app_tutorial.retrofit

import android.util.Log
import com.google.gson.JsonElement
import com.jeongdaeri.unsplash_app_tutorial.utils.API
import com.jeongdaeri.unsplash_app_tutorial.utils.Constants.TAG
import com.jeongdaeri.unsplash_app_tutorial.model.Photo
import com.jeongdaeri.unsplash_app_tutorial.utils.RESPONSE_STATE
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat

class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }

    // 레트로핏 인터페이스 가져오기
    private val iRetrofit : IRetrofit? = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)


    // 사진 검색 api 호출
    fun searchPhotos(searchTerm: String?, completion: (RESPONSE_STATE, ArrayList<Photo>?) -> Unit){

        val term = searchTerm.let {
            it
        }?: ""

//        val term = searchTerm ?: ""

        val call = iRetrofit?.searchPhotos(searchTerm = term).let {
            it
        }?: return
//        val call = iRetrofit?.searchPhotos(searchTerm = term) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement>{

            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")

                completion(RESPONSE_STATE.FAIL, null)
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")

                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            //json 형식에 { 있으면 asJsonObject로 변환하고, 각 item은 get(name)
                            val body = it.asJsonObject

                            val total = body.get("total").asInt
                            val total_pages = body.get("total_pages").asInt
                            val results = body.getAsJsonArray("results")

                            var parsedPhotoDataArray = ArrayList<Photo>()

                            results.forEach { resultItem ->
                                val resultItemObject = resultItem.asJsonObject
                                val user = resultItemObject.get("user").asJsonObject
                                val username = user.get("username").asString
                                val likesCount =  resultItemObject.get("likes").asInt
                                val thumbnailLink = resultItemObject.get("urls").asJsonObject.get("thumb").asString
                                val createdAt = resultItemObject.get("created_at").asString

                                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                val formatter = SimpleDateFormat("yyyy년\nMM월dd일")
                                val outputDateString = formatter.format(parser.parse(createdAt))

                                val photoItem = Photo(
                                    autor = username,
                                    likesCount = likesCount,
                                    thumbnail = thumbnailLink,
                                    createdAt = outputDateString
                                )
                                parsedPhotoDataArray.add(photoItem)
                            }

                            completion(RESPONSE_STATE.OKAY , parsedPhotoDataArray)
                        }

                    }
                }

            }

        })
    }


}
