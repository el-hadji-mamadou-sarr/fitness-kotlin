package com.example.fitshare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.fitshare.R
import com.example.fitshare.models.Post
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PostAdapter(context: Context, private val posts: List<Post>) : ArrayAdapter<Post>(context, 0, posts) {

    override fun getCount(): Int {
        return posts.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.post_item, parent, false)


        val post = posts[position]

        val postAvatarView = view.findViewById<ImageView>(R.id.post_avatar)
        val postCreatorFullnameView = view.findViewById<TextView>(R.id.post_fullname)
        val postTextView = view.findViewById<TextView>(R.id.post_text)
        val createdAtTextView = view.findViewById<TextView>(R.id.post_date)
        val postImageView = view.findViewById<ImageView>(R.id.post_image)

        postTextView.text = post.postText
        val formattedDate = convertTimestampToDate(post.createdAt)
        createdAtTextView.text = formattedDate
        postCreatorFullnameView.text = post.creator

        return view
    }
    fun convertTimestampToDate(timestamp: Timestamp): String {
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val date = Date(timestamp.seconds)
        return dateFormat.format(date)
    }
}

