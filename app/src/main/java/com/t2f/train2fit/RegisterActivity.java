package com.t2f.train2fit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by NITHIN JOHN on 05-11-2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText u_name, u_email, u_upassword, u_cpassword,u_phone;
    private String name,email,upassword,cpassword,phone;
    Button regbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        u_name = (EditText) findViewById(R.id.name);
        u_email = (EditText) findViewById(R.id.emailid);
        u_upassword = (EditText) findViewById(R.id.upassword);
        u_cpassword = (EditText) findViewById(R.id.cpassword);
        u_phone = (EditText) findViewById(R.id.phone);
        regbutton = (Button) findViewById(R.id.regbutton);

        regbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                register(); // Register function call
            }
        });

    }
    public void register(){
        initialize();
        if(!validate()){
            Toast.makeText(this,"Sign Up Failed",Toast.LENGTH_SHORT).show();
        }
        else{
            onSignUp();
        }

    }

    public void onSignUp(){
        //TODO
    }

    public boolean validate(){
        boolean res = true;

        if(name.isEmpty()||name.length()>32){
            u_name.setError("Please enter valid name");
            res = false;
        }

        if(email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            u_email.setError("Please enter valid email address");
            res = false;
        }

        if(upassword.isEmpty()){
            u_upassword.setError("Input Password to proceed further");
            res=false;
        }

        if(cpassword.isEmpty()){
            u_cpassword.setError("Input Password to proceed further");
            res=false;
        }

        if(phone.isEmpty()){
            u_phone.setError("Please Enter Phone number");
            res=false;
        }

        return res;
    }

    public void initialize(){
        name = u_name.getText().toString().trim();
        email = u_email.getText().toString().trim();
        upassword = u_upassword.getText().toString().trim();
        cpassword = u_cpassword.getText().toString().trim();
        phone = u_phone.getText().toString().trim();
    }


}
