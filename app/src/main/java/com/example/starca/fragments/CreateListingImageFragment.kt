package com.example.starca.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.starca.R

class CreateListingImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_listing_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.create_listing_image_next_button).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateListingSubmitFragment()).commit()
        }

        view.findViewById<Button>(R.id.create_listing_image_cancel_button).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()
        }
    }
}