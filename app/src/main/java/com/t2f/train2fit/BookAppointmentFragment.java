package com.t2f.train2fit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class BookAppointmentFragment extends Fragment {

    private ArrayAdapter<String> mBookAppointmentAdapter;

    public BookAppointmentFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState){

        super.onCreate(saveInstanceState);
        //Allow the fragement to access menu items.
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_refresh) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragement_book_appointment, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_appointments);
        final ArrayList<String> list = new ArrayList<String>();
        String[] values = new String[] { "Holistic Personal Trainer", "Athletic Trainers", "Rehabilitation Specialist Trainer",
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                CharSequence selectedAppointment = mBookAppointmentAdapter.getItem(position);
                Intent newIntent = new Intent(getActivity(), BookAppointmentDetailActivity.class);
                newIntent.putExtra(Intent.EXTRA_TEXT, selectedAppointment);
                startActivity(newIntent);

            }
    });

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
