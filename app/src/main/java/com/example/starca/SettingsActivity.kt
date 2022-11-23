package com.example.starca

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.starca.fragments.EditEmailDialogFragment
import com.example.starca.fragments.EditPasswordDialogFragment
import com.example.starca.fragments.EditUsernameDialogFragment
import com.example.starca.models.Image
import com.example.starca.models.Listing
import com.parse.*
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException


class SettingsActivity : AppCompatActivity(),
    EditUsernameDialogFragment.EditUsernameDialogFragmentListener,
    EditEmailDialogFragment.EditEmailDialogFragmentListener,
    EditPasswordDialogFragment.EditPasswordDialogFragmentListener {

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
            builder.setMessage("Are you sure you want to delete your accont? This can't be undone.")
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

    // Launch DialogFragment to allow user to choose where to update profile photo from
    // Called in activity_settings.xml on both the image avatar and update profile photo icon
    fun choosePhotoSelectorDialog(v: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1046)
    }
    fun loadFromUri(photoUri: Uri) : Bitmap {
        lateinit var image: Bitmap

        try {
            if (Build.VERSION.SDK_INT > 27) {
                val source : ImageDecoder.Source = ImageDecoder.createSource(this.contentResolver, photoUri)
                image = ImageDecoder.decodeBitmap(source)
            } else {
                image = MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e : IOException) {
            e.printStackTrace()
        }
        return image
    }
    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri!!, projection, null, null, null)
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor!!.moveToFirst()
        val columnIndex: Int = cursor!!.getColumnIndex(projection[0])
        val filePath: String = cursor!!.getString(columnIndex)
//        val yourSelectedImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
        return cursor!!.getString(column_index)
        cursor!!.close()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((data != null) && requestCode == 1046) {
            val photoUri : Uri = data.data!!
            val imagePath = getPath(photoUri)
            val selectedImage = loadFromUri(photoUri)
            photoFile = File(imagePath)

            ParseUser.getCurrentUser().put("profilePicture", ParseFile(photoFile))

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

//        if (photoFile != null) {
//            user.put("profilePicture", ParseFile(photoFile))
//            Log.i(TAG, "The photoFile is not null")
//        }

        user.saveInBackground(SaveCallback() {
            fun done(e: ParseException) {
                if (it != null) {
                    Log.e(TAG, it.message!!)
                    it.printStackTrace()
                }
                else {
                    Toast.makeText(applicationContext, "Saved Successfully", Toast.LENGTH_SHORT).show()
                    Glide.with(applicationContext)
                        .load(user.getParseFile("profilePicture")?.url)
                        .placeholder(R.drawable.ic_profile)
                        .into(findViewById(R.id.profilePhoto))
                }
            }
        })
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
