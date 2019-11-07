package com.example.database.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.database.R;
import com.example.database.models.StudentDetails;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnSubmit, btnDelete,btnLogout;

    EditText etStudentName, etPhoneNumber, etDeleteStudentName;

    // Declaring String variable ( In which we are storing firebase server URL ).
    public static final String Firebase_Server_URL = "https://insertdata-android-examples.firebaseio.com/";

    // Declaring String variables to store name & phone number get from EditText.
    String NameHolder, NumberHolder;

    Firebase firebase;

    DatabaseReference databaseReference;

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "Student_Database";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(MainActivity.this);
        firebase = new Firebase(Firebase_Server_URL);
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        etStudentName = (EditText) findViewById(R.id.et_student_name);
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        etDeleteStudentName = (EditText) findViewById(R.id.et_delete_student_name);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetDataFromEditText();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etDeleteStudentName.getText().toString().trim().equals(""))
                {
                    etDeleteStudentName.setError("student name cannot be empty, please fill");
                }else {

                    String studentName = etDeleteStudentName.getText().toString();
                    deleteData(studentName);
                }

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                Intent i = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void deleteData(String studentName) {

        Query deleteQuery = databaseReference.orderByChild("studentName").equalTo(studentName);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //  if(dataSnapshot.exists()) {
                    for (DataSnapshot delData : dataSnapshot.getChildren()) {
                        delData.getRef().removeValue();
                    }
                    etDeleteStudentName.setText("");
                    Toast.makeText(MainActivity.this, "Data Deleted Successfully from Firebase Database", Toast.LENGTH_LONG).show();
//                }else
//                {
//                    Toast.makeText(MainActivity.this,"Data is not there in Firebase Database",Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    public void GetDataFromEditText() {


        StudentDetails studentDetails = new StudentDetails();

        if(etStudentName.getText().toString().trim().equals(""))
        {
            etStudentName.setError("student name cannot be empty, please fill");
        }else if(etPhoneNumber.getText().toString().trim().equals(""))
        {
            etPhoneNumber.setError("phone number cannot be empty, please fill");
        }else {
            NameHolder = etStudentName.getText().toString().trim();
            NumberHolder = etPhoneNumber.getText().toString().trim();
            // Adding name into class function object.
            studentDetails.setStudentName(NameHolder);

            // Adding phone number into class function object.
            studentDetails.setStudentPhoneNumber(NumberHolder);

            // Getting the ID from firebase database.
            String StudentRecordIDFromServer = databaseReference.push().getKey();

            // Adding the both name and number values using student details class object using ID.
            databaseReference.child(StudentRecordIDFromServer).setValue(studentDetails);

            etStudentName.setText("");
            etPhoneNumber.setText("");

            // Showing Toast message after successfully data submit.
            Toast.makeText(MainActivity.this, "Data Inserted Successfully into Firebase Database", Toast.LENGTH_LONG).show();


        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(MainActivity.this, SwitchActivity.class);
        startActivity(i);
        finish();

    }
}