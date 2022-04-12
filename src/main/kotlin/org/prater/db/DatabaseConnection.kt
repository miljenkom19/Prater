package org.prater.db

import org.ktorm.database.Database

object DatabaseConnection {

    val database = Database.connect(
        url = "jdbc:mysql://acw2033ndw0at1t7.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/cnwygwyrd98zflfk",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "ltyc7ni6n3q3t3a4",
        password = "ixujgdkuy0dheqnk"
    )
}