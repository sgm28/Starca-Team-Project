package com.example.starca.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray

@Parcelize
data class ListingRequest(
    val userId: String,
    val status: Int
) : Parcelable {

    companion object {
        fun fromJsonArray(requestJSONArray: JSONArray) : List<ListingRequest> {

            val requests = mutableListOf<ListingRequest>()

            for (i in 0 until requestJSONArray.length()) {
                val requestJSON = requestJSONArray.getJSONObject(i)

                requests.add(
                    ListingRequest(
                        requestJSON.getString("objectId"),
                        requestJSON.getInt("status")
                    )
                )
            }
            return requests
        }
    }
}