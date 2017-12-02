package com.t2f.train2fit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputDOB, inputMobile, inputAddress;
    private Button SignIn, SignUp, Reset;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        SignIn = (Button) findViewById(R.id.sign_in_button);
        SignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputDOB = (EditText) findViewById(R.id.dob);
        inputMobile = (EditText)findViewById(R.id.mobile);
        inputAddress = (EditText)findViewById(R.id.address);
        Reset = (Button) findViewById(R.id.btn_reset_password);
        final EditText etFullName = (EditText) findViewById(R.id.fullName);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String dob = inputDOB.getText().toString().trim();
                String mobile = inputMobile.getText().toString().trim();
                String address = inputAddress.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(dob)) {
                    Toast.makeText(getApplicationContext(), "Enter DOB!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(getApplicationContext(), "Enter Mobile Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getApplicationContext(), "Enter Complete Address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                System.out.println("HELLO");
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.i("Testing", "User");
                                    System.out.println("line 1");
                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                                    Log.i("Testing", "User");
                                    System.out.println("line 2");
                                    String userId = task.getResult().getUser().getUid();
                                    String fullName = etFullName.getText().toString();
                                    String dob = inputDOB.getText().toString();
                                    String address = inputAddress.getText().toString();
                                    String mobile = inputMobile.getText().toString();
                                    String email = inputEmail.getText().toString();

                                    SharedPreferences sharedPref = getSharedPreferences("UserSignUp", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("username",inputEmail.getText().toString());
                                    editor.putString("password",inputPassword.getText().toString());
                                    editor.apply();

                                    Map<String, Object> userMap = new HashMap<String, Object>();
                                    userMap.put("full_name", fullName);
                                    userMap.put("dob", dob);
                                    userMap.put("address", address);
                                    userMap.put("mobile", mobile);
                                    userMap.put("email", email);

                                    mDatabase.child(userId).setValue(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void T) {
                                            Toast.makeText(getApplicationContext(), "Success: User Registration is Successful" , Toast.LENGTH_LONG ).show();
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error : User Registration is Unsuccessful" , Toast.LENGTH_LONG ).show();
                                        }
                                    });

                                }
                            }
                        });


            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
    }
}