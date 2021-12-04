package com.example.gc.subscriptbea.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gc.subscriptbea.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gc.subscriptbea.model.ItemsViewModel

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(ItemsViewModel(R.drawable.splash, "Item " + i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = HomeAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

    }
}