package woojin.projects.youtubeapplication.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import woojin.projects.youtubeapplication.model.Videos

interface RetrofitService {
    @Headers("Authorization: DdCvsHLrjjPG9JPdmyv3qBuioV90qAbsD0alrn8sqWQrxPNV7XkfCKgD")
    @GET("videos/popular")
    fun getVideos(
        @Query("per_page") num: Int
    ): Call<Videos>
}