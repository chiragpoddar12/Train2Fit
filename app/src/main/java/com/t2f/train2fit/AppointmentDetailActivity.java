package com.t2f.train2fit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static android.R.attr.width;
import static com.t2f.train2fit.R.attr.height;
import static com.t2f.train2fit.R.id.imageView;

public class AppointmentDetailActivity extends AppCompatActivity {
    TextView nameTV, emailTV,phnTV, timeTV,notesTV, typeTV;
    private ImageView profilePicIV;
    FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private String trainerType,date,note, trainerId,bookingId,status;
    Button cnl,complete,reschedule;
    String key;
    private StorageReference mStorage;
    public String userId;

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
        notesTV=(TextView)findViewById(R.id.notes);
        cnl=(Button)findViewById(R.id.cnclBooking);
        complete=(Button)findViewById(R.id.completeBooking);
        reschedule=(Button)findViewById(R.id.rescheduleBooking) ;
        typeTV = (TextView) findViewById(R.id.trainerType);
        profilePicIV = (ImageView) findViewById(R.id.profilePicIV);
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
        bookingId = intent.getStringExtra("bookingId");
        status = intent.getStringExtra("status");
        if (status .equals("Completed")) {
            complete.setText("View Feedback");
            cnl.setVisibility(View.GONE);
            reschedule.setVisibility(View.GONE);
        }
        timeTV.setText(date.toString());
        notesTV.setText(note.toString());
        auth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Trainers");
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        Query phoneQuery = mDatabase.orderByChild("trainerId").equalTo(trainerId);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Map<String, Object> trainerInfos = (Map<String, Object>) singleSnapshot.getValue();
                    for (Map.Entry<String, Object> userInfo : trainerInfos.entrySet()) {
                        switch (userInfo.getKey()) {
                            case "fullName":
                                nameTV.setText(userInfo.getValue().toString());
                                break;
                            case "type":
                                typeTV.setText(userInfo.getValue().toString());
                                break;
                            case "phone":
                                phnTV.setText(userInfo.getValue().toString());
                                break;
                            case "email":
                                emailTV.setText(userInfo.getValue().toString());
                                break;
                            case "profilePhoto":
                                URL profilePhotoURL = null;
                                try {
                                    profilePhotoURL = new URL(userInfo.getValue().toString());
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                                Picasso.with(getApplicationContext())
                                        .load(String.valueOf(profilePhotoURL)).into(profilePicIV);
                                break;
                        }
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Trainer Details", "onCancelled", databaseError.toException());
            }
        });

        reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent detailsIntent = new Intent(AppointmentDetailActivity.this,BookAppointmentDetailActivity.class);
                detailsIntent.putExtra("trainerType",trainerType);
                detailsIntent.putExtra("date",date);
                detailsIntent.putExtra("notes",note);
                detailsIntent.putExtra("trainerId",trainerId);
                detailsIntent.putExtra("bookingId",bookingId);
//                flag=true;
                startActivity(detailsIntent);

//                startActivity(new Intent(AppointmentDetailActivity.this,BookAppointmentDetailActivity.class));
                UpcomingAppointmentsFragment.flag=false;
            }
        });
        cnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query cancelAppointmentQuery = ref.child("Bookings").child(bookingId.toString());

                cancelAppointmentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Cancel button pressed", "onCancelled", databaseError.toException());
                    }
                });

                finish();
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("Completed")) {
                    Intent feedbackActivityIntent = new Intent(AppointmentDetailActivity.this, feedbackActivity.class);
                    feedbackActivityIntent.putExtra("bookingId", bookingId);
                    feedbackActivityIntent.putExtra("stutus", status);
                    startActivity(feedbackActivityIntent);
                } else {
                    try {
                        FirebaseDatabase.getInstance().getReference().child("Bookings").child(bookingId).child("status").setValue("Completed");
                        Toast.makeText(getApplicationContext(), "Your Appointment was completed successful", Toast.LENGTH_LONG).show();
                        Intent feedbackActivityIntent = new Intent(AppointmentDetailActivity.this, feedbackActivity.class);
                        feedbackActivityIntent.putExtra("bookingId", bookingId);
                        feedbackActivityIntent.putExtra("stutus", status);
                        startActivity(feedbackActivityIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
