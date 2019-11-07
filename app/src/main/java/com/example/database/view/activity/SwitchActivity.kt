package com.example.database.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View

import com.example.database.R
import com.example.database.chatData.Login

class SwitchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch)
    }


    fun gotoAdvance(view: View) {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    fun gotoChat(view: View) {
        startActivity(Intent(this, Login::class.java))
        finish()
    }

//    {
//        "rules": {
//        ".read": true,
//        ".write": true
//    }
//    }
}