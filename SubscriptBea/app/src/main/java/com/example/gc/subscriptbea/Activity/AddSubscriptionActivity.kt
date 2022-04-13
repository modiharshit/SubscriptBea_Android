package com.example.gc.subscriptbea.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.Subscription
import com.example.gc.subscriptbea.util.Constants
import com.example.gc.subscriptbea.util.Extensions.toast
import java.text.SimpleDateFormat
import java.util.*


class AddSubscriptionActivity : HMBaseActivity() {

    lateinit var subscriptionData: Subscription
    var subscripionId = ""
    var isNew = true
    var datePickerDialog: DatePickerDialog? = null
    var monthName = ""

    var spinnerSubscriptionType: Spinner? = null

    private val CHANNEL_ID = "SUBSCRIPTBEAR_NOTIFICATION_CHANNEL"
    private var notificationId = 1
    //val datePicker = findViewById<DatePicker>(R.id.datePicker)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subscription)

        spinnerSubscriptionType = findViewById<Spinner>(R.id.spinner_subscriptionType) as Spinner

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.subscriptionTypes,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerSubscriptionType!!.setAdapter(adapter);

        //USER IS NOT LOGGED IN
        if (firebaseAuth.currentUser == null) {
            signOutAndGoToLogin()
        }

        this.createNotificationChannel()

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
        val type = findViewById<Spinner>(R.id.spinner_subscriptionType).selectedItem.toString()
        val amount = this.getTextFromViewById(R.id.subscriptionAmount)
        val startDate = this.getTextFromViewById(R.id.subscriptionStartDate)
        subscriptionData = Subscription(this.getUniqueId(), title, amount, type, startDate )
        val subscriptions = buildMap(1){
            put(subscriptionData.id, subscriptionData)
        }
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).updateChildren(subscriptions)
            .addOnSuccessListener {
                Log.i(TAG, "Subscription Added successfully")
                toast("Subscription Added Successfully")
                this.updateUi()
                this.fireNotification("Subscription Added successfully")
            }
            .addOnFailureListener{
                Log.e(TAG, "Error Adding Subscription data", it)
            }
    }

    fun updateSubscription(){
        val title = this.getTextFromViewById(R.id.subscriptionTitle)
        val type = findViewById<Spinner>(R.id.spinner_subscriptionType).selectedItem.toString()
        val amount = this.getTextFromViewById(R.id.subscriptionAmount)
        val startDate = this.getTextFromViewById(R.id.subscriptionStartDate)
        subscriptionData = Subscription(subscriptionData.id, title, amount, type, startDate)
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).child(subscriptionData.id).setValue(subscriptionData)
            .addOnSuccessListener {
                Log.i(TAG, "Subscription Added or Updated successfully")
                toast("Subscription Updated Successfully")
                this.updateUi()
                this.fireNotification("Subscription Updated successfully")
            }
            .addOnFailureListener{
                Log.e(TAG, "Error Updating Subscription data", it)
            }
    }

    fun getSubscriptions(){
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).child(subscripionId).get()
            .addOnSuccessListener {
                if(it.value != null) {
                    Log.i(TAG, "Got value ${it.value}")
                    var subscriptionMap = it.getValue() as Map<String, Any>
                    subscriptionData = Subscription(
                        subscriptionMap.get(SUBSCRIPTION_ID).toString(),
                        subscriptionMap.get(SUBSCRIPTION_TITLE).toString(),
                        subscriptionMap.get(SUBSCRIPTION_AMOUNT).toString(),
                        subscriptionMap.get(SUBSCRIPTION_TYPE).toString(),
                        subscriptionMap.get(SUBSCRIPTION_START_DATE).toString()
                    )
                    if (subscriptionData != null) {
                        this.setTextFromViewById(R.id.subscriptionTitle, subscriptionData.title)
                        //this.setTextFromViewById(R.id.spinner_subscriptionType, subscriptionData.type)


                        var items: Array<String> =
                            resources.getStringArray(R.array.subscriptionTypes)
                        System.out.println("@@ type: " + subscriptionData.type)

                        for ((index, item )in items.withIndex()) {
                            System.out.println("@@ list: " + item)
                            if (item.equals(subscriptionData.type)) {
                                findViewById<Spinner>(R.id.spinner_subscriptionType).setSelection(index)
                            }
                        }

                        this.setTextFromViewById(R.id.subscriptionAmount, subscriptionData.amount)
                        this.setTextFromViewById(
                            R.id.subscriptionStartDate,
                            subscriptionData.startDate
                        )
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
                this.fireNotification("Subscription deleted successfully!")
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

    private fun fireNotification(msg: String){

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,CHANNEL_ID
        )
            .setSmallIcon(R.drawable.splash)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        builder.setContentTitle(msg)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId++, builder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "My Channel"
            val description = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                importance
            )
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}