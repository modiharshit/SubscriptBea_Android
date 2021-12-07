package com.example.gc.subscriptbea.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.Subscription
import com.example.gc.subscriptbea.util.Constants
import com.example.gc.subscriptbea.util.Extensions.toast

class AddSubscriptionActivity : HMBaseActivity() {

    lateinit var subscriptionData: Subscription
    var subscripionId = ""
    var isNew = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subscription)
    }

    override fun onStart() {
        super.onStart()
        subscripionId = intent.getStringExtra(Constants.SUBSCRIPTION_DETAIL_ID).toString()
        isNew = intent.getBooleanExtra(Constants.IS_NEW, true)

        isNew = intent.getBooleanExtra(Constants.IS_NEW, true)
        if(isNew){
            (findViewById(R.id.delete) as Button).visibility = Button.GONE
        } else {
            this.setTextFromViewById(R.id.save, "Update")
            this.getSubscriptions()
        }

    }

    fun updateUi(){
        this.goBackToHomeActivity()
    }

    //Firebase
    fun addSubscription(){
        val title = this.getTextFromViewById(R.id.subscriptionTitle)
        subscriptionData = Subscription(this.getUniqueId(), title)
        val subscriptions = buildMap(1){
            put(subscriptionData.id, subscriptionData)
        }
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).updateChildren(subscriptions)
            .addOnSuccessListener {
                Log.i(TAG, "Subscription Added successfully")
                toast("Subscription Added Successfully")
                this.updateUi()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error Adding Subscription data", it)
            }
    }

    fun updateSubscription(){
        val title = this.getTextFromViewById(R.id.title)
        subscriptionData = Subscription(subscriptionData.id, title)
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).child(subscriptionData.id).setValue(subscriptionData)
            .addOnSuccessListener {
                Log.i(TAG, "Subscription Added or Updated successfully")
                toast("Subscription Updated Successfully")
                this.updateUi()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error Updating Subscription data", it)
            }
    }

    fun getSubscriptions(){
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).child(subscripionId).get()
            .addOnSuccessListener {
                if(it.value != null){
                    Log.i(TAG, "Got value ${it.value}")
                    var subscriptionMap = it.getValue() as Map<String, Any>
                    subscriptionData = Subscription(
                        subscriptionMap.get(SUBSCRIPTION_ID).toString(),
                        subscriptionMap.get(SUBSCRIPTION_TITLE).toString())
                    if(subscriptionData != null){
                        this.setTextFromViewById(R.id.subscriptionTitle, subscriptionData.title)
                    }
                }
            }
            .addOnFailureListener{
                Log.e(TAG, "Error getting subscription data", it)
            }
    }

    fun deleteSubscription(){
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).child(subscriptionData.id).removeValue()
            .addOnSuccessListener {
                Log.i(TAG, "Subscription deleted successfully")
                toast("Subscription deleted Successfully")
                this.updateUi()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error deleting Subscription", it)
            }
    }

    fun btnAddSubscriptionDataAction(view: View){
        this.addSubscription()
        if(isNew){
            this.addSubscription()
        }else{
            this.updateSubscription()
        }
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