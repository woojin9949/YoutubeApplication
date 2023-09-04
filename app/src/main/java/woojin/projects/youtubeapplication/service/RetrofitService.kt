package woojin.projects.youtubeapplication.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import woojin.projects.youtubeapplication.data.Videos

interface RetrofitService {
    @Headers("Authorization: DdCvsHLrjjPG9JPdmyv3qBuioV90qAbsD0alrn8sqWQrxPNV7XkfCKgD")
    @GET("videos/popular")
    fun getVideos(): Call<Videos>
}