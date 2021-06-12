package com.jeongdaeri.unsplash_app_tutorial.model

import java.io.Serializable

data class Photo(var thumbnail: String?,
                var autor: String?,
                var createdAt: String?,
                var likesCount: Int?) : Serializable {

                }
