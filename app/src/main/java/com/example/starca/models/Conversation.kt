package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

/**
 *
 */
@ParseClassName("Conversation")
class Conversation : ParseObject() {

    fun getYou(): ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setYou(user: ParseUser) {
        put(KEY_USER, user)
    }

    fun getOtherPerson(): ParseUser? {
        return getParseUser(KEY_RECIPIENT)
    }

    fun setOtherPerson(user: ParseUser) {
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