package com.example.starca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.starca.fragments.EditEmailDialogFragment
import com.example.starca.fragments.EditPasswordDialogFragment
import com.example.starca.fragments.EditUsernameDialogFragment
import com.example.starca.models.Image
import com.example.starca.models.Listing
import com.parse.*


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Find the current logged in user from the database
//        val query = ParseUser.getQuery()
//        query.whereEqualTo("objectId", ParseUser.getCurrentUser().objectId)
//        query.getFirstInBackground(object: GetCallback<ParseUser> {
//
//            override fun done(user: ParseUser, e: ParseException?) {
//                if (e != null) {
//                    Log.e(TAG, "Error fetching User")
//                } else {
//
//                    val changeProfilePhotoButton = findViewById<ImageButton>(R.id.editProfilePicture)
//                    val editUsernameButton = findViewById<Button>(R.id.editUsernameButton)
//                    val editEmailButton = findViewById<Button>(R.id.editEmailButton)
//                    val editPasswordButton = findViewById<Button>(R.id.editPasswordButton)
//                    val deleteAccountButton = findViewById<Button>(R.id.deleteAccountButton)
//                    val submitChangesButton = findViewById<Button>(R.id.submitChangesButton)
//
//                    val userFullName =  findViewById<TextView>(R.id.userFullName)
//                    val tvCurrentUsername = findViewById<TextView>(R.id.tvCurrentUsername)
//                    val tvCurrentEmail = findViewById<TextView>(R.id.tvCurrentEmail)
//                    val etCurrentBio =  findViewById<EditText>(R.id.etEditBio)

                    // Input user's name:
//                    val name = user.getString("firstName") + " " + user.getString("lastName")
//                    userFullName.text = name
//
//                    // Load the current Username
//                    tvCurrentUsername.text = user.getString("username")
//
//                    // Load the current Email
//                    tvCurrentEmail.text = user.getString("email")
//
//                    // Load the current Bio
//                    etCurrentBio.setText(user.getString("bio"))

                    // Load profile photo
//                    Glide.with(applicationContext)
//                        .load(user.getParseFile("profilePicture")?.url)
//                        .placeholder(R.drawable.ic_profile)
//                        .into(findViewById(R.id.profilePhoto))

//                    editUsernameButton.setOnClickListener {
//                        showEditUsernameDialog()
//                    }
//
//                    editEmailButton.setOnClickListener {
//                        showEditEmailDialog()
//                    }
//
//                    editPasswordButton.setOnClickListener {
//                        showEditPasswordDialog()
//                    }
//
//
//                    submitChangesButton.setOnClickListener {
//                        submitChanges()
//                    }
//
//                    deleteAccountButton.setOnClickListener {
//                        deleteImages()
//                        deleteListings()
//                        deleteAccount()
//                    }
//                }
//            }
//        })

        val currUser = ParseUser.getCurrentUser()

        val changeProfilePhotoButton = findViewById<ImageButton>(R.id.editProfilePicture)
        val editUsernameButton = findViewById<Button>(R.id.editUsernameButton)
        val editEmailButton = findViewById<Button>(R.id.editEmailButton)
        val editPasswordButton = findViewById<Button>(R.id.editPasswordButton)
        val deleteAccountButton = findViewById<Button>(R.id.deleteAccountButton)
        val submitChangesButton = findViewById<Button>(R.id.submitChangesButton)

        val userFullName =  findViewById<TextView>(R.id.userFullName)
        val tvCurrentUsername = findViewById<TextView>(R.id.tvCurrentUsername)
        val tvCurrentEmail = findViewById<TextView>(R.id.tvCurrentEmail)
        val etCurrentBio =  findViewById<EditText>(R.id.etEditBio)

        val name = currUser.getString("firstName") + " " + currUser.getString("lastName")
        userFullName.text = name

        // Load the current Username
        tvCurrentUsername.text = currUser.getString("username")

        // Load the current Email
        tvCurrentEmail.text = currUser.getString("email")

        // Load the current Bio
        etCurrentBio.setText(currUser.getString("bio"))

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
            deleteImages()
            deleteListings()
//            deleteAccount()
        }
    }

    private fun showEditUsernameDialog() {
        EditUsernameDialogFragment().show(
            supportFragmentManager, EditUsernameDialogFragment.TAG
        )
    }

    private fun showEditEmailDialog() {
        EditEmailDialogFragment().show(
            supportFragmentManager, EditEmailDialogFragment.TAG
        )
    }

    private fun showEditPasswordDialog() {
        EditPasswordDialogFragment().show(
            supportFragmentManager, EditPasswordDialogFragment.TAG
        )
    }

    // Launch DialogFragment to allow user to choose where to update profile photo from
    // Called in activity_settings.xml on both the image avatar and update profile photo icon
    fun choosePhotoSelectorDialog(v: View) {
        Toast.makeText(this@SettingsActivity, "You changed your profile picture", Toast.LENGTH_SHORT).show()
    }

    fun submitChanges() {
        Toast.makeText(this@SettingsActivity, "Changes Saved!", Toast.LENGTH_SHORT).show()
    }

    fun deleteImages() {
        val query: ParseQuery<Image> = ParseQuery.getQuery(Image::class.java)
        query.whereEqualTo("userId", ParseUser.getCurrentUser())

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
        query.whereEqualTo("userId", ParseUser.getCurrentUser())

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