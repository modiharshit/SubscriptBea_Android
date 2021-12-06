package com.example.gc.subscriptbea.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.ItemsViewModel
import com.example.gc.subscriptbea.util.Extensions.toast

class AddSubscriptionActivity : HMBaseActivity() {

    lateinit var subscriptionData: ItemsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subscription)
    }

    private fun addSubscription() {

        val id = this.getUniqueId()
        val title = this.getTextFromViewById(R.id.subscriptionTitle)

        subscriptionData = ItemsViewModel("id", "Title")
        val subscriptions = buildMap(1){
            put(id, subscriptionData)
        }
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).updateChildren(subscriptions)
            .addOnSuccessListener {
                Log.i(TAG, "Subscription Added or Updated successfully")
                toast("Subscription added successfully.")
                this.goBackToHomeActivity()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error Adding or Updating Subscription data", it)
                toast("Error Adding or Updating Subscription data")
            }

    }

    private fun deleteSubscription() {

        //val id = subscriptionData.id
        val id = "1638815968620"
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).child(id).removeValue()
            .addOnSuccessListener {
                Log.i(TAG, "Subscription Added or Updated successfully")
                toast("Subscription deleted successfully.")

                this.goBackToHomeActivity()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error Adding or Updating Subscription data", it)
                toast("Error deleteing Subscription data")
            }
    }

    fun btnAddSubscriptionDataAction(view: View){
        this.addSubscription()
    }

    fun btnDeleteAction(view: View){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.delete)
        //set message for alert dialog
        builder.setMessage(R.string.Are_you_sure_delete_subscription)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            this.deleteSubscription()
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

}