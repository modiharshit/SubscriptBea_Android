package com.example.gc.subscriptbea.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.util.Constants

class SplashScreenActivity : HMBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    public override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = firebaseAuth.currentUser
            if(currentUser != null){
                this.goToNextActivity(HomeActivity::class.java)
            }
            else{
                this.goToNextActivity(MainActivity::class.java)
            }
        }, Constants.SPLASH_TIME_OUT)
    }
}