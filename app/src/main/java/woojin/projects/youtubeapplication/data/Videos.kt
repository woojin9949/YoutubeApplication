package woojin.projects.youtubeapplication.data

import com.google.gson.annotations.SerializedName

data class Videos(
    @SerializedName("videos")
    val videos: List<VideoDatas>,
)

data class VideoDatas(
    @SerializedName("id")
    val id: Long,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("video_files")
    val videoFiles: List<VideoFiles>
)

data class VideoFiles(
    @SerializedName("link")
    val link: String
)
