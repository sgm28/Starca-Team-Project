package com.example.starca.models

import android.util.Log
import com.example.starca.models.ListingRequest
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

/**
 * addressStreet : String
 */
@ParseClassName("Listing")
class Listing : ParseObject() {

    fun getID(): String? {
        return getString(KEY_ID)
    }

    fun getTitle(): String? {
        return getString(KEY_TITLE)
    }

    fun setTitle(title: String) {
        put(KEY_TITLE, title)
    }

    fun getDescription(): String? {
        return getString(KEY_DESCRIPTION)
    }

    fun setDescription(description: String) {
        put(KEY_DESCRIPTION, description)
    }

    fun getAddressStreet(): String? {
        return getString(KEY_ADDRESS_STREET)
    }

    fun setAddressStreet(addressStreet: String) {
        put(KEY_ADDRESS_STREET, addressStreet)
    }

    fun getAddressCity(): String? {
        return getString(KEY_ADDRESS_CITY)
    }

    fun setAddressCity(addressCity: String) {
        put(KEY_ADDRESS_CITY, addressCity)
    }

    fun getAddressState(): String? {
        return getString(KEY_ADDRESS_STATE)
    }

    fun setAddressState(addressState: String) {
        put(KEY_ADDRESS_STATE, addressState)
    }

    fun getAddressZip(): String? {
        return getString(KEY_ADDRESS_ZIP)
    }

    fun setAddressZip(addressZip: String) {
        put(KEY_ADDRESS_ZIP, addressZip)
    }

    // TODO: array for amenities?
    fun getAmenities(): String? {
        return getString(KEY_AMENITIES)
    }

    fun setAmenities(amenities: String) {
        put(KEY_ADDRESS_CITY, amenities)
    }

    fun getDimensions(): String? {
        return getString(KEY_DIMENSIONS)
    }

    fun setDimensions(dimensions: String) {
        put(KEY_DIMENSIONS, dimensions)
    }

    fun getImage(): ParseFile? {
        return getParseFile(KEY_IMAGE)
    }

    fun setImage(image: ParseFile) {
        put(KEY_IMAGE, image)
    }

    fun getRating(): Float? {
        return getDouble(KEY_RATING).toFloat()
    }

    fun setRating(rating: Float) {
        put(KEY_RATING, rating)
    }

    fun getPrice(): Float? {
        return getDouble(KEY_PRICE).toFloat()
    }

    fun setPrice(user: Float) {
        put(KEY_PRICE, user)
    }

    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser) {
        put(KEY_USER, user)
    }

    companion object {
        const val REQUEST_KEY = "1"
        const val KEY_TITLE = "title"
        const val KEY_DESCRIPTION = "description"
        const val KEY_ADDRESS_STREET = "addressStreet"
        const val KEY_ADDRESS_CITY = "addressCity"
        const val KEY_ADDRESS_STATE = "addressState"
        const val KEY_ADDRESS_ZIP = "addressZip"
        const val KEY_AMENITIES = "amenities"
        const val KEY_DIMENSIONS = "dimensions"
        const val KEY_IMAGE = "PictureOfListing"
        const val KEY_RATING = "listingRating"
        const val KEY_PRICE = "price"
        const val KEY_USER = "userID"
        const val KEY_ID = "objectId"
    }
}
