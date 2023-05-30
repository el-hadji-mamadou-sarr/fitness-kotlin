package com.example.fitshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.example.fitshare.adapters.PostAdapter
import com.example.fitshare.models.Post
import com.example.fitshare.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toobar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                // Navigate to the Login activity or any other activity you want the user to go to
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val listView:ListView = findViewById(R.id.listPost)
        val newPostButton: FloatingActionButton = findViewById(R.id.newPost)
        newPostButton.setOnClickListener {
            // Start the new activity here
            val intent = Intent(this, NewPostActivity::class.java)
            startActivity(intent)
        }

        val db = FirebaseFirestore.getInstance()
        val postsCollection = db.collection("Posts")
        val usersCollection = db.collection("Users")

        postsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val postList = mutableListOf<Post>()

                for (document in querySnapshot) {
                    val postText = document.getString("postText")
                    val imageUrl = document.getString("imageUrl")
                    val creator = document.getString("creator")
                    val createdAt = document.getTimestamp("createdAt")

                    if (postText != null && createdAt != null) {
                        usersCollection.document(creator.toString()).get()
                            .addOnSuccessListener { userDocument ->
                                if (userDocument.exists()) {
                                    val user = userDocument.toObject(User::class.java)
                                    if (user != null) {

                                        val post = Post(postText, imageUrl, user.fullname, createdAt)

                                        postList.add(post)
                                        println(postList.size)
                                        println(querySnapshot.size())

                                    }
                                }

                                // Check if all documents have been processed
                                if (postList.size == querySnapshot.size()) {
                                    println(postList)
                                    val postAdapter = PostAdapter(this, postList)
                                    listView.adapter = postAdapter
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error retrieving posts: ${exception.message}")
            }


    }
}