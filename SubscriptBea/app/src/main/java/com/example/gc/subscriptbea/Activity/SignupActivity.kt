package com.example.gc.subscriptbea.activity

import android.os.Bundle
import android.util.Log
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.util.Extensions.toast
import com.google.firebase.auth.FirebaseUser

class SignupActivity : HMBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    private fun updateUI(user: FirebaseUser?) {
        this.goToNextActivity(HomeActivity::class.java)
    }

    //Firebase
    private fun createAccount(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignupActivity", "createUserWithEmail:success")
                    this.updateUI(firebaseAuth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignupActivity", "createUserWithEmail:failure", task.exception)
                    toast("Authentication failed.")
                    this.updateUI(null)
                }
            }
    }
}