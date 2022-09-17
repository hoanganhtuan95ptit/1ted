package com.tuanha.language.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import kotlinx.parcelize.Parcelize
import java.util.*

@Keep
@Parcelize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Video(

    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var keywords: List<String> = emptyList(),
) : Parcelable