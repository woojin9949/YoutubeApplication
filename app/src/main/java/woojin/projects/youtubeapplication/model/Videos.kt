package woojin.projects.youtubeapplication.model

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
    val image: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("video_files")
    val videoFiles: List<VideoFiles>,
)

data class User(
    @SerializedName("name")
    val name: String
)

data class VideoFiles(
    @SerializedName("link")
    val link: String
)

data class Youtuber(
    val link: String,
    val name: String,
    val image: String,
)
