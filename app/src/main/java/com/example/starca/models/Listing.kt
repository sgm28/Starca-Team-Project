package com.example.starca.models

import com.parse.ParseClassName
import com.parse.ParseObject

/**
 * addressStreet : String
 */
@ParseClassName("Listing")
class Listing : ParseObject() {

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

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_DESCRIPTION = "description"
        const val KEY_ADDRESS_STREET = "addressStreet"
        const val KEY_ADDRESS_CITY = "addressCity"
        const val KEY_ADDRESS_STATE = "addressState"
        const val KEY_ADDRESS_ZIP = "addressZip"
        const val KEY_AMENITIES = "amenities"
    }
}