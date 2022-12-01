package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject

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

    companion object {
        const val KEY_CONVERSATION = "conversationId"
        const val KEY_BODY = "body"
    }
}