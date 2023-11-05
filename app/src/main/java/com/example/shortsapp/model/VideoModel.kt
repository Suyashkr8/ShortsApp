package com.example.shortsapp.model

import com.google.firebase.Timestamp

data class VideoModel(

    var videoId : String = "",
    var title : String = "",
    var url : String = "",
    var uploaderId : String = "",
    var createdTime : Timestamp = Timestamp.now()// here Timestamp is of Firebase
)