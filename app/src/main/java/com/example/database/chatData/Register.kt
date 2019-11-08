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
import com.firebase.client.Firebase
import org.json.JSONException
import org.json.JSONObject

import androidx.appcompat.app.AppCompatActivity

class Register : AppCompatActivity() {
    internal lateinit var username: EditText
    internal lateinit var password: EditText
    internal lateinit var registerButton: Button
    internal lateinit var user: String
    internal lateinit var pass: String
    internal lateinit var login: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById<View>(R.id.username) as EditText
        password = findViewById<View>(R.id.password) as EditText
        registerButton = findViewById<View>(R.id.registerButton) as Button
        login = findViewById<View>(R.id.login) as TextView

        Firebase.setAndroidContext(this)

        login.setOnClickListener { startActivity(Intent(this@Register, Login::class.java)) }

        registerButton.setOnClickListener {
            user = username.text.toString()
            pass = password.text.toString()

            if (user == "") {
                username.error = "can't be blank"
            } else if (pass == "") {
                password.error = "can't be blank"
            } else if (!user.matches("[A-Za-z0-9]+".toRegex())) {
                username.error = "only alphabet or number allowed"
            } else if (user.length < 5) {
                username.error = "at least 5 characters long"
            } else if (pass.length < 5) {
                password.error = "at least 5 characters long"
            } else {
                val pd = ProgressDialog(this@Register)
                pd.setMessage("Loading...")
                pd.show()

                val url = "https://androidchatapp-76776.firebaseio.com/users.json"

                val request = StringRequest(Request.Method.GET, url, Response.Listener { s ->
                    val reference = Firebase("https://androidchatapp-76776.firebaseio.com/users")

                    if (s == "null") {
                        reference.child(user).child("password").setValue(pass)
                        Toast.makeText(this@Register, "registration successful", Toast.LENGTH_LONG).show()
                    } else {
                        try {
                            val obj = JSONObject(s)

                            if (!obj.has(user)) {
                                reference.child(user).child("password").setValue(pass)
                                Toast.makeText(this@Register, "registration successful", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@Register, "username already exists", Toast.LENGTH_LONG).show()
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

                val rQueue = Volley.newRequestQueue(this@Register)
                rQueue.add(request)
            }
        }
    }
}