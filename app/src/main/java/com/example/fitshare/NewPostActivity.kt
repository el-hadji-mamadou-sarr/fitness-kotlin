package com.example.fitshare

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.example.fitshare.models.Post
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID


class NewPostActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }

        val postButton: Button = findViewById(R.id.postButton)
        val postEditText: EditText = findViewById(R.id.postText)

        postButton.setOnClickListener{
            val selectedImageView : ImageView = findViewById(R.id.selectedImageView)
            val postText = postEditText.text.toString()
            if(postText.isNotEmpty()){
                //save the post postText, selectedImageView if exist, creator , createdAt
                val firestore = FirebaseFirestore.getInstance()
                val createdAt = Timestamp.now()

                // Get the current user


                // Check if a user is currently signed in
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUser == null) {

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                val post = Post(postText, currentUser, createdAt)

                val selectedImage = selectedImageView.drawable?.toBitmap()
                if (selectedImage != null) {
                    val outputStream = ByteArrayOutputStream()
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val imageBytes = outputStream.toByteArray()

                    // Upload the image to Firebase Storage
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference
                    val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

                    val uploadTask = imageRef.putBytes(imageBytes)

                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            // Add the image URL to the post object
                            post.imageUrl = downloadUri.toString()

                            // Save the post to Firestore
                            firestore.collection("Posts")
                                .add(post)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error adding post: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Handle the error case if image upload fails
                            Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    // Save the post to Firestore without an image
                    firestore.collection("Posts")
                        .add(post)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error adding post: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }

            }else{
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val selectedImageView : ImageView = findViewById(R.id.selectedImageView)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data!!
            // Handle the selected image URI, e.g., display it in an ImageView
            selectedImageView.setImageURI(selectedImageUri)
        }
    }


}