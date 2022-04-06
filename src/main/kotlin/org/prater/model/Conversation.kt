package org.prater.model

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val id: Int?,
    val user1: Int,
    val user2: Int
)
