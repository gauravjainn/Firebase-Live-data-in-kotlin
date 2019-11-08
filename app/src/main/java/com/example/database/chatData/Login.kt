package com.example.database.chatData

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast


import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.database.R

import org.json.JSONException
import org.json.JSONObject

import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    internal lateinit var registerUser: TextView
    internal lateinit var username: EditText
    internal lateinit var password: EditText
    internal lateinit var loginButton: Button
    internal lateinit var user: String
    internal lateinit var pass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerUser = findViewById<View>(R.id.register) as TextView
        username = findViewById<View>(R.id.username) as EditText
        password = findViewById<View>(R.id.password) as EditText
        loginButton = findViewById<View>(R.id.loginButton) as Button

        registerUser.setOnClickListener { startActivity(Intent(this@Login, Register::class.java)) }

        loginButton.setOnClickListener {
            user = username.text.toString()
            pass = password.text.toString()

            if (user == "") {
                username.error = "can't be blank"
            } else if (pass == "") {
                password.error = "can't be blank"
            } else {
                val url = "https://androidchatapp-76776.firebaseio.com/users.json"
                val pd = ProgressDialog(this@Login)
                pd.setMessage("Loading...")
                pd.show()

                val request = StringRequest(Request.Method.GET, url, Response.Listener { s ->
                    if (s == "null") {
                        Toast.makeText(this@Login, "user not found", Toast.LENGTH_LONG).show()
                    } else {
                        try {
                            val obj = JSONObject(s)

                            if (!obj.has(user)) {
                                Toast.makeText(this@Login, "user not found", Toast.LENGTH_LONG).show()
                            } else if (obj.getJSONObject(user).getString("password") == pass) {
                                UserDetails.username = user
                                UserDetails.password = pass
                                startActivity(Intent(this@Login, Users::class.java))
                            } else {
                                Toast.makeText(this@Login, "incorrect password", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }

                    pd.dismiss()
                }, Response.ErrorListener { volleyError ->
                    println("" + volleyError)
                    pd.dismiss()
                })

                val rQueue = Volley.newRequestQueue(this@Login)
                rQueue.add(request)
            }
        }
    }
}
