package com.t2f.train2fit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingAppointmentsFragment extends Fragment {

    ListView listView;
    private DatabaseReference mDatabase;

    private ArrayAdapter<String> mBookAppointmentAdapter;
    final ArrayList<String> UpcomingAppointmentList = new ArrayList<String>();
    private ArrayList<String> mKeys = new ArrayList<>();

    public UpcomingAppointmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_upcoming_appointments, container, false);
        listView = (ListView) v.findViewById(R.id.lvupcomingAppointments);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Bookings");

        mBookAppointmentAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_appointments, // The name of the layout ID.
                        R.id.list_item_appointments_textview, // The ID of the textview to populate.
                        UpcomingAppointmentList);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Object trainer = map.get("trainerType");
                Object dateTime = map.get("dateTime");
                Object notes = map.get("notes");
                Object user = map.get("user");

                String trainerString = "Booked " + trainer.toString() + "\nDate: " + dateTime.toString() + "\nNotes: " + notes.toString();
                UpcomingAppointmentList.add(trainerString);
                mKeys.add(dataSnapshot.getKey());

                mBookAppointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Object trainer = map.get("trainerType");
                Object dateTime = map.get("dateTime");
                Object notes = map.get("notes");
                Object user = map.get("user");

                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);
                UpcomingAppointmentList.set(index, trainer.toString());
                mBookAppointmentAdapter.notifyDataSetChanged();

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

        listView.setAdapter(mBookAppointmentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CharSequence selectedAppointment = mBookAppointmentAdapter.getItem(position);
                Intent detailsIntent = new Intent(getContext(),AppointmentDetailActivity.class);
                detailsIntent.putExtra("TYPE",selectedAppointment);
                startActivity(detailsIntent);
            }
        });
        return v;

    }

}
