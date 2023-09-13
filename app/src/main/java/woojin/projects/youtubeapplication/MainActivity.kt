package woojin.projects.youtubeapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woojin.projects.youtubeapplication.databinding.ActivityMainBinding
import woojin.projects.youtubeapplication.model.Videos
import woojin.projects.youtubeapplication.model.YoutuberItem
import woojin.projects.youtubeapplication.service.RetrofitService

class MainActivity : AppCompatActivity() {
    //lazy 선언을 통해서 binding
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var videoDetailAdapter: VideoDetailAdapter

    private lateinit var videoList: List<YoutuberItem>

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val retrofitService = setRetrofitService()
        getVideo(retrofitService)

        initMotionLayout()
        initVideoRecyclerView()
        initPlayerVideoRecyclerView()
        initControlButton()
        initHideButton()
    }


    private fun initHideButton() {
        binding.hideButton.setOnClickListener {
            binding.motionLayout.transitionToState(R.id.hide)
            player?.pause()
        }
    }

    private fun initControlButton() {
        binding.controlButton.setOnClickListener {
            player?.let { exoPlayer ->
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                } else {
                    exoPlayer.play()
                }
            }
        }
    }

    private fun initVideoRecyclerView() {
        videoAdapter = VideoAdapter(context = this) { youtuberItem ->
            binding.motionLayout.setTransition(R.id.collapse, R.id.expand)
            binding.motionLayout.transitionToEnd()
            play(youtuberItem)
        }

        binding.videoListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }
    }

    private fun initPlayerVideoRecyclerView() {
        videoDetailAdapter = VideoDetailAdapter(context = this) { youtuberItem ->
            play(youtuberItem)
        }
        binding.playerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoDetailAdapter
        }
    }

    private fun initMotionLayout() {
        binding.motionLayout.targetView = binding.videoPlayerContainer
        binding.motionLayout.jumpToState(R.id.hide)

        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding.playerView.useController = false
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                binding.playerView.useController = (currentId == R.id.expand)
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }
        })
    }

    private fun getVideo(retrofitService: RetrofitService) {
        retrofitService.getVideos(20).enqueue(object : Callback<Videos> {
            override fun onResponse(call: Call<Videos>, response: Response<Videos>) {
                if (response.isSuccessful) {
                    //Pexels에서 받아온 영상 리스트 대입
                    videoList = response.body()?.videos?.map { videoDatas ->
                        //가져온 정보 link와 사용자 이름 추출 하여 데이터 클래스 대입
                        YoutuberItem(
                            url = videoDatas.videoFiles.first().link,
                            name = videoDatas.user.name,
                            image = videoDatas.image
                        )
                    } ?: return
                    videoAdapter.submitList(videoList)
                } else {
                    Log.e("MainActivity", "오류")
                }
            }

            override fun onFailure(call: Call<Videos>, t: Throwable) {
                Log.e("MainActivity", "${t.printStackTrace()}")
            }
        })
    }

    private fun initExoPlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer
                binding.playerView.useController = false

                exoPlayer.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)

                        if (isPlaying) {
                            binding.controlButton.setImageResource(R.drawable.baseline_pause_24)
                        } else {
                            binding.controlButton.setImageResource(R.drawable.baseline_play_arrow_24)
                        }
                    }
                })
            }
    }

    private fun play(youtuberItem: YoutuberItem) {
        player?.setMediaItem(MediaItem.fromUri(Uri.parse(youtuberItem.url)))
        player?.prepare()
        player?.play()

        binding.videoTitleTextView.text = youtuberItem.name
    }

    override fun onStart() {
        super.onStart()
        if (player == null) {
            initExoPlayer()
        }

    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initExoPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    private fun setRetrofitService(): RetrofitService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RetrofitService::class.java)
    }

}


