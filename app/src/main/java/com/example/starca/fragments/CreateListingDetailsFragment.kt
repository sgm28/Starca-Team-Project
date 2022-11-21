package com.example.starca.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.starca.R
import com.parse.ParseObject
import com.parse.ParseUser

class CreateListingDetailsFragment : Fragment() {

    val firstObject = ParseObject.create("Listing")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_listing_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.create_listing_details_next_button).setOnClickListener {


            val user = ParseUser.getCurrentUser()
            firstObject.put("userID", user)
            firstObject.put("username", user.username.toString())
            firstObject.put("title", view.findViewById<EditText>(R.id.editTextListing).text.toString())
            firstObject.put("description", view.findViewById<EditText>(R.id.editTextDescription).text.toString())
            firstObject.put("addressStreet", view.findViewById<EditText>(R.id.editTextAddress).text.toString())
            firstObject.put("addressCity",view.findViewById<EditText>(R.id.editTextCity).text.toString())
            firstObject.put("addressState",view.findViewById<EditText>(R.id.editTextState).text.toString())
            firstObject.put("addressZip",view.findViewById<EditText>(R.id.editTextZipCode).text.toString())
            firstObject.put("dimensions",view.findViewById<EditText>(R.id.editTextDimension).text.toString())

            // creating a bundle object
            val bundle = Bundle()
            bundle.putParcelable("user", firstObject)
           // val result = firstObject
            setFragmentResult("requestKey", bundle)
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateListingImageFragment()).commit()
        }

        view.findViewById<Button>(R.id.create_listing_details_cancel_button).setOnClickListener {


            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()



        }
    }





   }