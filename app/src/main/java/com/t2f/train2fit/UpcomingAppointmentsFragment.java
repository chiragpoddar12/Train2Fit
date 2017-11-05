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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingAppointmentsFragment extends Fragment {

    ListView listView;

    final ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> mBookAppointmentAdapter;

    public UpcomingAppointmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_upcoming_appointments, container, false);
        listView = (ListView) v.findViewById(R.id.lvupcomingAppointments);

        final String[] values = new String[] { "Holistic Personal Trainer", "Athletic Trainers", "Rehabilitation Specialist Trainer",
                "Yoga Trainer", "Weight Loss Trainers", "Weight Maintenance Trainers", "Physical Recovery Trainers", "Physical Recovery Trainers",
                "Sports Trainers", "Dance Trainers"};
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        mBookAppointmentAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_appointments, // The name of the layout ID.
                        R.id.list_item_appointments_textview, // The ID of the textview to populate.
                        list);

        listView.setAdapter(mBookAppointmentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailsIntent = new Intent(getContext(),AppointmentDetailActivity.class);
                detailsIntent.putExtra("TYPE",values[i]);
                startActivity(detailsIntent);
            }
        });
        return v;

    }

}
