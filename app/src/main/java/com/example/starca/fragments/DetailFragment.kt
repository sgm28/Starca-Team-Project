package com.example.starca.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.starca.models.ListingRequest
import com.example.starca.R
import com.example.starca.models.Conversation
import com.example.starca.models.Listing
import com.example.starca.models.Message
import com.google.gson.Gson
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.SaveCallback
import org.json.JSONArray


private const val LISTING_BUNDLE = "LISTING_BUNDLE"

enum class FLAGS(val code: Int) {
    DENIED(0),
    REQUESTED(1),
    APPROVED(2),
    BOUGHT(3)
}

class DetailFragment : Fragment() {
    private var listing: Listing? = null

    lateinit var button_bottomLeft: Button
    lateinit var button_bottomRight: Button
    lateinit var tv_requestDenied: TextView

    //lateinit var conversationId: String
    lateinit var conversation: Conversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listing = it.getParcelable(LISTING_BUNDLE)
        }

        postponeEnterTransition()
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }
/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
    */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleTv = view.findViewById<TextView>(R.id.detail_title_tv)
        val cityTv = view.findViewById<TextView>(R.id.detail_city_tv)
        val stateTv = view.findViewById<TextView>(R.id.detail_state_tv)
        val ratingRb = view.findViewById<RatingBar>(R.id.detail_rating_rb)
        val descriptionTv = view.findViewById<TextView>(R.id.detail_description_tv)
        val imageIv = view.findViewById<ImageView>(R.id.detail_image_iv)
        ViewCompat.setTransitionName(imageIv, "transition_detail_image")
        ViewCompat.setTransitionName(titleTv, "transition_detail_title")

        titleTv.text = listing?.getString("title")
        cityTv.text = listing?.getString("addressCity") + ", "
        stateTv.text = listing?.getString("addressState")
        ratingRb.rating = listing?.getDouble("listingRating")!!.toFloat()
        descriptionTv.text = listing?.getString("description")

        builder = AlertDialog.Builder(context)

        Glide.with(requireContext()).load(listing?.getParseFile("PictureOfListing")?.url)
            .into(imageIv)

        startPostponedEnterTransition()

        // okay. start simple. get the 2 buttons and textview
        button_bottomLeft = view.findViewById(R.id.button_bottomLeft)
        button_bottomRight = view.findViewById(R.id.button_bottomRight)
        tv_requestDenied = view.findViewById(R.id.tv_request_denied)

        getRequest()
    }


    fun getRequest() {

        //read flag. get request array from this listing
        val requests_arrayJSON = listing?.getJSONArray("listingRequests")

        if (requests_arrayJSON == null) {
            // listing array default to undefined.
//            Toast.makeText(context, "No Array Exists", Toast.LENGTH_SHORT).show() // confirmed. it defaults to null
            //
            val newArray = JSONArray()
            listing?.put("listingRequests", newArray)
            listing?.saveInBackground {
                nnn(newArray)
            }
        } else {
            nnn(requests_arrayJSON)
        }

    }

    // left button = cancel
    // right button = continue


    fun nnn(requests_arrayJSON: JSONArray) {
        val requests = requests_arrayJSON?.let {
            listing?.let { it1 ->
                ListingRequest.fromJsonArray(
                    it,
                    it1.objectId
                )
            }
        }
        //i have requests now. it's a list. lets get the 1 request.
        if (requests != null) {

            for (request in requests) {

                if (request.objectId == ParseUser.getCurrentUser().objectId) {
                    // we now have the request for this user at this listing.
                    //Toast.makeText(context, "u: ${ParseUser.getCurrentUser().objectId} f: ${request.status}", Toast.LENGTH_SHORT).show()
                    //get the status of that request
                    when (request.status) {
                        FLAGS.DENIED.code -> {
                            //show the denied (cancel) nav
                            displayDeniedNav()
                            button_bottomLeft.setOnClickListener(cancelRequest(requests, request))
                        }
                        FLAGS.REQUESTED.code -> {
                            //show cancel nav
                            displayAwaitingNav()
                            button_bottomLeft.setOnClickListener(cancelRequest(requests, request))
                        }
                        FLAGS.APPROVED.code -> {
                            // approved should show option to buy and option to cancel.
                            displayApprovedNav()
                            button_bottomLeft.setOnClickListener(cancelRequest(requests, request))
                            button_bottomRight.setOnClickListener(buyStorage(requests, request))
                        }
                        FLAGS.BOUGHT.code -> {
                            displayBoughtNav()
                        }
                    }
                    return
                }
            }
        }
        displayRequestNav()
        // if you get here, this mean you have no requests under this listing.
        // Give right button (continue) the ability to rent
        button_bottomRight.setOnClickListener(
            requests?.let { requestListing(it) }
        )
    }

    private fun displayApprovedNav() {
        //left cancel.
        button_bottomLeft.visibility = View.VISIBLE
        button_bottomLeft.text = "Cancel Offer"
        button_bottomLeft.setBackgroundColor(Color.parseColor("#7F0C0C"))

        // right continue
        button_bottomRight.visibility = View.VISIBLE
        button_bottomRight.text = "Rent Now"
        button_bottomRight.setBackgroundColor(Color.parseColor("#0C825F"))

        // let user know they are approved
        tv_requestDenied.visibility = View.VISIBLE
        tv_requestDenied.setTextColor(Color.parseColor("#0C825F"))
        tv_requestDenied.text = "Your Request for Rental has been Approved."
    }

    private fun displayRequestNav() {
        button_bottomRight.visibility = View.VISIBLE
        button_bottomRight.text = "Rent"
        button_bottomRight.setBackgroundColor(Color.parseColor("#0C825F"))

        button_bottomLeft.visibility = View.GONE

        tv_requestDenied.visibility = View.INVISIBLE
    }

    private fun displayDeniedNav() {
        button_bottomLeft.visibility = View.VISIBLE
        button_bottomLeft.setBackgroundColor(Color.BLACK)
        button_bottomLeft.text = "Okay"

        button_bottomRight.visibility = View.GONE

        tv_requestDenied.setTextColor(Color.BLACK)
        tv_requestDenied.visibility = View.VISIBLE
        tv_requestDenied.text = "Request for Rental Denied."
    }

    private fun displayBoughtNav() {
        button_bottomLeft.visibility = View.GONE

        button_bottomRight.visibility = View.GONE

        tv_requestDenied.setTextColor(Color.parseColor("#0C825F"))
        tv_requestDenied.visibility = View.VISIBLE
        tv_requestDenied.text = "Rented"
    }

    private fun displayAwaitingNav() {
        button_bottomLeft.visibility = View.VISIBLE
        button_bottomLeft.text = "Cancel Request"

        button_bottomRight.visibility = View.GONE

        tv_requestDenied.visibility = View.VISIBLE
        tv_requestDenied.setTextColor(Color.BLACK)
        tv_requestDenied.text = "Awaiting Approval..."
    }

    private fun requestListing(requestArray: MutableList<ListingRequest>): View.OnClickListener {
        //sendMessage(conversationId)
        return View.OnClickListener { _ ->
            // here you will create a request. you will add it to the listing array.
            Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show()

            // create an listingRequest object
            val newRequest =
                ListingRequest(
                    ParseUser.getCurrentUser().objectId,
                    FLAGS.REQUESTED.code,
                    listing!!.objectId
                )

            //add to request array
            requestArray.add(newRequest)

            val gson = Gson()

            val stArr = ArrayList<String>()
            for (request in requestArray) {
                stArr.add(gson.toJson(request))
            }

            listing?.put("listingRequests", stArr)

            listing?.saveInBackground { e ->
                if (e == null) {
                    Toast.makeText(context, "Request Submitted", Toast.LENGTH_SHORT).show()
                    displayAwaitingNav()
                    button_bottomLeft.setOnClickListener(cancelRequest(requestArray, newRequest))
                    // Start sending interest message, first check for duplicate conversation
                    checkDuplicateConversation()
                } else {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "requestListing: $e")
                }
            }
        }
    }

    lateinit var builder: AlertDialog.Builder

    private fun buyStorage(
        requestArray: MutableList<ListingRequest>,
        request: ListingRequest
    ): View.OnClickListener {
        return View.OnClickListener { _ ->

            // wow, kotlin supports null coalescing
            val price = listing?.getNumber("price") ?: "199.99"

            val tryEmail = ParseUser.getCurrentUser().email
            val email = if (ParseUser.getCurrentUser()
                    .getBoolean("emailVerified")
            ) tryEmail else "your inbox"

            confirmBuy(requestArray, request, price, email)
        }
    }

    private fun confirmBuy(requestArray: MutableList<ListingRequest>,
                           request: ListingRequest, price: java.io.Serializable, email: String) {
        builder.setMessage("Rent ${listing?.getTitle()} for ${price} per month?")
            .setCancelable(false)
            .setPositiveButton("Confirm") { dialog, id ->
                Toast.makeText(context, "receipt sent to ${email}.", Toast.LENGTH_SHORT)
                    .show()

                //change the code.
                setBought(requestArray, request)
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, id ->
                dialog.cancel()
            }

        val alert: AlertDialog = builder.create()

        alert.setTitle("Confirm rental purchase of ${listing?.getTitle()}.")
        alert.show()

        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#7F0C0C"))
    }

    private fun setBought(
        requestArray: MutableList<ListingRequest>,
        request: ListingRequest
    ) {

        // here you will create a request. you will add it to the listing array.
        Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show()

        // create an listingRequest object
        val newRequest =
            ListingRequest(
                ParseUser.getCurrentUser().objectId,
                FLAGS.BOUGHT.code,
                listing!!.objectId
            )

        //add to request array
        requestArray.add(newRequest)
        requestArray.remove(request)

        val gson = Gson()

        val stArr = ArrayList<String>()
        for (request in requestArray) {
            stArr.add(gson.toJson(request))
        }

        listing?.put("listingRequests", stArr)

        listing?.saveInBackground { e ->
            if (e == null) {
                displayBoughtNav()
            } else {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "requestListing: $e")
            }
        }

    }

    private fun cancelRequest(
        requestArray: MutableList<ListingRequest>,
        request: ListingRequest
    ): View.OnClickListener {
        return View.OnClickListener { _ ->
            //return to original request view.
            tv_requestDenied.visibility = View.INVISIBLE
            button_bottomLeft.setBackgroundColor(Color.parseColor("#0C825F"))
            button_bottomLeft.text = "Rent"

            // you are supposed to remove the listingRequest here. not the listing.
            requestArray.remove(request)

            val gson = Gson()

            val stArr = ArrayList<String>()
            for (r in requestArray) {
                stArr.add(gson.toJson(r))
            }

            listing?.put("listingRequests", stArr)

            listing?.saveInBackground { e ->
                if (e == null) {
                    Toast.makeText(context, "Request Removed", Toast.LENGTH_SHORT).show()
                    displayRequestNav()
                    // add renting functionality back to the button.
                    button_bottomRight.setOnClickListener(requestListing(requestArray))
                } else {
                    Log.e(TAG, "cancelRequest: $e")
                }
            }
        }
    }

    //////////////////
    //Logic
    //  Create the Converstation table - DONE
    // Link to message table Pointer points to Conversation ObjectID column -DONE


    // Get the user id of the poster - DONE
    // Save the listingPoster, currentUser to Conversation Fragment  database - DONE
    //Implement logic to prevent duplicate conversation - DONE
    // Implement logic to prevent duplicate entry - DONE
    //Get callback to retrieve   conversation pointer - DONE
    // send the message automatically -DONE
    // Save message, conversationPointer - DONE

    //TODO
    // Chat fragment
    // Pull
    //  if userId = currentLoginInuser display the data(listingPosterFirstname, messages,)
    // That's it
    //ParseObject message = ParseObject.create("Message");
    //message.put(Message.USER_ID_KEY, userId);
    //message.put(Message.BODY_KEY, data);
    // Using new `Message` Parse-backed model now


    //Log.d(TAG,listing!!.getUser()!!.objectId)
    // Log.d(TAG, ParseUser.getCurrentUser().objectId)


    // If conversation is NOT duplicate, save conversation

    fun sendMessage() {
        val message = Message()
        val initMessage = "Hi I am interested in " + listing!!.getTitle();
        Log.d("Message", initMessage)
        message.setBody(initMessage)
        message.setConversation(conversation)
        conversation.getUser()?.let { message.setUserId(it.objectId) }
        //The message is from the current login in user.
        //When the recipient will received the message, the message will displayed who sent it.
        message.setRecipent(ParseUser.getCurrentUser().username.toString())


        message.saveInBackground(SaveCallback {
            Toast.makeText(
                requireContext(), "Successfully created message on Parse",
                Toast.LENGTH_SHORT
            ).show()
        })
    }
/*
//Function get the objectId of login in user
    fun compareConversation(user: ParseUser, recipient: ParseUser){
        val query: ParseQuery<Conversation> = ParseQuery(Conversation::class.java)

        query.include(Conversation.KEY_USER)
        query.include(Conversation.KEY_RECIPIENT)
        query.whereEqualTo(Conversation.KEY_USER, user)
        query.findInBackground { conversation, e ->
            if (e != null) {
                Log.d(TAG, "No conversation found: $e")
            } else {
                if (!conversation.isNullOrEmpty()) {
                    Log.d(TAG, conversation.toString())
                    conversationId = conversation[0].objectId
                    Log.d(TAG, conversationId) //Retrieving conversation object
                    sendMessage(conversation[0]) //Sending the conversation to sendMessage
                }
            }
        }
    }*/

    private fun checkDuplicateConversation() {

        val user = ParseUser.getCurrentUser() //current user
        val recipient = listing!!.getUser()!! //listing owner

        val query: ParseQuery<Conversation> = ParseQuery(Conversation::class.java)

        query.include(Conversation.KEY_USER)
        query.include(Conversation.KEY_RECIPIENT)
        query.whereEqualTo(Conversation.KEY_USER, user)
        query.whereEqualTo(Conversation.KEY_RECIPIENT, recipient)
        query.findInBackground { queryConversation, e ->
            if (e != null) {
                Log.d(TAG, "Error fetching conversation $e")
            } else {
                // If conversation found, set DB conversation to current conversation
                if (!queryConversation.isNullOrEmpty()) {
                    conversation = queryConversation[0]
                    sendMessage()
                } else {
                    // If no conversation found, create one
                    createConversation(user, recipient)
                }
            }
        }
    }

    /*
    private fun checkDuplicateMessage() {
        val query: ParseQuery<Message> = ParseQuery(Message::class.java)
        query.include(Message.KEY_CONVERSATION)
        query.findInBackground { message, e ->
            if (e != null) {
                Log.d(TAG, "No conversation found: $e")
            } else {
                if (!message.isNullOrEmpty()) {
                    Log.d(TAG, message.toString())
                    //conversationId = conversation[0].objectId
                    //Log.d(TAG, conversationId)
                    Log.d(TAG, "Duplicated message found in the database. Message not sent")
                } else {
                    sendMessage()
                    Log.d(TAG, "Message sent")
                }
            }
        }
    }
*/
    private fun createConversation(user: ParseUser, recipient: ParseUser) {

        // Create new conversation
        conversation = Conversation()
        conversation.setUser(user)
        conversation.setRecipient(recipient)
        // Save conversation
        conversation.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving post $e")
            } else {
                Log.d("Create", "Created the convo")
            }
        }
        // Send interest message
        sendMessage()
    }

    companion object {
        const val TAG = "DetailFragment"
    }
}