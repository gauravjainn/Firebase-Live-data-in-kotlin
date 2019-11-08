package com.example.database.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.example.database.R
import com.example.database.models.StudentDetails
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    internal var btnSubmit: Button
    internal var btnDelete: Button
    internal var btnLogout: Button

    internal var etStudentName: EditText
    internal var etPhoneNumber: EditText
    internal var etDeleteStudentName: EditText

    // Declaring String variables to store name & phone number get from EditText.
    internal var NameHolder: String
    internal var NumberHolder: String

    internal var firebase: Firebase

    internal var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Firebase.setAndroidContext(this@MainActivity)
        firebase = Firebase(Firebase_Server_URL)
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path)

        btnDelete = findViewById<View>(R.id.btn_delete) as Button
        btnLogout = findViewById<View>(R.id.btn_logout) as Button
        btnSubmit = findViewById<View>(R.id.btn_submit) as Button

        etStudentName = findViewById<View>(R.id.et_student_name) as EditText
        etPhoneNumber = findViewById<View>(R.id.et_phone_number) as EditText
        etDeleteStudentName = findViewById<View>(R.id.et_delete_student_name) as EditText


        btnSubmit.setOnClickListener { GetDataFromEditText() }

        btnDelete.setOnClickListener {
            if (etDeleteStudentName.text.toString().trim { it <= ' ' } == "") {
                etDeleteStudentName.error = "student name cannot be empty, please fill"
            } else {

                val studentName = etDeleteStudentName.text.toString()
                deleteData(studentName)
            }
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val i = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(i)
            finish()
        }


    }

    private fun deleteData(studentName: String) {

        val deleteQuery = databaseReference.orderByChild("studentName").equalTo(studentName)
        deleteQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //  if(dataSnapshot.exists()) {
                for (delData in dataSnapshot.children) {
                    delData.ref.removeValue()
                }
                etDeleteStudentName.setText("")
                Toast.makeText(this@MainActivity, "Data Deleted Successfully from Firebase Database", Toast.LENGTH_LONG).show()
                //                }else
                //                {
                //                    Toast.makeText(MainActivity.this,"Data is not there in Firebase Database",Toast.LENGTH_LONG).show();
                //                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })

    }

    fun GetDataFromEditText() {


        val studentDetails = StudentDetails()

        if (etStudentName.text.toString().trim { it <= ' ' } == "") {
            etStudentName.error = "student name cannot be empty, please fill"
        } else if (etPhoneNumber.text.toString().trim { it <= ' ' } == "") {
            etPhoneNumber.error = "phone number cannot be empty, please fill"
        } else {
            NameHolder = etStudentName.text.toString().trim { it <= ' ' }
            NumberHolder = etPhoneNumber.text.toString().trim { it <= ' ' }
            // Adding name into class function object.
            studentDetails.studentName = NameHolder

            // Adding phone number into class function object.
            studentDetails.studentPhoneNumber = NumberHolder

            // Getting the ID from firebase database.
            val StudentRecordIDFromServer = databaseReference.push().key

            // Adding the both name and number values using student details class object using ID.
            databaseReference.child(StudentRecordIDFromServer!!).setValue(studentDetails)

            etStudentName.setText("")
            etPhoneNumber.setText("")

            // Showing Toast message after successfully data submit.
            Toast.makeText(this@MainActivity, "Data Inserted Successfully into Firebase Database", Toast.LENGTH_LONG).show()


        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        val i = Intent(this@MainActivity, SwitchActivity::class.java)
        startActivity(i)
        finish()

    }

    companion object {

        // Declaring String variable ( In which we are storing firebase server URL ).
        val Firebase_Server_URL = "https://insertdata-android-examples.firebaseio.com/"

        // Root Database Name for Firebase Database.
        val Database_Path = "Student_Database"
    }
}