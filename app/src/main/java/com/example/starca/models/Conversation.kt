package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

/**
 *
 */
@ParseClassName("Conversation")
class Conversation : ParseObject() {

    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser) {
        put(KEY_USER, user)
    }

    fun getRecipient(): ParseUser? {
        return getParseUser(KEY_RECIPIENT)
    }

    fun setRecipient(user: ParseUser) {
        put(KEY_RECIPIENT, user)
    }

    fun getConversationId(): ParseUser? {
        return getParseUser(KEY_RECIPIENT)
    }

    companion object {
        const val KEY_USER = "user"
        const val KEY_RECIPIENT = "recipient"
    }
}