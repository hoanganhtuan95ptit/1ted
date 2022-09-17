package com.tuanha.language.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.math.roundToInt

@Keep
@Parcelize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Subtitle(

    var id: String = UUID.randomUUID().toString()
) : Parcelable {

    @JsonProperty("text")
    var text: String? = null

    @JsonProperty("start")
    var startStr: String = ""

    @JsonProperty("duration")
    var durationStr: String = ""


    val end: Int
        @JsonIgnore get() = ((startStr.toDouble() + durationStr.toDouble()) * 1000).roundToInt()

    val start: Int
        @JsonIgnore get() = (startStr.toDouble() * 1000).roundToInt()

    val content: String
        @JsonIgnore get() = text ?: ""

    var words: List<Word> = emptyList()
}