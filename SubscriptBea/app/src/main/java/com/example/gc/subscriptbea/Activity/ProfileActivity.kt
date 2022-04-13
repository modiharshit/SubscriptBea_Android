package com.example.gc.subscriptbea.activity

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.User
import com.example.gc.subscriptbea.util.Extensions.toast
import com.squareup.picasso.Picasso


class ProfileActivity : HMBaseActivity() {

    lateinit var user: User
    private val FIRST_NAME = "firstName"
    private val LAST_NAME = "lastName"
    private val ID = "id"
    private val EMAIL = "email"
    private val PROFILE_PICTURE = "profilePicture"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.getProfile()

        //REGISTER FOR NOTIFICATION

        //REGISTER FOR NOTIFICATION
        registerFroNotifications()

    }

    fun btnUpdateAction(view: View) {
        this.updateProfile()
    }

    fun btnLogoutAction(view: View) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.logout)
        //set message for alert dialog
        builder.setMessage(R.string.Are_you_sure_logout)
        builder.setIcon(R.drawable.splash)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            this.signOut()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which ->

        }
        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    //Firebase
    private fun getProfile() {
    firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).get()
        .addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")
            var userMap = it.getValue() as Map<String, Any>
            user = User(
                userMap.get(ID) as String,
                userMap.get(FIRST_NAME) as String,
                userMap.get(LAST_NAME) as String,
                userMap.get(EMAIL) as String,
                userMap.get(PROFILE_PICTURE) as String
            )
            if(user != null){
                this.setTextFromViewById(R.id.firstName, user.firstName)
                this.setTextFromViewById(R.id.lastName, user.lastName)
                this.setTextFromViewById(R.id.email, user.email)
                val profileImage: ImageView = findViewById(R.id.imgProfilePicture) as ImageView
                if(user.profilePicture != ""){
                    Picasso.get().load(user.profilePicture).into(profileImage)
                }
                else{
                    profileImage.setImageResource(R.drawable.splash)
                }
            }
        }.addOnFailureListener{
            Log.e(TAG, "Error getting User data", it)
        }
    }

    private fun updateProfile() {
    user.firstName = this.getTextFromViewById(R.id.firstName)
    user.lastName = this.getTextFromViewById(R.id.lastName)
    val userValues = buildMap(2){
        put(FIRST_NAME, user.firstName)
        put(LAST_NAME, user.lastName)
    }
    firebaseDatabase.child(NODE_USERS).child(user.id).updateChildren(userValues)
        .addOnSuccessListener {
            Log.i(TAG, "User Updated")
            toast("User profile updated")
        }
        .addOnFailureListener{
            Log.e(TAG, "Error updating User data", it)
        }
    }

    private fun registerFroNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(getString(R.string.notification_channel), name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun btnSendNotificationAction(View: View?) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Notifications Turned On")
            .setContentText("YOu have turned on notifications")

        val notificationIntent = Intent(this, ProfileActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(contentIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())
    }
}