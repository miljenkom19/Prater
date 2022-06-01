package org.prater.entities

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ImageEntity: Table<Nothing>(tableName = "image") {
    val id =                    int("id").primaryKey()
    val data =                  varchar("data")
}