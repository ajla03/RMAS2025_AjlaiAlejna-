package com.example.projekatfaza23.data.api

import kotlinx.serialization.Serializable
import okio.Source

@Serializable
data class GoogleApiUserResponse(
    val photos : List<Photo>? = null
)

@Serializable
data class Photo (
    val url: String? = null
)
