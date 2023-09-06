package woojin.projects.youtubeapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woojin.projects.youtubeapplication.model.Videos
import woojin.projects.youtubeapplication.databinding.ActivityMainBinding
import woojin.projects.youtubeapplication.model.Youtuber
import woojin.projects.youtubeapplication.service.RetrofitService

class MainActivity : AppCompatActivity() {
    //lazy 선언을 통해서 binding
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    //private lateinit var videoAdapter: VideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val retrofitService = setRetrofitService()
        getVideo(retrofitService)

        //videoAdapter = VideoAdapter()

//        binding.videoListRecyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = videoAdapter
//        }

    }


}

private fun getVideo(retrofitService: RetrofitService) {
    retrofitService.getVideos().enqueue(object : Callback<Videos> {
        override fun onResponse(call: Call<Videos>, response: Response<Videos>) {
            if (response.isSuccessful) {
                //Pexels에서 받아온 영상 리스트 대입
                val list = response.body()?.videos?.map { videoDatas ->
                    Youtuber(
                        videoDatas.videoFiles.first().link,
                        videoDatas.user.name,
                        videoDatas.image
                    )
                }
                //가져온 정보 link와 사용자 이름 추출 하여 데이터 클래스 대입
                Log.e("testt", list.toString())
            } else {
                Log.e("MainActivity", "오류")
            }
        }

        override fun onFailure(call: Call<Videos>, t: Throwable) {
            Log.e("MainActivity", "${t.printStackTrace()}")
        }
    })

}

private fun setRetrofitService(): RetrofitService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.pexels.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService = retrofit.create(RetrofitService::class.java)
    return retrofitService
}
