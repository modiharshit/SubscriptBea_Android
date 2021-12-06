package com.example.gc.subscriptbea.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.User
import com.google.gson.Gson

class ProfileActivity : HMBaseActivity() {

    lateinit var user: User
    private val FIRST_NAME = "firstName"
    private val LAST_NAME = "lastName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.getProfile()
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
        builder.setIcon(android.R.drawable.ic_dialog_alert)

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
        firebaseDatabase.child("users").child(firebaseAuth.uid.toString()).get()
            .addOnSuccessListener {
                Log.i(TAG, "Got value ${it.value}")
                user = Gson().fromJson(it.value.toString(), User::class.java)
                if(user != null){
                    this.setTextFromViewById(R.id.firstName, user.firstName)
                    this.setTextFromViewById(R.id.lastName, user.lastName)
                    this.setTextFromViewById(R.id.email, user.email)
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
                this.goBackToHomeActivity()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error updating User data", it)
            }
    }
}