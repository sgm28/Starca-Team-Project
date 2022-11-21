package com.example.starca.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.starca.R
import com.parse.ParseObject

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


            // creating a bundle object
            firstObject.put("title", "Garage")
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