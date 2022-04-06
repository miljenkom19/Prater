package org.prater.entities

import org.ktorm.schema.Table
import org.ktorm.schema.int

object ConversationEntity : Table<Nothing>(tableName = "conversation") {
    val id =                    int("id").primaryKey()
    val user1 =                 int("user1")
    val user2 =                 int("user2")
}