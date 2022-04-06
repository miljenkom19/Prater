package org.prater.db

import org.ktorm.database.Database

object DatabaseConnection {

    val database = Database.connect(
        url = "jdbc:mysql://localhost:3306/prater",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "prater",
        password = "prater"
    )
}