package com.example.gc.subscriptbea.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.gc.subscriptbea.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.ItemsViewModel
import com.example.gc.subscriptbea.model.User
import com.google.gson.Gson
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
    fun getSubscriptions(){
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_SUBSCRIPTIONS).get()
            .addOnSuccessListener {
                Log.d(TAG, "Got Artworks ${(it.getValue())}")

                var artworkMap = it.getValue() as Map<String, Any>
                var subscriptions: ArrayList<ItemsViewModel> = ArrayList()
                var subscriptionsData: ItemsViewModel

                for ((k, v) in artworkMap) {
                    var subscriptionsValuesMap = v as Map<String, String>
                    subscriptionsData = ItemsViewModel(
                        subscriptionsValuesMap.get(SUBSCRIPTION_ID).toString(),
                        subscriptionsValuesMap.get(SUBSCRIPTION_TITLE).toString())
                    subscriptions.add(subscriptionsData)
                }

                //Passing data to custom adapter
                val adapter = HomeAdapter(subscriptions)
                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter

            }.addOnFailureListener{
                Log.e(TAG, "Error getting Artworks", it)
            }
    }


    fun btnAddSubscriptionAction(view: View) {
        this.goToNextActivity(AddSubscriptionActivity::class.java)
    }
}
