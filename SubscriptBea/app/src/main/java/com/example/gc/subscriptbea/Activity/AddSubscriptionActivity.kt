package com.example.gc.subscriptbea.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ScrollView
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.Subscription
import com.example.gc.subscriptbea.util.Constants
import com.example.gc.subscriptbea.util.Extensions.toast
import kotlinx.android.synthetic.main.activity_add_subscription.view.*
import java.text.SimpleDateFormat
import java.util.*


class AddSubscriptionActivity : HMBaseActivity() {

    lateinit var subscriptionData: Subscription
    var subscripionId = ""
    var isNew = true

    //val datePicker = findViewById<DatePicker>(R.id.datePicker)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subscription)

        //USER IS NOT LOGGED IN
        if (firebaseAuth.currentUser == null)  {
            signOutAndGoToLogin()
        }


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
        val type = this.getTextFromViewById(R.id.subscriptionType)
        val amount = this.getTextFromViewById(R.id.subscriptionAmount)
        val startDate = this.getTextFromViewById(R.id.subscriptionStartDate)
        subscriptionData = Subscription(this.getUniqueId(), title, type, amount, startDate )
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
        val type = this.getTextFromViewById(R.id.subscriptionType)
        val amount = this.getTextFromViewById(R.id.subscriptionAmount)
        val startDate = this.getTextFromViewById(R.id.subscriptionStartDate)
        subscriptionData = Subscription(subscriptionData.id, title, type, amount, startDate)
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
                        subscriptionMap.get(SUBSCRIPTION_TITLE).toString(),
                        subscriptionMap.get(SUBSCRIPTION_TYPE).toString(),
                        subscriptionMap.get(SUBSCRIPTION_AMOUNT).toString(),
                        subscriptionMap.get(SUBSCRIPTION_START_DATE).toString())
                    if(subscriptionData != null){
                        this.setTextFromViewById(R.id.subscriptionTitle, subscriptionData.title)
                        this.setTextFromViewById(R.id.subscriptionType, subscriptionData.type)
                        this.setTextFromViewById(R.id.subscriptionAmount, subscriptionData.amount)
                        this.setTextFromViewById(R.id.subscriptionStartDate, subscriptionData.startDate)
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

        if(isNew){
            this.addSubscription()
        } else {
            this.updateSubscription()
        }
    }


    fun showDatePickerAction(view: View){

        view.subscriptionStartDate.transformIntoDatePicker(this, "MM/dd/yyyy")
        view.subscriptionStartDate.transformIntoDatePicker(this, "MM/dd/yyyy", Date())
    }

    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
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