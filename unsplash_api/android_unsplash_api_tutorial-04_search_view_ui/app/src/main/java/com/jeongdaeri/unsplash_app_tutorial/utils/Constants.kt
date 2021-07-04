package com.jeongdaeri.unsplash_app_tutorial.utils

object Constants {
    const val TAG : String = "로그"
}

enum class SEARCH_TYPE {
    PHOTO,
    USER
}

enum class RESPONSE_STATUS {
    OKAY,
    FAIL,
    NO_CONTENT
}


object API {
    const val BASE_URL : String = "https://api.unsplash.com/"

    // 여러분꺼 하셔야됩니다!! ㅎㅎ;;
    const val CLIENT_ID : String = "jqUoxxWNWsduh73eXzcWHfKUDu_j5GUW6CnahUgW34c"

    const val SEARCH_PHOTOS : String = "search/photos"
    const val SEARCH_USERS : String = "search/users"

}
