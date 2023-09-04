package woojin.projects.youtubeapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import woojin.projects.youtubeapplication.data.Videos
import woojin.projects.youtubeapplication.databinding.ActivityMainBinding
import woojin.projects.youtubeapplication.service.RetrofitService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(RetrofitService::class.java)

        binding.button.setOnClickListener {
            retrofitService.getVideos().enqueue(object : Callback<Videos> {
                override fun onResponse(call: Call<Videos>, response: Response<Videos>) {
                    if (response.isSuccessful) {
                        //Pexels에서 받아온 영상 리스트 대입
                        val link = response.body()?.videos?.map { videoDatas ->
                            videoDatas.videoFiles.first().link
                        }
                    } else {
                        Log.e("MainActivity", "오류")
                    }
                }

                override fun onFailure(call: Call<Videos>, t: Throwable) {
                    Log.e("MainActivity", "${t.printStackTrace()}")
                }
            })
        }

    }
}