@file:Suppress("BlockingMethodInNonBlockingContext")

package com.tuanha.language.ui.service

import android.os.Environment
import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tuanha.core.utils.extentions.toArrayList
import com.tuanha.core.utils.extentions.toJson
import com.tuanha.core.utils.extentions.toListObject
import com.tuanha.core.utils.extentions.toObject
import com.tuanha.coreapp.ui.servicer.BaseForegroundService
import com.tuanha.coreapp.utils.extentions.serviceScope
import com.tuanha.language.App
import com.tuanha.language.data.api.CloneApi
import com.tuanha.language.entities.Video
import com.tuanha.language.utils.clone.LatinCrawl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.android.ext.android.inject
import java.io.*
import java.net.MalformedURLException
import java.net.URL


class SyncService : BaseForegroundService() {

    private val cloneApi: CloneApi by inject()

    override fun onCreate() {
        super.onCreate()

        val latinCrawl = LatinCrawl()

        serviceScope.launch(handler + Dispatchers.IO) {

            val videoId = "uEATpbQ9md4"
            val languageCode = "en"

            val metaData = cloneApi.fetchVideoInfo(CloneApi.SubtitleParam(videoId, languageCode)).data ?: return@launch

            var subtitles = cloneApi.fetchSubtitles(CloneApi.SubtitleParam(videoId, languageCode)).data ?: return@launch

            val urlImg = metaData.videoDetails.thumbnail.thumbnails.sortedByDescending {
                it.height + it.width
            }.firstOrNull()?.url ?: return@launch

            val urlMp3 = metaData.formats.firstOrNull {
                it.mimeType.contains("audio/webm")
            }?.url ?: return@launch

            val urlMp4 = metaData.formats.firstOrNull {
                it.mimeType.contains("video/mp4") && it.audioQuality.contains("AUDIO_QUALITY_LOW")
            }?.url ?: metaData.formats.firstOrNull {
                it.mimeType.contains("audio/mp4")
            }?.url ?: return@launch

            Log.d("tuanha", "onCreate: $videoId")

            subtitles = subtitles.mapIndexed { index, subtitle ->

                subtitle.words = latinCrawl.getWord(subtitle)

                subtitle.durationStr = (((subtitles.getOrNull(index + 1)?.start ?: subtitle.end) - subtitle.start) / 1000.0).toString()
                subtitle.text = null

                subtitle
            }


            val fileSubtitle = getFile("video/metadata/$videoId.json")
            writeFile(fileSubtitle.absolutePath, subtitles.toJson())

            Log.d("tuanha", "onCreate: $videoId subtitle completed")
            Log.d("tuanha", "onCreate: $videoId img start ${urlImg}")

            val fileImg = getFile("video/metadata/$videoId.png")
            downloadFile(urlImg, fileImg)

            Log.d("tuanha", "onCreate: $videoId img completed ${urlImg}")
            Log.d("tuanha", "onCreate: $videoId mp3 start ${urlMp3}")

            val fileMp3 = getFile("video/metadata/$videoId.webm")
            downloadFile(urlMp3, fileMp3)

            Log.d("tuanha", "onCreate: $videoId mp3 completed ${urlMp3}")
            Log.d("tuanha", "onCreate: $videoId mp4 start ${urlMp4}")

            val fileMp4 = getFile("video/metadata/$videoId.mp4")
            downloadFile(urlMp4, fileMp4)

            Log.d("tuanha", "onCreate: $videoId mp4 completed ${urlMp4}")

            val fileAllVideos = getFile("video/$languageCode.json")
            val videos = readFile(fileAllVideos.absolutePath).toListObject(Video::class.java).toArrayList()
            videos.add((Video(videoId, metaData.videoDetails.title, metaData.videoDetails.keywords)))
            writeFile(fileAllVideos.absolutePath, videos.toJson())


            val pages = videos.chunked(20).mapIndexed { index, list ->

                val fileVideos = getFile("video/$languageCode/$index.json")
                writeFile(fileVideos.absolutePath, list.toJson())

                index
            }


            val fileConfig = getFile("video.json")
            val config = readFile(fileAllVideos.absolutePath).runCatching { toObject(Config::class.java) }.getOrNull() ?: Config()
            config.videos = config.videos.toMutableMap().apply {
                put(languageCode, pages.size)
            }
            writeFile(fileConfig.absolutePath, fileConfig.toJson())

            Log.d("tuanha", "onCreate: $videoId end")
        }
    }

    private fun getFile(shortPath: String): File {

        val file = File(App.shared.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + "/" + shortPath)

        file.parentFile?.parentFile?.parentFile?.parentFile?.parentFile?.takeIf { !it.exists() }?.mkdirs()
        file.parentFile?.parentFile?.parentFile?.parentFile?.takeIf { !it.exists() }?.mkdirs()
        file.parentFile?.parentFile?.parentFile?.takeIf { !it.exists() }?.mkdirs()
        file.parentFile?.parentFile?.takeIf { !it.exists() }?.mkdirs()
        file.parentFile?.takeIf { !it.exists() }?.mkdirs()

        if (!file.exists()) {
            file.createNewFile()
        }

        return file
    }

    private fun readFile(filePath: String): String {

        val builder = StringBuilder()

        var bufferedReader: BufferedReader? = null

        val file = File(filePath)

        if (file.parentFile?.exists() == false) file.parentFile?.mkdirs()

        if (!File(filePath).exists()) File(filePath).createNewFile()

        try {
            bufferedReader = BufferedReader(FileReader(File(filePath)))
        } catch (e: FileNotFoundException) {
            Log.d("tuanha", "readFile: ", e)
        }


        try {
            var row = ""
            while (bufferedReader?.readLine()?.also { row = it } != null) {
                builder.append(
                    """
                        $row
                        
                        """.trimIndent()
                )
            }
            bufferedReader?.close()
        } catch (e: IOException) {
            Log.d("tuanha", "readFile: ", e)
        }

        return builder.toString()
    }

    private fun writeFile(filePath: String, text: String?) {

        var fileWriter: FileWriter? = null

        try {
            fileWriter = FileWriter(filePath, false)
            fileWriter.write(text)
        } catch (e: IOException) {
            throw RuntimeException("IOException occurred. ", e)
        } finally {
            fileWriter?.close()
        }
    }

    private fun downloadFile(url: String, outputFile: File) {
        try {
            val u = URL(url)
            val `is`: InputStream = u.openStream()
            val dis = DataInputStream(`is`)
            val buffer = ByteArray(1024)
            var length: Int
            val fos = FileOutputStream(outputFile)
            while (dis.read(buffer).also { length = it } > 0) {
                Log.d("tuanha", "downloadFile: $length")
                fos.write(buffer, 0, length)
            }
        } catch (mue: MalformedURLException) {
            Log.e("SYNC getUpdate", "malformed url error", mue)
        } catch (ioe: IOException) {
            Log.e("SYNC getUpdate", "io error", ioe)
        } catch (se: SecurityException) {
            Log.e("SYNC getUpdate", "security error", se)
        }
    }

    @Keep
    @Parcelize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Config(

        var videos: Map<String, Int> = mapOf()
    ) : Parcelable
}