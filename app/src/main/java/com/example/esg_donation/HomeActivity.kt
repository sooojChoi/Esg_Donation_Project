package com.example.esg_donation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.esg_donation.databinding.ActivityHomeBinding
import com.example.esg_donation.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity(){
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.button.setOnClickListener {
            auth.signOut()
        }

    }
}