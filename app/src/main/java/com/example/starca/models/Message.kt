package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.example.starca.models.Conversation
import com.parse.ParseUser


/**
 *
 */
@ParseClassName("Message")
class Message : ParseObject() {

    fun getConversation(): String? {
        return getString(KEY_CONVERSATION)
    }

    fun setConversation(conversationId: ParseObject) {
        put(KEY_CONVERSATION, conversationId)
    }

    fun getBody(): String? {
        return getString(KEY_BODY)
    }

    fun setBody(message: String) {
        put(KEY_BODY, message)
    }

    //Issue
    fun getUserId(): String? {
        return getString(USER_ID_KEY)
    }

    fun setUserId(userId : String)
    {
        put(USER_ID_KEY, userId)
    }

    fun setRecipent(recipent: String)
    {
        put(USER_RECIPIENT, recipent)
    }

    fun getRecipent() : String? {
        return getString(USER_RECIPIENT)
    }

    companion object {
        const val KEY_CONVERSATION = "conversationId"
        const val KEY_BODY = "body"
        const val USER_ID_KEY = "userId"
        const val USER_RECIPIENT = "recipient"
    }
}