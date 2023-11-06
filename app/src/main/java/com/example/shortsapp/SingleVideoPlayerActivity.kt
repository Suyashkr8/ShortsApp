package com.example.shortsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shortsapp.adapter.VideoListAdapter
import com.example.shortsapp.databinding.ActivitySingleVideoPlayerBinding
import com.example.shortsapp.model.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SingleVideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingleVideoPlayerBinding
    private lateinit var videoId : String

    private lateinit var adapter: VideoListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySingleVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoId = intent.getStringExtra("videoId")!!
        setupViewPager()
    }

    private fun setupViewPager() {
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos")
                    .whereEqualTo("videoId",videoId),
                VideoModel::class.java
            ).build()

        adapter = VideoListAdapter(options)
        binding.viewPager2.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }
}