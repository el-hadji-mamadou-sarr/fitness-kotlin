package com.example.fitshare.models

import com.google.firebase.Timestamp

data class Post(
    val postText: String,
    var imageUrl: String?,
    val creator: String,
    val createdAt: Timestamp
){
    // Empty constructor required for Firestore deserialization
    constructor() : this( "", "", "",Timestamp.now())
    constructor(postText: String, creator: String?, createdAt: Timestamp) : this(postText, null, creator.toString(), createdAt)
}
