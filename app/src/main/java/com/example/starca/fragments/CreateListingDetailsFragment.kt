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

            //Creating ParseObject to gather the user's inputted values.
            val usersDataObject: ParseObject = ParseObject.create("Listing")
            val user = ParseUser.getCurrentUser()
            usersDataObject.put("userID", user)
            usersDataObject.put("username", user.username.toString())
            usersDataObject.put(
                "title",
                view.findViewById<EditText>(R.id.editTextListing).text.toString()
            )
            usersDataObject.put(
                "description",
                view.findViewById<EditText>(R.id.editTextDescription).text.toString()
            )
            usersDataObject.put(
                "addressStreet",
                view.findViewById<EditText>(R.id.editTextAddress).text.toString()
            )
            usersDataObject.put(
                "addressCity",
                view.findViewById<EditText>(R.id.editTextCity).text.toString()
            )
            usersDataObject.put(
                "addressState",
                view.findViewById<EditText>(R.id.editTextState).text.toString()
            )


            usersDataObject.put(
                "addressZip",
                Integer.parseInt(view.findViewById<EditText>(R.id.editTextZipCode).text.toString())
                    .toString()
            )
            usersDataObject.put(
                "dimensions",
                view.findViewById<EditText>(R.id.editTextDimension).text.toString()
            )

            // creating a bundle object to hold the usersDataObject
            val bundle = Bundle()
            bundle.putParcelable("user", usersDataObject)

            //passing the  bundle  to the Fragment manager
            setFragmentResult("1", bundle)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateListingImageFragment()).commit()
        }

        view.findViewById<Button>(R.id.create_listing_details_cancel_button).setOnClickListener {


            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment()).commit()


        }
    }


}