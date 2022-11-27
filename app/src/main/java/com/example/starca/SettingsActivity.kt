package com.example.starca

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.starca.fragments.EditEmailDialogFragment
import com.example.starca.fragments.EditPasswordDialogFragment
import com.example.starca.fragments.EditUsernameDialogFragment
import com.example.starca.models.Image
import com.example.starca.models.Listing
import com.parse.*
import com.example.starca.FileUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException


class SettingsActivity : AppCompatActivity(),
    EditUsernameDialogFragment.EditUsernameDialogFragmentListener,
    EditEmailDialogFragment.EditEmailDialogFragmentListener,
    EditPasswordDialogFragment.EditPasswordDialogFragmentListener {

    val SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1046
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val user = ParseUser.getCurrentUser()
        ParseUser.getCurrentUser().fetchIfNeeded()

        val userFullName =  findViewById<TextView>(R.id.userFullName)
        val tvCurrentUsername = findViewById<TextView>(R.id.tvCurrentUsername)
        val tvCurrentPassword = findViewById<TextView>(R.id.tvCurrentPassword)
        val tvCurrentEmail = findViewById<TextView>(R.id.tvCurrentEmail)
        val etCurrentBio =  findViewById<EditText>(R.id.etEditBio)

        val editUsernameButton = findViewById<Button>(R.id.editUsernameButton)
        val editEmailButton = findViewById<Button>(R.id.editEmailButton)
        val editPasswordButton = findViewById<Button>(R.id.editPasswordButton)
        val deleteAccountButton = findViewById<Button>(R.id.deleteAccountButton)
        val submitChangesButton = findViewById<Button>(R.id.submitChangesButton)

        // Load User Data
        val name = user.getString("firstName") + " " + user.getString("lastName")
        userFullName.text = name

        tvCurrentUsername.text = user.getString("username")

        tvCurrentPassword.text = user.getString("password")

        tvCurrentEmail.text = user.getString("email")

        etCurrentBio.setText(user.getString("bio"))

        Glide.with(applicationContext)
            .load(user.getParseFile("profilePicture")?.url)
            .placeholder(R.drawable.ic_profile)
            .into(findViewById(R.id.profilePhoto))

        editUsernameButton.setOnClickListener {
            showEditUsernameDialog()
        }

        editEmailButton.setOnClickListener {
            showEditEmailDialog()
        }

        editPasswordButton.setOnClickListener {
            showEditPasswordDialog()
        }

        submitChangesButton.setOnClickListener {
            submitChanges()
        }

        deleteAccountButton.setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete your account? This can't be undone.")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialogInt, i ->
                    deleteImages()
                    deleteListings()
                    deleteAccount()
                }
                .setNegativeButton("No") {dialogInt, i ->
                        dialogInt.cancel()
                }
            val alert : AlertDialog = builder.create()
            alert.show()
        }
    }

    private fun showEditUsernameDialog() {
        val fragmentManager : FragmentManager = supportFragmentManager
        val editUsernameDialogFragment = EditUsernameDialogFragment()
        editUsernameDialogFragment.show(fragmentManager, "fragment_edit_username")
    }
    override fun onFinishedUpdatingUsername(string: String) {
        findViewById<TextView>(R.id.tvCurrentUsername).text = string
    }

    private fun showEditEmailDialog() {
        val fragmentManager : FragmentManager = supportFragmentManager
        val editEmailDialogFragment = EditEmailDialogFragment()
        editEmailDialogFragment.show(fragmentManager, "fragment_edit_email")
    }
    override fun onFinishedUpdatingEmail(string: String) {
        findViewById<TextView>(R.id.tvCurrentEmail).text = string
    }

    private fun showEditPasswordDialog() {
        val fragmentManager : FragmentManager = supportFragmentManager
        val editPasswordDialogFragment = EditPasswordDialogFragment()
        editPasswordDialogFragment.show(fragmentManager, "fragment_edit_password")
    }
    override fun onFinishedUpdatingPassword(string: String) {
        findViewById<TextView>(R.id.tvCurrentPassword).text = string
    }

    // Called in activity_settings.xml on both the image avatar and update profile photo icon
    fun choosePhotoSelectorDialog(v: View) {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val PERMISSIONS = arrayOf<String>(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(
            this,
            PERMISSIONS,
            1
        )
        MediaStore.Images.Media.TITLE
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(packageManager) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    fun loadFromUri(photoUri: Uri): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(this.contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
            val photoUri = data.data

            // Load the image located at photoUri into selectedImage
            val selectedImage = loadFromUri(photoUri!!)

            photoFile = FileUtils().getFileFromUri(this,photoUri)

            // Load the selected image into a preview
            findViewById<CircleImageView>(R.id.profilePhoto).setImageBitmap(selectedImage)
        }
    }

    fun submitChanges() {
//        Toast.makeText(this@SettingsActivity, "Changes Saved!", Toast.LENGTH_SHORT).show()

        val user = ParseUser.getCurrentUser()
        val userName = findViewById<TextView>(R.id.tvCurrentUsername).text.toString()
        val email = findViewById<TextView>(R.id.tvCurrentEmail).text.toString()
        val password = findViewById<TextView>(R.id.tvCurrentPassword).text.toString()
        val bio = findViewById<EditText>(R.id.etEditBio).text.toString()

        // Put username
        user.put("username", userName)
        // Put email
        user.put("email", email)
        // Put bio
        user.put("bio", bio)
        // Put password
        if (password != null) {
            user.put("password", password)
        }

        if (photoFile != null) {
            user.put("profilePicture", ParseFile(photoFile))
        }

        val pFile = ParseFile(photoFile)

        pFile.saveInBackground(SaveCallback { e ->
            if (e == null) {
                user.saveInBackground {exception ->
                    if (exception != null) {
                        // Something has went wrong
                        Log.e(TAG, "Error updating profile")
                        exception.printStackTrace()
                        Toast.makeText(
                            this,
                            "Something went wrong updating your profile!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.i(TAG, "Profile updated successfully")
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.i(TAG, "Error saving file")
                e.printStackTrace()
            }
        })

//        user.saveInBackground {exception ->
//            if (exception != null) {
//                // Something has went wrong
//                Log.e(TAG, "Error updating profile")
//                exception.printStackTrace()
//                Toast.makeText(
//                    this,
//                    "Something went wrong updating your profile!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                Log.i(TAG, "Profile updated successfully")
//                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
//                Glide.with(applicationContext)
//                    .load(user.getParseFile("profilePicture")?.url)
//                    .placeholder(R.drawable.ic_profile)
//                    .into(findViewById(R.id.profilePhoto))
//            }
//        }
//        user.saveInBackground(SaveCallback() {
//            fun done(e: ParseException) {
//                if (it != null) {
//                    Log.e(TAG, it.message!!)
//                    it.printStackTrace()
//                }
//                else {
//                    Toast.makeText(applicationContext, "Saved Successfully", Toast.LENGTH_SHORT).show()
//                    Glide.with(applicationContext)
//                        .load(user.getParseFile("profilePicture")?.url)
//                        .placeholder(R.drawable.ic_profile)
//                        .into(findViewById(R.id.profilePhoto))
//                }
//            }
//        })
//        Glide.with(applicationContext)
//            .load(user.getParseFile("profilePicture")?.url)
//            .placeholder(R.drawable.ic_profile)
//            .into(findViewById(R.id.profilePhoto))
    }

    fun deleteImages() {
        val query: ParseQuery<Image> = ParseQuery.getQuery(Image::class.java)
        query.whereEqualTo("userId", ParseUser.getCurrentUser().objectId)

        query.findInBackground(object: FindCallback<Image> {
            override fun done(images: MutableList<Image>?, e: ParseException?) {
                if (e != null) {
                    Toast.makeText(this@SettingsActivity, "Error occurred in deleting listing images", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, e.toString())
                } else {
                    if (images != null) {
                        for (image in images) {
                            Log.i(TAG, "Image ID: " + image.objectId + " Listing ID: " + image.getListingId())
                            image.deleteInBackground()
                        }
                    }
                }
            }

        })
    }

    fun deleteListings() {
        val query:  ParseQuery<Listing> = ParseQuery.getQuery(Listing::class.java)
        query.whereEqualTo("userId", ParseUser.getCurrentUser().objectId)

        query.findInBackground(object: FindCallback<Listing> {
            override fun done(listings: MutableList<Listing>?, e: ParseException?) {
                if (e != null) {
                    Toast.makeText(this@SettingsActivity, "Error occurred in deleting listings", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, e.toString())
                } else {
                    if (listings != null) {
                        for (listing in listings) {
                            Log.i(TAG, "Listing ID: " + listing.objectId + " Listing Title: " + listing.getTitle())
                            listing.deleteInBackground()
                        }
                    }
                }
            }

        })
    }

    fun deleteAccount() {
        ParseUser.getCurrentUser().deleteInBackground()
        Toast.makeText(this@SettingsActivity, "Account and Data Deleted", Toast.LENGTH_SHORT).show()
        ParseUser.logOut()
        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val TAG = "SettingsActivity"
    }
}
