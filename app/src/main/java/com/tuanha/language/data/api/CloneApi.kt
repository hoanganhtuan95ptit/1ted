package com.tuanha.language.data.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tuanha.detect.data.api.retrofit.AppResponse
import com.tuanha.language.entities.Subtitle
import kotlinx.parcelize.Parcelize
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CloneApi {

    @Headers(value = ["enableDecrypt:false", "Lens-Nope:Time"])
    @POST("http://192.168.1.233:3000/subtitles")
    suspend fun fetchSubtitles(@Body param: SubtitleParam): AppResponse<List<Subtitle>>

    @Headers(value = ["enableDecrypt:false", "Lens-Nope:Time"])
    @POST("http://192.168.1.233:3000/videoInfo")
    suspend fun fetchVideoInfo(@Body param: SubtitleParam): AppResponse<VideoMetaData>

    @Keep
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class SubtitleParam(val videoId: String, val language: String)


    @Keep
    @Parcelize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class VideoMetaData(
        val formats: List<VideoMetaDataFormat> = emptyList(),
        val videoDetails: VideoMetaDataDetail = VideoMetaDataDetail()
    ) : Parcelable

    @Keep
    @Parcelize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class VideoMetaDataFormat(
        val url: String = "",
        val mimeType: String = "",
        val audioQuality: String = ""
    ) : Parcelable

    @Keep
    @Parcelize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class VideoMetaDataDetail(

        val thumbnail: VideoMetaDataThumbnail = VideoMetaDataThumbnail(),

        val title: String = "",
        val keywords: List<String> = emptyList()
    ) : Parcelable

    @Keep
    @Parcelize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class VideoMetaDataThumbnail(
        val thumbnails: List<VideoMetaDataThumbnailChild> = emptyList(),
    ) : Parcelable

    @Keep
    @Parcelize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class VideoMetaDataThumbnailChild(
        val url: String = "",
        val width: Int = 0,
        val height: Int = 0,
    ) : Parcelable
}