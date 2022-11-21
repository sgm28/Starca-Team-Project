package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Image")
class Image : ParseObject() {

    fun getUser() : ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser) {
        put(KEY_USER, user)
    }

    fun getListingId() : String? {
        return getString(KEY_LISTING)
    }

    fun setListingId(listingId: String) {
        put(KEY_LISTING, listingId)
    }

    fun getImage() : ParseFile? {
        return getParseFile(KEY_IMAGE)
    }

    fun setImage(parsefile: ParseFile) {
        put(KEY_IMAGE, parsefile)
    }


    companion object {
        const val KEY_USER = "userId"
        const val KEY_LISTING = "listingID"
        const val KEY_IMAGE = "image"
    }
}