package com.example.starca

import android.app.Application
import com.example.starca.models.User
import com.parse.Parse
import com.parse.ParseObject

class StarcaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Register the User Parse model so that we can use that class and link to the table
        ParseObject.registerSubclass(User::class.java)

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}