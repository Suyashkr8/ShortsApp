package com.example.shortsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.shortsapp.databinding.ActivityLoginBinding
import com.example.shortsapp.databinding.ActivitySignupBinding
import com.example.shortsapp.util.UiUtil
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAuth.getInstance().currentUser?.let {
            //user is there logged in
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }

        binding.btnSubmit.setOnClickListener {
            login()
        }

        binding.tvGoToSignup.setOnClickListener {
            startActivity( Intent( this, SignupActivity::class.java))
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

    private fun login() {

        val email = binding.etEmailInput.text.toString()
        val password = binding.etPasswordInput.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.etEmailInput.error = "Email not valid"
            return
        }

        if (password.length<6)
        {
            binding.etPasswordInput.error = "Minimum 6 characters should be there"
            return
        }

        loginWithFirebase(email, password)

    }

    private fun loginWithFirebase(email: String, password: String) {
        setInProgress(true)

        FirebaseAuth.getInstance().signInWithEmailAndPassword( email, password)
            .addOnSuccessListener {
                UiUtil.showToast(this,"Login Successfull")
                setInProgress(false)

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            .addOnFailureListener {
                UiUtil.showToast(applicationContext, it.localizedMessage?:"Something went wrong")
                setInProgress(false)

            }
    }
}