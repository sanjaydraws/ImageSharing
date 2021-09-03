package com.example.imagesharing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.example.imagesharing.activities.BaseActivity
import com.example.imagesharing.databinding.ActivityMainBinding
import com.example.imagesharing.uitls.applyProviderPermission
import com.example.imagesharing.uitls.createFile
import com.example.imagesharing.uitls.getUri

class MainActivity : BaseActivity() {
    var binding:ActivityMainBinding? = null
    var mainImageUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding?.apply {
            setContentView(binding?.root)
        }
        init()
    }

    override fun initArguments() {
    }

    override fun initAnalytics() {
    }

    override fun initViews() {
    }

    override fun setupListener() {
        binding?.imgShare?.setOnClickListener {
            if (mainImageUri!=null){
                shareImage(mainImageUri)
            }else{
                showToast("Please select Image")
            }
        }

        binding?.imagCamera?.setOnClickListener {
            imageCapture()
        }
        binding?.imgGallery?.setOnClickListener {
            openGallery()
        }
    }

    override fun setupData() {
    }

    fun imageCapture(){
            val imgFile = context?.createFile("${System.currentTimeMillis()}.jpg")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
             mainImageUri = context?.getUri(imgFile)
            context?.applyProviderPermission(intent, mainImageUri)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mainImageUri)
            startActivityForResult(intent, Constants.CAMERA)
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
//        intent.type = "image/* video/*"

        startActivityForResult(intent, Constants.GALLERY);

    }


    fun shareImage(imageUri: Uri?) {
        val intent = Intent()
        intent.apply {
            type = "image/*"
            action = Intent.ACTION_SEND
        }
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        applyProviderPermission(intent,imageUri)
        startActivity(intent)
    }

//    fun openGalleryForImages() {
//        if (Build.VERSION.SDK_INT < 23) {
//            var intent = Intent()
//            intent.type = "image/*"
//            intent.putExtra(Intent.ACTION_PICK, true)
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(
//                Intent.createChooser(intent, "Choose "), RequestCodes.GALLERY
//            )
//        } else {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            intent.type = "image/* video/*"
//            startActivityForResult(intent, RequestCodes.GALLERY);
//        }
//    }

    fun videoCapture() {
//        val videoFile = context?.createFile("Post_vid_{${System.currentTimeMillis()}}.mp4")
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//        videoUri = context?.getUri(videoFile)
//        context?.applyProviderPermission(intent, videoUri)
//        intent.putExtra()
        startActivityForResult(intent, Constants.VIDEO_CAPTURE)
    }

    fun chooseVideoFromGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Video"), Constants.GALLERY_VIDEO)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.GALLERY && data != null) {
            if (data.data != null) {
                val mMimeType=  data.data?.let { context.contentResolver?.getType(it) }
                when {
                    mMimeType?.contains("video/") == true -> {
//                        listener?.getVideoUri(data.data)
                    }
                    mMimeType?.contains("image/")  == true -> {
                        mainImageUri = data.data //content://com.miui.gallery.open/raw/%2Fstorage%2Femulated%2F0%2FDCIM%2FCamera%2FIMG_20210903_165104.jpg
                        binding?.imageView?.let {
                            Glide
                                .with(this@MainActivity)
                                .load(mainImageUri)
                                .centerCrop()
                                .into(it)
                        }
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.CAMERA) {
            binding?.imageView?.let {
                Glide
                    .with(this@MainActivity)
                    .load(mainImageUri)
                    .centerCrop()
                    .into(it)
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.VIDEO_CAPTURE && data != null) {
            val videoUri = data.data
//            listener?.getVideoUri(videoUri)
        }
    }
}