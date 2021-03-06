/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t2f.train2fit;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookAppointmentDetailActivity extends AppCompatActivity {


    private static EditText DateEdit;
    private TextView detail_text;
    private EditText appointment_date;
    private EditText appointment_notes;
    private static DatabaseReference mDatabase;
    private static FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    public LocationManager lm;
    public LocationListener locationListener;
    public static Location location;
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_appointment_detail);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            return;
        } else {

            configureLocation();
        }
//        lm.requestLocationUpdates("gps", 5000, 0, locationListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    public void configureLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            return;
        }
        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1 : configureLocation();
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

//            Intent settingsIntent = new Intent(this, SettingsActivity.class);
//            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
        private TextView detail_text;
        private EditText appointment_date;
        private EditText appointment_notes;
        DatabaseReference mDatabase;
        private String bookingId;
        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            bookingId = "";

        // The detail Activity called via intent.  Inspect the intent for appointment data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String appointmentStr = "You Selected " + intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.detail_text)).setText(appointmentStr);
                }else  {
                    String appointmentStr = "You Selected " + intent.getStringExtra("trainerType");
                    ((TextView) rootView.findViewById(R.id.detail_text)).setText(appointmentStr);
                    ((TextView) rootView.findViewById(R.id.appointment_date)).setText(intent.getStringExtra("date"));
                    ((TextView) rootView.findViewById(R.id.appointment_notes)).setText(intent.getStringExtra("notes"));
                    bookingId = intent.getStringExtra("bookingId");
              }

            DateEdit = (EditText) rootView.findViewById(R.id.appointment_date);
            DateEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    DialogFragment timeFragment = new TimePickerFragment();
                    DialogFragment dateFragment = new DatePickerFragment();
                    if (DateEdit.getText().toString() != ""){
                        bundle.putString("dateAsText",DateEdit.getText().toString());
                        timeFragment.setArguments(bundle);
                        dateFragment.setArguments(bundle);
                    }
                    timeFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                    dateFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

                }
            });

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            if (bookingId.toString() == "") {
                DateEdit.setText(day + "-" + (month + 1) + "-" + year + " " + hour + ":" + minute);
            }


            mDatabase = FirebaseDatabase.getInstance().getReference().child("Bookings");
            final Button book_appointment_button = (Button) rootView.findViewById(R.id.book_appointment_button);
            book_appointment_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)  {
                    //get firebase auth instance
                    auth = FirebaseAuth.getInstance();

                    //get current user
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    String userId = user.getUid();
//                    String userId = user.getUid(); //retrieve from session
                    final String trainerType = ((TextView) rootView.findViewById(R.id.detail_text)).getText().toString().split("You Selected")[1];
                    final String date_time = ((EditText) rootView.findViewById(R.id.appointment_date)).getText().toString();
                    final String notes = ((EditText) rootView.findViewById(R.id.appointment_notes)).getText().toString();
                    final Map<String, String> booking = new HashMap();
                    final float distance = 0;
                    booking.put("trainerType", trainerType);
                    booking.put("dateTime", date_time);
                    booking.put("notes", notes);
                    booking.put("user", userId);
                    booking.put("status","Upcoming");

                    mDatabase.getParent().child("Trainers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String,Object> trainers = (Map<String,Object>) dataSnapshot.getValue();
                            double minDist = 999999999;
                            String trainerId = "";
                            for (Map.Entry<String, Object> trainer : trainers.entrySet()){
                                Map trainerData = (Map) trainer.getValue();
                                System.out.println(trainerData.get("trainerType").toString());
                                System.out.println(trainerType);
                                if(trainerType.trim().equals(trainerData.get("trainerType").toString().trim())){
                                    if(location != null){
                                        Location trainerLocation = new Location("trainer");
                                        float trainerLat = Float.parseFloat(trainerData.get("lat").toString());
                                        float trainerLng = Float.parseFloat(trainerData.get("lng").toString());
                                        trainerLocation.setLatitude(trainerLat);
                                        trainerLocation.setLongitude(trainerLng);

                                        double distance = location.distanceTo(trainerLocation);
                                        if(distance < minDist) {
                                            minDist = distance;
                                            trainerId = trainerData.get("trainerId").toString();
                                        }
                                    }else{
                                        Toast.makeText(getActivity(),"Switch on your location tracker" , Toast.LENGTH_LONG ).show();
                                        return;
                                    }
                                }
                            }
                            booking.put("trainerId", trainerId);
                            if (bookingId.toString() == "") {
                                mDatabase.push().setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void T) {
                                        Toast.makeText(getActivity().getBaseContext(), "Your Appointment booking is successful" , Toast.LENGTH_LONG ).show();
                                        Intent intent = new Intent(getActivity(), BookingActivity.class);
                                        getActivity().finish();
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity().getBaseContext(), "Error : Your Appointment booking is Unsuccessful" , Toast.LENGTH_LONG ).show();
                                    }
                                });
                            }else {
                                    try {
                                        mDatabase.child(bookingId).child("dateTime").setValue(date_time);
                                        mDatabase.child(bookingId).child("notes").setValue(notes);
                                        Toast.makeText(getActivity().getBaseContext(), "Your Appointment reschedule was successful", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getActivity(), BookingActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            return rootView;
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            Bundle bundle = getArguments();
            final Calendar calendar = getDateFromBundle(bundle);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            DateEdit.setText(day + "-" + (month + 1) + "-" + year);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            Bundle bundle = getArguments();
            final Calendar calendar = getDateFromBundle(bundle);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            DateEdit.setText(DateEdit.getText() + " " + hourOfDay + ":" + minute);
        }
    }

    public static Calendar getDateFromBundle(Bundle bundle){
        String dateString = bundle.getString("dateAsText");
        Date date = new Date();
        final Calendar calendar;
        if(dateString.toString() != "") {
            try {
                date = simpleDateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } else  {
            calendar = Calendar.getInstance();
        }
        return calendar;
    }
}