package com.example.fitshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.fitshare.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    // Declare Firebase Authentication instance
    private lateinit var auth: FirebaseAuth
    private lateinit var registerButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var fullnameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton = findViewById(R.id.register)
        emailEditText = findViewById(R.id.email)
        fullnameEditText = findViewById(R.id.fullname)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.confirm_password)

        // Initialize Firebase Authentication instance
        auth = FirebaseAuth.getInstance()

        // Set click listener for the Register button
        registerButton.setOnClickListener {
            // Get user input from EditText fields
            val fullname = fullnameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirm_password = confirmPasswordEditText.text.toString().trim()

            // Validate user input


            if (email.isNotEmpty()
                && fullname.isNotEmpty()
                && password.isNotEmpty()
                && confirm_password.isNotEmpty()
                && confirm_password == password
            ) {

            // Create user with Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("task","success")
                            // User registration successful, update UI with user information
                            val user = auth.currentUser

                            // Create a User object to store additional user information
                            val newUser = User(fullname, email)

                            // Store user information in Firestore Users collection
                            FirebaseFirestore.getInstance().collection("Users")
                                .document(user?.uid!!)
                                .set(newUser)
                                .addOnSuccessListener {
                                    // User information stored successfully, redirect to HomeActivity
                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    // Failed to store user information in Firestore, delete user from Firebase Authentication
                                    user.delete()
                                        .addOnCompleteListener { deleteTask ->
                                            if (deleteTask.isSuccessful) {
                                                Toast.makeText(
                                                    baseContext,
                                                    "Failed to store user",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                        } else {
                            // User registration failed due to some error, display a message to the user
                            Toast.makeText(
                                baseContext,
                                "Registration Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }else{
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}