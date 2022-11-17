package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject

/**
 * firstName : String
 * lastName : String
 * email : String
 * rating : Float
 * bio : String
 */
@ParseClassName("User")
class User : ParseObject() {

    fun getFirstName(): String? {
        return getString(KEY_FIRST_NAME)
    }

    fun setFirstName(firstName: String) {
        put(KEY_FIRST_NAME, firstName)
    }

    fun getLastName(): String? {
        return getString(KEY_LAST_NAME)
    }

    fun setLastName(lastName: String) {
        put(KEY_LAST_NAME, lastName)
    }

    fun getEmail(): String? {
        return getString(KEY_EMAIL)
    }

    fun setEmail(email: String) {
        put(KEY_EMAIL, email)
    }

    fun getRating(): String? {
        return getString(KEY_RATING)
    }

    fun setRating(rating: Float) {
        put(KEY_RATING, rating)
    }

    fun getBio(): String? {
        return getString(KEY_BIO)
    }

    fun setBio(description: String) {
        put(KEY_BIO, description)
    }

    companion object {
        const val KEY_FIRST_NAME = "firstName"
        const val KEY_LAST_NAME = "lastName"
        const val KEY_EMAIL = "email"
        const val KEY_RATING = "rating"
        const val KEY_BIO = "bio"
    }
}