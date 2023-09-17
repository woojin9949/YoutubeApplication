package woojin.projects.youtubeapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
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

    //Main화면 리스트업
    private lateinit var videoAdapter: VideoAdapter

    //특정 동영상 클릭 시, 세부 리스트업 -> 영상 정보,선택한 영상을 제외한 영상 리스트업
    private lateinit var videoDetailAdapter: VideoDetailAdapter

    private lateinit var videoList: List<YoutuberItem>

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val retrofitService = setRetrofitService()
        getVideo(retrofitService)

        initMotionLayout()
        initMainVideoRecyclerView()
        initDetailVideoRecyclerView()
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

    private fun initMainVideoRecyclerView() {
        videoAdapter = VideoAdapter(context = this) { youtuberItem ->
            binding.motionLayout.setTransition(R.id.collapse, R.id.expand)
            binding.motionLayout.transitionToEnd()

            //클릭시 해당 아이템을 리스트로 생성 후, videoList에서 클릭한 영상과 동일한 영상인경우
            //Filtering을 통해 제외
            // (구조)
            //첫번째 Recycler는 헤더 => 즉, 해당 영상정보를 보여주기위해 listOf(youtuberItem)사용
            //두번째 Recycler부터는 나머지 영상들 리스트업
            val list = listOf(youtuberItem) + videoList.filter {
                it.url != youtuberItem.url
            }
            videoDetailAdapter.submitList(list)

            play(youtuberItem)
        }

        binding.videoListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }
    }

    private fun initDetailVideoRecyclerView() {
        videoDetailAdapter = VideoDetailAdapter(context = this) { youtuberItem ->
            play(youtuberItem)
            val list = listOf(youtuberItem) + videoList.filter {
                it.url != youtuberItem.url
            }
            //Background에서 실행되므로 기다렸다가 scrollToPosition을 해야함
            videoDetailAdapter.submitList(list) {
                binding.playerRecyclerView.scrollToPosition(0)
            }
        }

        binding.playerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoDetailAdapter
            itemAnimator = null
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

                //ExoPlayer에 리스너 장착-> 플레이 중인지 확인
                //Play인 경우 => controlButton pause로 변경
                //Pause인 경우 => controlButton play로 변경
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


