package org.prater.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.ktorm.dsl.*
import org.prater.db.DatabaseConnection
import org.prater.entities.ConversationEntity
import org.prater.entities.MessageEntity
import org.prater.entities.UserEntity
import org.prater.model.*

fun Application.configureRouting() {

    val db = DatabaseConnection.database

    routing {
        //Users
        get("/users") {
            val users = db.from(UserEntity).select().map {
                val id = it[UserEntity.id]!!
                val username = it[UserEntity.username]!!
                val password = it[UserEntity.password]!!
                User(id, username, password)
            }

            if(users.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                call.respond(HttpStatusCode.OK, users)
            }
        }

        get("/users/login") {
            val usernameCheck = call.parameters["username"]
            val passwordCheck = call.parameters["password"]

            val users = db.from(UserEntity).select().map {
                val id = it[UserEntity.id]!!
                val username = it[UserEntity.username]!!
                val password = it[UserEntity.password]!!
                User(id, username, password)
            }

            for(user in users) {
                if(user.username == usernameCheck && user.password == passwordCheck) {
                    call.respond(HttpStatusCode.OK, user)
                }
            }

            call.respond(HttpStatusCode.NotFound)

        }

        post("/users/register") {
            val user = call.receive<User>()

            val users = db.from(UserEntity).select().map {
                val id = it[UserEntity.id]!!
                val username = it[UserEntity.username]!!
                val password = it[UserEntity.password]!!
                User(id, username, password)
            }

            for(u in users) {
                if(u.username == user.username) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            val generatedId = db.insertAndGenerateKey(UserEntity) {
                set(it.username, user.username)
                set(it.password, user.password)
            }

            if((generatedId as? Int ?: 0) == 0) {
                call.respond(HttpStatusCode.ServiceUnavailable)
            } else {
                val id = generatedId as Int
                val username = user.username
                val password = user.password
                val userResponse = User(id, username, password)
                call.respond(HttpStatusCode.OK, userResponse)
            }
        }

        get("/users/id/{userId}") {
            call.sessions.set(UserSession("prater", 0))
            val userId = Integer.parseInt(call.parameters["userId"])

            val user = db.from(UserEntity)
                .select()
                .where{ UserEntity.id eq userId }
                .map {
                    val id = it[UserEntity.id]!!
                    val username = it[UserEntity.username]!!
                    val password = it[UserEntity.password]!!
                    User(id, username, password)
                }
                .firstOrNull()

            if(user == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(user)
            }
        }

        get("/users/{username}") {
            call.sessions.set(UserSession("prater", 0))
            val username = call.parameters["username"]!!

            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map {
                    val userId = it[UserEntity.id]!!
                    val userUsername = it[UserEntity.username]!!
                    val userPassword = it[UserEntity.password]!!
                    User(userId, userUsername, userPassword)
                }
                .firstOrNull()

            if(user == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(user)
            }
        }

        /*
        post("/users") {
            val userRequest = call.receive<UserRequest>()

            val insert = db.insert(UserEntity) {
                set(it.username, userRequest.username)
                set(it.password, userRequest.password)
            }

            if(insert == 0) {
                call.respond(HttpStatusCode.ServiceUnavailable)
            } else {
                call.respond(userRequest)
            }

        }
        */

        put("/users") {
            val user = call.receive<User>()

            val update = db.update(UserEntity) {
                set(it.username, user.username)
                set(it.password, user.password)
                where { it.id eq user.id!! }
            }

            if(update == 0) {
                call.respond(HttpStatusCode.ServiceUnavailable)
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/users/{user}") {
            val user = call.receive<User>()

            val delete = db.delete(UserEntity) { it.id eq user.id!! }

            if(delete == 0) {
                call.respond(HttpStatusCode.ServiceUnavailable)
            } else {
                call.respond(user)
            }

        }

        //Messages
        get("/messages/{conversationId}") {
            val conversationId = Integer.parseInt(call.parameters["conversationId"])

            val messages = db.from(MessageEntity)
                .select()
                .where{ MessageEntity.conversationId eq conversationId }
                .map {
                    val id = it[MessageEntity.id]!!
                    val content = it[MessageEntity.content]!!
                    val userId = it[MessageEntity.userId]!!
                    val convId = it[MessageEntity.conversationId]!!
                    val dateTime = it[MessageEntity.dateTime]!!.toKotlinLocalDateTime()
                    Message(id, content, userId, convId, dateTime)
                }

            call.respond(HttpStatusCode.OK, messages)
        }

        post("/messages") {
            val message = call.receive<Message>()

            val generatedId = db.insertAndGenerateKey(MessageEntity) {
                set(it.content, message.content)
                set(it.userId, message.userId)
                set(it.conversationId, message.conversationId)
                set(it.dateTime, message.dateTime.toJavaLocalDateTime())
            }

            if((generatedId as? Int ?: 0) == 0) {
                call.respond(HttpStatusCode.ServiceUnavailable)
            } else {
                val id = generatedId as Int
                val content = message.content
                val userId = message.userId
                val conversationId = message.conversationId
                val dateTime = message.dateTime
                val messageResponse = Message(id, content, userId, conversationId, dateTime)
                call.respond(HttpStatusCode.OK, messageResponse)
            }
        }

        //Conversations
        get("/conversations/{userId}") {
            val userId = Integer.parseInt(call.parameters["userId"])

            val conversations = db.from(ConversationEntity)
                .select()
                .where { (ConversationEntity.user1 eq userId) or (ConversationEntity.user2 eq userId)}
                .map {
                    val id = it[ConversationEntity.id]!!
                    val user1 = it[ConversationEntity.user1]!!
                    val user2 = it[ConversationEntity.user2]!!
                    Conversation(id, user1, user2)
                }

            call.respond(HttpStatusCode.OK, conversations)

        }

        post("/conversations") {
            val conversation = call.receive<Conversation>()

            val generatedId = db.insertAndGenerateKey(ConversationEntity) {
                set(it.user1, conversation.user1)
                set(it.user2, conversation.user2)
            }

            if((generatedId as? Int ?: 0) == 0) {
                call.respond(HttpStatusCode.ServiceUnavailable)
            } else {
                val id = generatedId as Int
                val user1 = conversation.user1
                val user2 = conversation.user2
                val conversationResponse = Conversation(id, user1, user2)
                call.respond(HttpStatusCode.OK, conversationResponse)
            }
        }

    }
}
