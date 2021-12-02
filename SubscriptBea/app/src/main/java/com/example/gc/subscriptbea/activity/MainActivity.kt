package com.example.gc.subscriptbea.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity

class MainActivity : HMBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun btnLoginAction(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun btnSignupAction(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}