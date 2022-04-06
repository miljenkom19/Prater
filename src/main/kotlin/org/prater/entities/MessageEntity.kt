package org.prater.entities

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object MessageEntity : Table<Nothing>(tableName = "message") {
    val id =                    int("id").primaryKey()
    val content =               varchar("content")
    val userId =                int("userId")
    val conversationId =        int("conversationId")
    val dateTime =              datetime("dateTime")
}