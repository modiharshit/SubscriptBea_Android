package com.example.gc.subscriptbea.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.gc.subscriptbea.R
import com.example.gc.subscriptbea.helpers.HMBaseActivity
import com.example.gc.subscriptbea.model.User
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

    fun btnSignupAction(view: View){
        this.createAccount()
    }

    //Firebase
    private fun createAccount() {
        val firstName: String = this.getTextFromViewById(R.id.firstName)
        val lastName: String = this.getTextFromViewById(R.id.lastName)
        val email: String = this.getTextFromViewById(R.id.email)
        val password: String = this.getTextFromViewById(R.id.password)
        val confirmPassword: String = this.getTextFromViewById(R.id.confirmPassword)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    var user = User(firebaseAuth.uid.toString() ,firstName, lastName, email)
                    firebaseDatabase.child("users").child(user.id).setValue(user)
                    this.updateUI(firebaseAuth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    toast("Authentication failed.")
                    this.updateUI(null)
                }
            }
    }

}