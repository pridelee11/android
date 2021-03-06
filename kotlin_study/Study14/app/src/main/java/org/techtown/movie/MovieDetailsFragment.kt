package org.techtown.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_movie_details.*
import kotlinx.android.synthetic.main.fragment_movie_details.view.*
import org.techtown.movie.data.MovieList
import org.techtown.movie.data.SearchListResponse
import org.techtown.movie.data.SearchMovieList
import java.net.URLEncoder
import java.util.*
import kotlin.collections.HashMap

class MovieDetailsFragment : Fragment() {
    var index:Int = 0
    var listType:String = ""

    var favoriteSelected:Boolean = false

    lateinit var adapter: MovieCommentAdapter
    lateinit var youtubeAdapter: YoutubeMovieAdapter

    var curTitle = ""
    var curGrade = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            index = bundle.getInt("index")
            listType = bundle.getString("listType", "boxOffice")
        }

        println("MovieDetails:create called : ${index}, ${listType}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_movie_details, container, false)

        setData(rootView)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rootView.recyclerView.layoutManager = layoutManager

        adapter = MovieCommentAdapter()

        rootView.recyclerView.adapter = adapter

        adapter.listener = object : OnMovieCommentClickListener {
            override fun onItemClick(holder: MovieCommentAdapter.ViewHolder?, view: View?, position: Int) {
                val name = adapter.items[position]
                showToast("????????? ????????? : $name")
            }
        }

        // ???????????? ????????????????????? ????????? ????????? ??????
        loadComment()



        // ???????????? ?????????
        rootView.favoriteView.setOnClickListener {
            if (favoriteSelected) {
                rootView.favoriteView.setImageResource(R.drawable.favorite)
                favoriteSelected = false

                val mainActivity = activity as MainActivity

                // ???????????????????????? movieId ??????
                var movieCode:String? = null
                when(listType) {
                    "boxOffice" -> {
                        movieCode = MovieList.data[index].movieDetails?.movieCd
                    }
                    "reservation" -> {
                        movieCode = MovieList.reservationData[index].movieDetails?.movieCd
                    }
                }
                val movieId = mainActivity.queryMovieId(movieCode)

                // ???????????????????????? ??????
                mainActivity.deleteMovie(movieId)

            } else {
                rootView.favoriteView.setImageResource(R.drawable.favorite_selected)
                favoriteSelected = true

                val movieId = Utils.generateId()
                val mainActivity = activity as MainActivity

                // ????????????????????? ??????
                mainActivity.insertMovie(movieId, listType, index)

            }
        }

        // ???????????? ?????? ?????? ???
        rootView.writeCommentButton.setOnClickListener {
            val activityIntent = Intent(activity, CommentWriteActivity::class.java)
            activityIntent.putExtra("index", index)
            activityIntent.putExtra("listType", listType)
            activityIntent.putExtra("title", curTitle)
            activityIntent.putExtra("grade", curGrade)
            startActivityForResult(activityIntent, 101)
        }

        // ???????????? ?????? ?????? ???
        rootView.allCommentButton.setOnClickListener {
            val activityIntent = Intent(activity, CommentActivity::class.java)
            activityIntent.putExtra("index", index)
            activityIntent.putExtra("listType", listType)
            activityIntent.putExtra("title", curTitle)
            activityIntent.putExtra("grade", curGrade)
            startActivity(activityIntent)
        }

        // ????????? ???????????? ?????? ?????????????????? ??????
        rootView.youtubeRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        youtubeAdapter = YoutubeMovieAdapter()
        rootView.youtubeRecyclerView.adapter = youtubeAdapter

        youtubeAdapter.listener = object : OnYoutubeItemClickListener {
            override fun onItemClick(holder: YoutubeMovieAdapter.ViewHolder?, view: View?, position: Int) {
                val item = youtubeAdapter.items[position]
                showToast("????????? ????????? : ${item.title}")

                val activityIntent = Intent(activity, YoutubeActivity::class.java)
                activityIntent.putExtra("videoTitle", item.title)
                activityIntent.putExtra("videoId", item.id)
                startActivity(activityIntent)

            }
        }

        requestYoutubeSearch()


        return rootView
    }


    fun requestYoutubeSearch() {
        val apiKey = "AIzaSyDsfwl9qBPQOtf9nHzEK7pKNn5Z8AWLw08"
        val url = "https://www.googleapis.com/youtube/v3/search?key=${apiKey}&q=${URLEncoder.encode(curTitle)}&type=video&regionCode=KR&videoDuration=short&part=snippet"

        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                println("\n?????? -> ${it}")
                processResponse(it)

            },
            {
                println("\n?????? -> ${it.message}")
            }
        )

        request.setShouldCache(false)
        MainActivity.requestQueue?.add(request)
        println("\n????????? ?????? ?????????")
    }

    fun processResponse(response:String) {
        val gson = Gson()
        val searchList = gson.fromJson(response, SearchListResponse::class.java)
        println("?????? ?????? : ${searchList.items.size}")

        youtubeAdapter.items?.clear()
        searchList.items.forEach {
            val item = YoutubeItem(it.id?.videoId,
                it.snippet?.publishedAt, it.snippet?.title,
                it.snippet?.description, it.snippet?.thumbnails?.medium?.url)
            youtubeAdapter.items?.add(item)
        }
        youtubeAdapter.notifyDataSetChanged()

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {
            // ????????? ????????????
            adapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(0)
        }

    }

    fun loadComment() {
        AppInfo.databaseRef = FirebaseDatabase.getInstance().reference

        AppInfo.databaseRef.orderByKey().limitToLast(10).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadCommentList(dataSnapshot)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadItem:onCancelled : ${databaseError.toException()}")
            }
        })

    }


    fun loadCommentList(dataSnapshot: DataSnapshot) {
        val collectionIterator = dataSnapshot.children.iterator()
        if (collectionIterator.hasNext()) {
            adapter.items.clear()

            val comments = collectionIterator.next()
            val itemsIterator = comments.children.reversed().iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()

                val key = currentItem.key

                val map = currentItem.getValue() as HashMap<String, Any>
                val objectId = map.get("objectId") as String
                val author = map.get("author") as String
                val rating = map.get("rating") as Long
                val contents = map.get("contents") as String
                val recommendCount = map.get("recommendCount") as Long

                val curDate = Date(map.get("timestamp") as Long)
                val curTime = Utils.convertCommentTime(curDate)
                var commentTime = ""
                if (curTime != null) {
                    commentTime = curTime
                }

                adapter.items.add(MovieComment(objectId, author, commentTime, rating, contents, recommendCount))

                if (adapter.items.size > 2) {
                    break
                }
            }

            adapter.notifyDataSetChanged()
        }
    }



    fun checkFavoriteExist(rootView:View, movieTitle:String) {
        val mainActivity = activity as MainActivity

        val exists = mainActivity.queryMovieExists(movieTitle)
        if (exists) {
            favoriteSelected = true
            rootView.favoriteView.setImageResource(R.drawable.favorite_selected)
        } else {
            favoriteSelected = false
            rootView.favoriteView.setImageResource(R.drawable.favorite)
        }
    }


    fun setData(rootView:View) {
        Log.d(TAG, "setData called : ${index}, ${listType}")

        var imageUrl:String? = ""
        var movieTitle:String? = ""
        var gradeTitle:String? = ""
        var releaseDate:String? = ""
        var genre:String? = ""
        var runningTime:String? = ""
        var subValue1:String? = ""
        var subValue2:String? = ""
        var subValue3:String? = ""
        var director:String? = ""
        var actor:String? = ""
        var company:String? = ""
        var synopsis:String? = ""

        when(listType) {
            "boxOffice" -> {
                rootView.subTitleTextView1.text = "?????? / ?????????"
                rootView.subTitleTextView2.text = "?????? ??????"
                rootView.subTitleTextView3.text = "?????? ?????????"

                // RatingBar??? ??????????????? ??????
                rootView.gradeRatingBar.visibility = View.GONE

                val movieData = MovieList.data[index]
                Log.d(TAG, "MovieData -> ${movieData.toString()}")

                imageUrl = movieData.tmdbMovieResult?.poster_path
                movieTitle = movieData.movieInfo?.movieNm
                gradeTitle = movieData.movieDetails?.audits?.get(0)?.watchGradeNm
                releaseDate = movieData.movieInfo?.openDt
                genre = movieData.movieDetails?.genres?.get(0)?.genreNm
                runningTime = movieData.movieDetails?.showTm

                val subValue1Str = movieData.movieInfo?.audiCnt
                subValue1 = Utils.addComma(subValue1Str?.toInt()) + "???"

                subValue2 = movieData.movieInfo?.rankInten

                val subValue3Str = movieData.movieInfo?.audiAcc
                subValue3 = Utils.addComma(subValue3Str?.toInt()) + "???"

                director = movieData.movieDetails?.directors?.get(0)?.peopleNm
                actor = movieData.movieDetails?.actors?.get(0)?.peopleNm +
                        "(" + movieData.movieDetails?.actors?.get(0)?.cast + ")"
                company = movieData.movieDetails?.companys?.get(0)?.companyNm
                synopsis = movieData.tmdbMovieResult?.overview
            }
            "reservation" -> {
                rootView.subTitleTextView1.text = "?????? / ????????? ??????"
                rootView.subTitleTextView2.text = "????????? ??????"
                rootView.subTitleTextView3.text = "?????????"

                // RatingBar??? ???????????? ??????
                rootView.gradeRatingBar.visibility = View.VISIBLE

                val movieSearchData = MovieList.reservationData[index]
                Log.d(TAG, "MovieSearchData -> ${movieSearchData.toString()}")

                imageUrl = movieSearchData.tmdbMovieResult?.poster_path
                movieTitle = movieSearchData.movieDetails?.movieNm
                gradeTitle = movieSearchData.movieDetails?.audits?.get(0)?.watchGradeNm

                val releaseDateStr = movieSearchData.movieDetails?.openDt
                releaseDate = Utils.dateFormat2.format(Utils.dateFormat.parse(releaseDateStr))

                genre = movieSearchData.movieDetails?.genres?.get(0)?.genreNm
                runningTime = movieSearchData.movieDetails?.showTm
                subValue1 = MovieList.reservation[index].score2
                subValue2 = MovieList.reservation[index].score1
                subValue3 = MovieList.reservation[index].reserve + "%"
                director = movieSearchData.movieDetails?.directors?.get(0)?.peopleNm
                actor = movieSearchData.movieDetails?.actors?.get(0)?.peopleNm +
                        "(" + movieSearchData.movieDetails?.actors?.get(0)?.cast + ")"
                company = movieSearchData.movieDetails?.companys?.get(0)?.companyNm
                synopsis = movieSearchData.tmdbMovieResult?.overview
            }
        }

        // ????????? ?????????
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            val url = "http://image.tmdb.org/t/p/w200${imageUrl}"

            Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .into(rootView.thumbnailView)
        }

        // ?????????
        rootView.movieTitleView.text = movieTitle

        // ????????????
        var grade = 0
        if (gradeTitle != null) {
            if (gradeTitle.indexOf("??????") > -1) {
                grade = 0
                rootView.ratingView.setImageResource(R.drawable.grade_all)
            } else if (gradeTitle.indexOf("12") > -1) {
                grade = 1
                rootView.ratingView.setImageResource(R.drawable.grade_12)
            } else if (gradeTitle.indexOf("15") > -1) {
                grade = 2
                rootView.ratingView.setImageResource(R.drawable.grade_15)
            } else if (gradeTitle.indexOf("19") > -1) {
                grade = 3
                rootView.ratingView.setImageResource(R.drawable.grade_19)
            }
        }

        // ?????????
        rootView.releaseDateView.text = releaseDate

        // ??????
        rootView.genreView.text = genre

        // ????????????
        rootView.runningTimeView.text = runningTime

        // ??????
        rootView.rankingView.text = "${index+1}???"

        // ????????? / ??????
        rootView.subValueTextView1.text = subValue1

        // ?????? / ??????
        rootView.subValueTextView2.text = subValue2

        val rating = subValue2?.toFloat()
        if (rating != null) {
            rootView.gradeRatingBar.rating = rating / 2.0F
        }

        // ????????? / ?????????
        rootView.subValueTextView3.text = subValue3

        // ??????
        rootView.directorView.text = director

        // ??????
        rootView.actorView.text = actor

        // ?????????
        rootView.companyView.text = company

        // ?????????
        rootView.synopsisView.text = synopsis

        // ???????????????????????? ???????????? ???????????? ???????????? ????????? ??????
        checkFavoriteExist(rootView, movieTitle!!)

        curTitle = movieTitle
        curGrade = grade

    }


    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "MovieDetailsFragment"

        fun newInstance(index:Int?, listType:String?): MovieDetailsFragment {
            val fragment = MovieDetailsFragment()

            val bundle = Bundle()
            bundle.putInt("index", index!!)
            bundle.putString("listType", listType)
            fragment.arguments = bundle

            return fragment
        }
    }

}