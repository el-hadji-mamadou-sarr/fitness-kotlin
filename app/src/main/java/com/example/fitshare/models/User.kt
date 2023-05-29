package com.example.fitshare.models

data class User(
    val fullname: String = "",
    val email: String = "",
    val age: Int = 0,
    val bio: String = "",

) {
    // Empty constructor required for Firestore deserialization
    constructor() : this("", "", 0, "")
}