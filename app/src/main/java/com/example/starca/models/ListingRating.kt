package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

/**
 *
 */
@ParseClassName("ListingRating")
class ListingRating : ParseObject() {

    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser) {
        put(KEY_USER, user)
    }

    fun getListingId(): String? {
        return getString(KEY_LISTING_ID)
    }

    fun setListingId(listingId: String) {
        put(KEY_LISTING_ID, listingId)
    }

    fun getRating(): Float? {
        return getDouble(KEY_RATING).toFloat()
    }

    fun setRating(rating: Float) {
        put(KEY_RATING, rating)
    }

    companion object {
        const val KEY_USER = "user"
        const val KEY_LISTING_ID = "listingId"
        const val KEY_RATING = "rating"
    }
}