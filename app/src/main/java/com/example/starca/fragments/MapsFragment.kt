package com.example.starca.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.starca.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapsFragment : Fragment() {

    private val DASHBOARD_KEY = "DASH_DATA"
    lateinit var dashboardData: DashboardFragment.DashboardData

    private val callback = OnMapReadyCallback { googleMap ->

        val geo = Geocoder(context, Locale.getDefault())

        val builder = LatLngBounds.Builder()

        for (index: Int in 0 until dashboardData.storageLocations().size) {
            val storageLocation = dashboardData.storageLocations()[index]
            try {

                val addressName = storageLocation.address
                val storageName = storageLocation.name

                val address = geo.getFromLocationName(addressName, 1)

                if (address.size==0){
                    Toast.makeText(
                        context,
                        "problem address index: ${storageLocation.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                    continue
                }
                val locLL = LatLng(address[0].latitude, address[0].longitude)


                val marker = googleMap.addMarker(
                    MarkerOptions().position(locLL).title(storageName)
                )
                builder.include(marker!!.position);

                googleMap.setOnMarkerClickListener { marker ->
                    Toast.makeText(
                        context,
                        "clicked ${marker.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                    dashboardData.parent.selectMarker(index)
                    true
                }

            } catch (e: IOException) {
                Toast.makeText(
                    context,
                    "problem: ${storageLocation.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        val bounds = builder.build()

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.10).toInt()

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)

        googleMap.moveCamera(cu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        var bundle = Bundle()
        bundle = requireArguments()
        dashboardData = bundle.getParcelable(DASHBOARD_KEY)!!

    }


}












