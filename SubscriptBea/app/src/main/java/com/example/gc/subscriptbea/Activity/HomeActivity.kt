package com.example.gc.subscriptbea.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.Subscription
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : HMBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set Toolbar
        setSupportActionBar(toolbar)


        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        this.getSubscriptions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemView = item.itemId
        when(itemView){

            R.id.profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
        return false
    }

    //Firebase
    fun getSubscriptions() {
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).get()
            .addOnSuccessListener {
                var subscriptions: ArrayList<Subscription> = ArrayList()
                if(it.value != null){
                    Log.d(TAG, "Got Subscriptions ${(it.value)}")
                    var subscriptionMap = it.getValue() as Map<String, Any>
                    var subscription: Subscription

                    for ((k, v) in subscriptionMap) {
                        var subscriptionValuesMap = v as Map<String, String>
                        subscription = Subscription(
                            subscriptionValuesMap.get(SUBSCRIPTION_ID).toString(),
                            subscriptionValuesMap.get(SUBSCRIPTION_TITLE).toString(),
                            subscriptionValuesMap.get(SUBSCRIPTION_AMOUNT).toString(),
                            subscriptionValuesMap.get(SUBSCRIPTION_TYPE).toString(),
                            subscriptionValuesMap.get(SUBSCRIPTION_START_DATE).toString())
                        subscriptions.add(subscription)
                    }
                }
                //Passing data to custom adapter
                val adapter = HomeAdapter(subscriptions)
                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter

                startIntroAnimation();

            }.addOnFailureListener{
                Log.e(TAG, "Error getting Subscriptions", it)
            }
    }

    //ANIMATION CODE FOR RECYCLER VIEW
    private fun startIntroAnimation() {
        recyclerview.alpha = 0f
        recyclerview.animate()
            .translationY(0f)
            .setDuration(2500)
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    fun btnAddSubscriptionAction(view: View) {
        this.goToNextActivity(AddSubscriptionActivity::class.java)
    }
}
