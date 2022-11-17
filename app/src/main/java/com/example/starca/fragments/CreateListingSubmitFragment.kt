package com.example.starca.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.starca.R


class CreateListingSubmitFragment : Fragment() {

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
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()
            Toast.makeText(requireContext(), "Submitted new listing!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.create_listing_submit_cancel_button).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()
        }
    }
}