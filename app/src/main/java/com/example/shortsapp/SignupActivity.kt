package com.example.shortsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.shortsapp.databinding.ActivitySignupBinding
import com.example.shortsapp.model.UserModel
import com.example.shortsapp.util.UiUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            signup()
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity( Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setInProgress(inProgress : Boolean)
    {
        if(inProgress)
        {
            binding.pbProgressbar.visibility = View.VISIBLE
            binding.btnSubmit.visibility = View.GONE
        }
        else
        {
            binding.pbProgressbar.visibility = View.GONE
            binding.btnSubmit.visibility = View.VISIBLE
        }
    }

    private fun signup() {
        val email = binding.etEmailInput.text.toString()
        val password = binding.etPasswordInput.text.toString()
        val confirmPassword = binding.etConfirmPasswordInput.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.etEmailInput.error = "Email not valid"
            return
        }

        if (password.length<6)
        {
            binding.etPasswordInput.setError("Minimum 6 characters should be there")
            return
        }

        if (password != confirmPassword)
        {
            binding.etPasswordInput.setError("password not matched")
            return
        }

        signUpWithFirebase(email, password)


    }

    private fun signUpWithFirebase(email : String, password : String)
    {
        setInProgress(true)

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let {
                    user ->
                    val userModel = UserModel(user.uid, email, email.substringBefore("@") )

                    Firebase.firestore.collection("users")
                        .document(user.uid)
                        .set(userModel)
                        .addOnSuccessListener {
                            UiUtil.showToast(applicationContext, "Account added successfully")
                            //setInProgress(false)
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                }
            }
            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?:"Something went wrong")
                setInProgress(false)

            }
    }
}
