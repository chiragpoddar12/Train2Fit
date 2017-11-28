package com.t2f.train2fit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AppointmentDetailActivity extends AppCompatActivity {
    TextView nameTV, emailTV,phnTV, timeTV,locationTV, typeTV;
    FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private String trainerType,date,note, trainerId;
    Button cnl,reschedule;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        Intent intent=getIntent();
        CharSequence appointmentDetails= intent.getCharSequenceExtra("TYPE");
        getSupportActionBar().setTitle(intent.getStringExtra("TYPE"));
        nameTV=(TextView)findViewById(R.id.trainerNam);
        emailTV=(TextView)findViewById(R.id.emailTV);
        phnTV=(TextView)findViewById(R.id.phnTV);
        timeTV=(TextView)findViewById(R.id.timeTV);
        locationTV=(TextView)findViewById(R.id.location);
        cnl=(Button)findViewById(R.id.cnclBooking) ;
        reschedule=(Button)findViewById(R.id.rescheduleBooking) ;
        typeTV = (TextView) findViewById(R.id.trainerType);

        phnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+phnTV.getText()));
                    startActivity(intent);
                }catch (Exception e){
                    Log.e("TAG","Error in Phone call");
                }

            }
        });

        emailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] {(String) emailTV.getText()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                    startActivity(Intent.createChooser(intent, ""));
                }catch(ActivityNotFoundException e){
                    Log.e("TAG","Error in Email activity");
                }
            }
        });

        trainerType=intent.getStringExtra("trainerType");
        date=intent.getStringExtra("date");
        note=intent.getStringExtra("notes");
        trainerId = intent.getStringExtra("trainerId");



        auth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Trainers");

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                if(dataSnapshot.getKey().equals(trainerId))
                {
                    String fullName = (String) map.get("fullName");
                    String lat = (String) map.get("lat");
                    String lng = (String) map.get("lng");
                    String type = (String) map.get("type");
                    String phone = (String) map.get("phone");
                    String email = (String) map.get("email");

                    nameTV.setText(fullName);
                    typeTV.setText(type);
                    phnTV.setText(phone);
                    emailTV.setText(email);


                }



//                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.toString())) {
//                    if(trainerType.equalsIgnoreCase(trainer.toString())&&date.equalsIgnoreCase(dateTime.toString()))
//                    {
//                        key=dataSnapshot.getKey();
//
//                    }
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(key).removeValue();
                startActivity(new Intent(AppointmentDetailActivity.this,BookAppointmentActivity.class));
                UpcomingAppointmentsFragment.flag=false;
                finish();
            }
        });
        cnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(key).removeValue();
                finish();
            }
        });






    }
}
