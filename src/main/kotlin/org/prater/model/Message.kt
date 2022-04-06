package org.prater.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int?,
    val content: String,
    val userId: Int,
    val conversationId: Int,
    val dateTime: LocalDateTime
)
