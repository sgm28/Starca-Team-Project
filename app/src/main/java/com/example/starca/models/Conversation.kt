package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject

/**
 *
 */
@ParseClassName("Conversation")
class Conversation : ParseObject() {

    fun getUsers(): List<String>? {
        return getList<String>(KEY_USERS)
    }

    fun setUsers(users: List<String>) {
        put(KEY_USERS, users)
    }

    companion object {
        const val KEY_USERS = "users"
    }
}