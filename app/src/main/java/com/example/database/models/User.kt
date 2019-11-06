package com.example.database.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User {
    var username: String
    var email: String

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(username: String, email: String) {
        this.username = username
        this.email = email
    }
}