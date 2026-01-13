package com.example.projekatfaza23.data.api

import kotlinx.serialization.Serializable

data class GoogleApiUserResponse(
    val photos : List<Photo>? = null
)

data class Photo (
    val url: String? = null
)
