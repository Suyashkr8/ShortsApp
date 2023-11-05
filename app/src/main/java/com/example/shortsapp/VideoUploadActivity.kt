package com.example.shortsapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.shortsapp.databinding.ActivityVideoUploadBinding
import com.example.shortsapp.model.VideoModel
import com.example.shortsapp.util.UiUtil
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class VideoUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoUploadBinding
    private var selectedVideoUri : Uri? = null
    private lateinit var videoLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {   result->
            if(result.resultCode == RESULT_OK)
            {
                selectedVideoUri = result.data?.data
                UiUtil.showToast(this,"Got video "+ selectedVideoUri.toString())
                showPostView()
            }
        }

        binding.rlUploadView.setOnClickListener {
            checkPermissionAndOpenVideoPicker()
        }

        binding.btnSubmitPost.setOnClickListener {
            postVideo()
        }

        binding.btnCancelPost.setOnClickListener {
            finish()
        }
    }

    private fun postVideo() {
        if(binding.etPostCaptionInput.text.toString().isEmpty())
        {
            binding.etPostCaptionInput.error = "Write something"
            return
        }

        setInProgress(true)
        selectedVideoUri?.apply {
            //store in firebase cloud storage

            val videoRef = FirebaseStorage
                                .getInstance()
                                .reference
                                .child("videos/"+ this.lastPathSegment)

            videoRef.putFile(this)
                .addOnSuccessListener {//when video upload task is done
                    videoRef.downloadUrl.addOnSuccessListener {
                        downloadUrl->
                        //we will get the url from above than save the modal in firebase firestore
                        postToFirestore(downloadUrl.toString())
                    }

            }
        }

    }

    private fun postToFirestore(url : String) {
        val videoModel = VideoModel(
            FirebaseAuth.getInstance().currentUser?.uid!! + "_" + Timestamp.now().toString(),
            binding.etPostCaptionInput.text.toString(),
            url,
            FirebaseAuth.getInstance().currentUser?.uid!!,
            Timestamp.now(),
        )

        Firebase.firestore.collection("videos")
            .document(videoModel.videoId)
            .set(videoModel)
            .addOnSuccessListener {

                setInProgress(false)
                UiUtil.showToast(applicationContext,"Video uploaded Successfully")
                finish()
            }
            .addOnFailureListener{
                setInProgress(true)
                UiUtil.showToast(applicationContext, "Video failed to upload")
            }

    }

    private fun setInProgress(inProgress : Boolean)
    {
        if(inProgress)
        {
            binding.pbProgressbar1.visibility = View.VISIBLE
            binding.btnSubmitPost.visibility = View.GONE
        }
        else
        {
            binding.pbProgressbar1.visibility = View.GONE
            binding.btnSubmitPost.visibility = View.VISIBLE
        }
    }

    private fun showPostView() {
        selectedVideoUri?.let {
            binding.rlPostView.visibility = View.VISIBLE
            binding.rlUploadView.visibility = View.GONE

            Glide.with(binding.ivPostThumbnail).load(it).into(binding.ivPostThumbnail)

        }
    }

    private fun checkPermissionAndOpenVideoPicker() {
        var readExternalVideo : String = ""

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readExternalVideo = android.Manifest.permission.READ_MEDIA_VIDEO
        else
            readExternalVideo = android.Manifest.permission.READ_EXTERNAL_STORAGE

        if(ContextCompat.checkSelfPermission(this, readExternalVideo) == PackageManager.PERMISSION_GRANTED)
        {
            //if we have the permissions
            openVideoPicker()
        }
        else
        {
            ActivityCompat.requestPermissions(this, arrayOf(readExternalVideo), 100)
        }
    }

    private fun openVideoPicker() {
        val intent = Intent( Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*" //to get all the videos
        videoLauncher.launch(intent)
    }
}