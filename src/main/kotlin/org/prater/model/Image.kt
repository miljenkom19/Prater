package org.prater.model

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: Int?,
    val data: String
)
