package com.example.gc.subscriptbea.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.days


class AddSubscriptionActivity : HMBaseActivity() {

    lateinit var subscriptionData: Subscription
    var subscripionId = ""
    var isNew = true
    var datePickerDialog: DatePickerDialog? = null
    var monthName = ""

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

    private fun updateLabel(myCalendar: Calendar, dateEditText: EditText) {
        val myFormat: String = "dd-MMM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        dateEditText.setText(sdf.format(myCalendar.time))
    }

    fun showDatePickerAction(view: View){
            val c: Calendar = Calendar.getInstance()
            val mYear: Int = c.get(Calendar.YEAR) // current year

            val mMonth: Int = c.get(Calendar.MONTH) // current month

            val mDay: Int = c.get(Calendar.DAY_OF_MONTH) // current day

            // date picker dialog
            datePickerDialog = DatePickerDialog(this,
                { view, year, monthOfYear, dayOfMonth ->

                    if (monthOfYear == 0 ) {
                        monthName = "Jan"
                    } else if (monthOfYear == 1 ) {
                        monthName = "Feb"
                    } else if (monthOfYear == 2 ) {
                        monthName = "Mar"
                    } else if (monthOfYear == 3 ) {
                        monthName = "Apr"
                    } else if (monthOfYear == 4 ) {
                        monthName = "May"
                    } else if (monthOfYear == 5 ) {
                        monthName = "Jun"
                    } else if (monthOfYear == 6 ) {
                        monthName = "Jul"
                    } else if (monthOfYear == 7 ) {
                        monthName = "Aug"
                    } else if (monthOfYear == 8 ) {
                        monthName = "Sep"
                    } else if (monthOfYear == 9 ) {
                        monthName = "Oct"
                    } else if (monthOfYear == 10 ) {
                        monthName = "Nov"
                    } else {
                        monthName = "Dec"
                    }

                    findViewById<EditText>(R.id.subscriptionStartDate).setText(


                        this.monthName + " " + dayOfMonth.toString() + ", " + year
                    )
                }, mYear, mMonth, mDay
            )
            datePickerDialog!!.show()

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