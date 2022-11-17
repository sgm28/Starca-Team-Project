package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("User")
class User : ParseObject() {

    fun getBio(): String? {
        return getString(KEY_BIO)
    }

    fun setBio(description: String) {
        put(KEY_BIO, description)
    }

    companion object {
        const val KEY_BIO = "bio"
    }
}