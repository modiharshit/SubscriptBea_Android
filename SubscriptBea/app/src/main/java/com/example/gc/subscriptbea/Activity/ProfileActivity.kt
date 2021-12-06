package com.example.gc.subscriptbea.activity

import android.os.Bundle
import android.view.View
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity

class ProfileActivity : HMBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun btnSignupAction(view: View) {
        this.signOut()
    }
}