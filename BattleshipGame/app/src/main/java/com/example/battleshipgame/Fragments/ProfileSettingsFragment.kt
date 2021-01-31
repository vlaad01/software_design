package com.example.battleshipgame.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.battleshipgame.databinding.FragmentProfileSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import android.Manifest
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.math.BigInteger
import java.security.MessageDigest

class ProfileSettingsFragment : Fragment() {
    private lateinit var binding: FragmentProfileSettingsBinding
    private lateinit var mAuth: FirebaseAuth
    private var imageURI: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileSettingsBinding.inflate(inflater)
        mAuth = FirebaseAuth.getInstance()
        setUI()
        binding.btnSaveChng.setOnClickListener {
            saveChanges()
        }
        binding.btnUploadImage.setOnClickListener {
            uploadImage()
        }

        binding.btnUploadGravatar.setOnClickListener {
            uploadGravatar()
        }
        return binding.root
    }

    private fun setUI() {
        val user = mAuth.currentUser!!
        imageURI = user.photoUrl

        binding.editEmail.setText(user.email)
        binding.editName.setText(user.displayName)
        Picasso.get()
            .load(user.photoUrl.toString())
            .resize(120, 120)
            .centerCrop()
            .into(binding.imageView)
    }

    private fun saveChanges() {
        val user = mAuth.currentUser!!

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.editName.text.toString())
            .setPhotoUri(Uri.parse(imageURI.toString()))
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity!!.finish()
                } else {
                    Toast.makeText(
                        activity,
                        task.exception!!.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun uploadImage() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 222)
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                112
            )
        }
    }

    private fun uploadGravatar() {

        fun md5(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16)
                .padStart(32, '0')
        }

        val hash = md5(mAuth.currentUser!!.email!!)
        imageURI = Uri.parse("https://www.gravatar.com/avatar/${hash}?s=120")
        Picasso.get()
            .load(imageURI)
            .into(binding.imageView)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 121) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 222)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 222) {
                if (data != null) {
                    imageURI = data.data!!
                    binding.imageView.setImageURI(imageURI)
                    val imageExtension = MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(context!!.contentResolver.getType(imageURI!!))
                    val ref = FirebaseStorage.getInstance().reference
                        .child("img/img_${System.currentTimeMillis()}.${imageExtension}")
                    val uploadTask = ref.putFile(imageURI!!)
                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        ref.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            imageURI = task.result
                        }
                    }
                }
            }
        }
    }
}