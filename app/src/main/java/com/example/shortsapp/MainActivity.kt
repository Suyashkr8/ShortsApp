package com.example.shortsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shortsapp.adapter.VideoListAdapter
import com.example.shortsapp.databinding.ActivityMainBinding
import com.example.shortsapp.model.VideoModel
import com.example.shortsapp.util.UiUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var adapter: VideoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavBar.setOnItemSelectedListener {menuItem-> //menuItem ki jagah it bhi rehene de sakte the
            when(menuItem.itemId)
            {
                R.id.bm_home -> {
                    UiUtil.showToast(this, "Home")

                }
                R.id.bm_add_video -> {
                    UiUtil.showToast(this, "Add video")
                    startActivity( Intent( this, VideoUploadActivity::class.java))

                }
                R.id.bm_profile -> {
                    UiUtil.showToast(this, "Profile")
                }
            }
            false
        }

        setupViewPager()


    }

    private fun setupViewPager() {
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos"),
                VideoModel::class.java
            ).build()

        adapter = VideoListAdapter(options)
        binding.viewPager.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}