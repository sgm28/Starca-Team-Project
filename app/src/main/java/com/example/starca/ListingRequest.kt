package com.example.starca

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
data class ListingRequest(
    val objectId: String,
    val status: Int,
    val listingId: String
) : Parcelable {

    companion object {
        fun fromJsonArray(requestJSONArray: JSONArray, listingId: String) : MutableList<ListingRequest> {

            val requests = mutableListOf<ListingRequest>()
            Log.e("DetailFragment", "fromJsonArray: $requestJSONArray", )

            for (i in 0 until requestJSONArray.length()) {
                val str = requestJSONArray.get(i).toString()
                val requestJSON = JSONObject(str)

                requests.add(
                    ListingRequest(
                        requestJSON.getString("objectId"),
                        requestJSON.getInt("status"),
                        listingId
                    )
                )
            }
            return requests
        }
    }
}