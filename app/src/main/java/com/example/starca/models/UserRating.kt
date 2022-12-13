package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

/**
 *
 */
@ParseClassName("UserRating")
class UserRating : ParseObject() {

    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser) {
        put(KEY_USER, user)
    }

    fun getUserToRateId(): String? {
        return getString(KEY_USER_TO_RATE_ID)
    }

    fun setUserToRateId(userToRate: String) {
        put(KEY_USER_TO_RATE_ID, userToRate)
    }

    fun getRating(): Float? {
        return getDouble(KEY_USER_RATING).toFloat()
    }

    fun setRating(rating: Float) {
        put(KEY_USER_RATING, rating)
    }

    companion object {
        const val KEY_USER = "user"
        const val KEY_USER_TO_RATE_ID = "userToRateId"
        const val KEY_USER_RATING = "rating"
    }

}