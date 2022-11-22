package com.example.starca.fragments


import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResult

import androidx.fragment.app.setFragmentResultListener

import com.example.starca.R
import com.parse.ParseFile
import com.parse.ParseObject
import java.io.File


class CreateListingImageFragment : Fragment() {

    val APP_TAG = "CreateListingImageFragment.kt"
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    private val photoFileName = "photo.jpg"
    var photoFile: File? = null
    private var usersDataObject: ParseObject = ParseObject.create("Listing")
    private lateinit var ivPreview: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //This function will receive the data from CreateListingImageFragment
        //The function uses the requestKey to get the correct data
        //The function will assign the data from CreateListingImageFragment to firstObject
        setFragmentResultListener("1") { key, bundle ->

            usersDataObject = bundle.getParcelable<ParseObject>("user")!!

            //For testing purposes/////////////////////////////////////////////////////////////////
            // val result = bundle.getString("data")
            // Do something with the result...
            //Log.v(APP_TAG, usersDataObject.getString("addressZip").toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_listing_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPreview = view.findViewById<ImageView>(R.id.imageView)

        view.findViewById<Button>(R.id.create_listing_image_next_button).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateListingSubmitFragment()).commit()


            // creating a bundle object to hold the usersDataObject
            val bundle = Bundle()
            bundle.putParcelable("user", usersDataObject)

            //passing the  bundle  to the Fragment manager
            setFragmentResult("2", bundle)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateListingSubmitFragment()).commit()

        }

        view.findViewById<Button>(R.id.create_listing_image_cancel_button).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment()).commit()

        }

        view.findViewById<Button>(R.id.uploadImageButton).setOnClickListener {
            //val user = ParseUser.getCurrentUser()
            onLaunchCamera()
            gatherPhotoData()

            //For testing purpose//////////////////
            //submitPost()
        }


    }

    private fun gatherPhotoData() {

        usersDataObject.put("PictureOfListing", ParseFile(photoFile))

    }

    //for testing purpose
   private fun submitPost() {

//        firstObject.put("userID", user)
//        firstObject.put("username", user.username.toString())
//        firstObject.put("title", "Garage")
//        firstObject.put("description", "70 square feet")
//        firstObject.put("addressStreet", "127 Nike Drive")
//        firstObject.put("addressCity","Newark")
//        firstObject.put("addressState","NJ")
//        firstObject.put("addressZip","07101")
//        firstObject.put("dimensions","50X50")
        val listOfStrings = listOf("ab", "cd")
        usersDataObject.put("amenities", listOfStrings)
        //usersDataObject.put("addressZip","07101")
        usersDataObject.saveInBackground {
            if (it != null) {
                it.localizedMessage?.let { message -> Log.e("MainActivity", message) }
            } else {
                Log.d("MainActivity", "Object saved.")
            }
        }
    }

    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(
                    requireContext(),
                    "com.codepath.fileprovider",
                    photoFile!!
                )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.

        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Within the onCreate method
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Error taking picture", Toast.LENGTH_SHORT).show()
            }
        }
    }


}