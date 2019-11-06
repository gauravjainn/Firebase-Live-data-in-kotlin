package com.example.database.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast

import com.example.database.R
import com.example.database.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : BaseActivity(), View.OnClickListener {
    private var mEmailField: EditText? = null
    private var mPasswordField: EditText? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mEmailField = findViewById(R.id.field_email)
        mPasswordField = findViewById(R.id.field_password)
        findViewById<View>(R.id.button_sign_in).setOnClickListener(this)
        findViewById<View>(R.id.button_sign_up).setOnClickListener(this)


        mAuth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        if (mAuth!!.currentUser != null) {
            onAuthSuccess(mAuth!!.currentUser!!)
        }
    }

    private fun onAuthSuccess(firebaseUser: FirebaseUser) {
        val email = firebaseUser.email
        var username = email

        if (email != null && email.contains("@")) {
            username = email.split("@".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]
        }
        val user = username?.let { email?.let { it1 -> User(it, it1) } }
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("users").child(firebaseUser.uid).setValue(user)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun signIn() {
        val email = mEmailField!!.text.toString().trim { it <= ' ' }
        val password = mPasswordField!!.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            showProgressDialog()
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    onAuthSuccess(task.result!!.user!!)
                } else {
                    Toast.makeText(this@SignInActivity, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun signUp() {
        val email = mEmailField!!.text.toString().trim { it <= ' ' }
        val password = mPasswordField!!.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            showProgressDialog()
            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    onAuthSuccess(task.result!!.user!!)
                } else {
                    Toast.makeText(this@SignInActivity, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            mEmailField!!.error = getString(R.string.required)
            return false
        } else if (TextUtils.isEmpty(password)) {
            mPasswordField!!.error = getString(R.string.required)
            return false
        } else {
            mEmailField!!.error = null
            mPasswordField!!.error = null
            return true
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_sign_in -> signIn()
            R.id.button_sign_up -> signUp()
        }
    }
}