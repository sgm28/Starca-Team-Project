package com.example.starca.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import com.example.starca.R
import com.parse.ParseObject

/**
 * TODO: Allow users to put in their amenities here
 *      Allow them to list their price here as well.
 */
class CreateListingSubmitFragment : Fragment() {

    //val APP_TAG = "CreateListingSubmitFragment.kt"
    private var usersDataObject: ParseObject = ParseObject.create("Listing")
    //var userAddress = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_listing_submit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.create_listing_submit_button).setOnClickListener {

            //This function will receive the data from CreateListingImageFragment
            //The function uses the requestKey to get the correct data
            //The function will assign the data from CreateListingImageFragment to firstObject
            setFragmentResultListener("2") { key, bundle ->

                usersDataObject = bundle.getParcelable<ParseObject>("user")!!

                //For testing purposes/////////////////////////////////////////////////////////////////
                // val result = bundle.getString("data")
                // Do something with the result...
                //userAddress = usersDataObject.getString("addressZip").toString()
                // Log.d(APP_TAG, usersDataObject.getString("addressZip").toString())
                // Log.d(APP_TAG, userAddress)
            }
            //Call the submit post method
            //The submit post method adds hardcoded amenities values to userDataObject
            //then submits the data
            submitPost()
        }

        view.findViewById<Button>(R.id.create_listing_submit_cancel_button).setOnClickListener {

            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()

        }
    }

    private fun submitPost() {

        val listOfStrings = listOf("heat", "air condition", "web camera")
        usersDataObject.put("amenities", listOfStrings)
        ///////////////////TESTING PURPOSE//////////////////////////////////
        // Log.d(APP_TAG, userAddress)
        // Log.d(APP_TAG, usersDataObject.getString("addressZip").toString())


        usersDataObject.saveInBackground {
            e ->
            if (e != null) {
                Log.e(TAG, "submitPost: error: ${e.message}", )
            } else {
                Log.i(TAG, "submitPost: Listing Submitted.")

                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()

                Toast.makeText(requireContext(), "Submitted new listing!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "CreateListingSubmitFragment"
    }
}