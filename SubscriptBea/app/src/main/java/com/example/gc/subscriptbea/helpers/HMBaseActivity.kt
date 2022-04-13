package com.example.gc.subscriptbea.helpers

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gc.subscriptbea.activity.HomeActivity
import com.example.gc.subscriptbea.activity.LoginActivity
import com.example.gc.subscriptbea.util.Extensions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

open class HMBaseActivity : AppCompatActivity() {

    val NODE_USERS = "users"
    val NODE_SUBSCRIPTIONS = "subscriptions"
    protected val SUBSCRIPTION_ID = "id"
    protected val SUBSCRIPTION_TITLE = "title"
    protected val SUBSCRIPTION_TYPE = "type"
    protected val SUBSCRIPTION_AMOUNT = "amount"
    protected val SUBSCRIPTION_START_DATE = "startDate"

    protected val TAG: String = this.javaClass.simpleName
    protected lateinit var firebaseAuth: FirebaseAuth
    protected lateinit var firebaseDatabase: DatabaseReference

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = Firebase.database.reference
    }

    fun getTextFromViewById(id: Int): String {
        val text = (findViewById(id) as TextView).text.toString()
        return text
    }

    fun setTextFromViewById(id: Int, text: String) {
        (findViewById(id) as TextView).text = text
    }

    fun goToNextActivity(activity: Class<*>?){
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun goToNextActivityWithoutHistory(activity: Class<*>?){
        val intent = Intent(this, activity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun goBackToHomeActivity(){
        val intent = Intent(this, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent);
    }

    fun signOut() {
        toast("signed out")
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun signOutAndGoToLogin() {
        toast("Please Login to add subscriptions")
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun getUniqueId(): String {
        return System.currentTimeMillis().toString()
    }
}