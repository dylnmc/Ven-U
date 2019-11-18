package edu.fsu.cs.ven_u.ui.timeline;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.fsu.cs.ven_u.Database;
import edu.fsu.cs.ven_u.EventFragment;
import edu.fsu.cs.ven_u.R;
import edu.fsu.cs.ven_u.TimelineRecyclerAdapter;
import edu.fsu.cs.ven_u.TimelineItem;

public class TimelineFragment extends Fragment implements TimelineRecyclerAdapter.OnTimelineItemListener {
    private String TAG = "TIMELINE_FRAGMENT";

    private ArrayList<TimelineItem> timelineItems = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TimelineViewModel timelineViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        timelineViewModel = ViewModelProviders.of(this).get(TimelineViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_timeline, container, false);

        // final TextView textView = root.findViewById(R.id.text_timeline);
        timelineViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // textView.setText(s);
            }
        });

        //5 miles in meters (8046.72)
        final double RADIUS = 8046.72;

        //Temp current position
        final Location current_location = new Location("current");
        current_location.setLatitude(30.441365);
        current_location.setLongitude(-84.298080);

        final Database db = new Database();
        db.eventDb.orderByChild("title")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //For each child
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            Database.Event event = child.getValue(Database.Event.class);
                            //Create location object
                            Location location = new Location(event.getTitle());
                            location.setLatitude(event.getLatitude());
                            location.setLongitude(event.getLongitude());

                            //Check if within radius(currently 5 miles)
                            if(current_location.distanceTo(location) <= RADIUS){
                                timelineItems.add(new TimelineItem(
                                        event.getTitle(),
                                        event.getVisibility(),
                                        event.getCreator(),
                                        event.getDescription(),
                                        event.getStartTime(),
                                        event.getEndTime(),
                                        event.getLatitude(),
                                        event.getLongitude()
                                ));
                            }
                        }
                        mRecyclerView = root.findViewById(R.id.recyclerView);
                        mRecyclerView.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(getActivity());

                        //If no events found, insert message
                        if(timelineItems.size() == 0) {
                            timelineItems.add(new TimelineItem(
                                    "No events found", "", "", "", "", "", 0.0, 0.0));
                        }
                        mAdapter = new TimelineRecyclerAdapter(timelineItems, TimelineFragment.this);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
        return root;
    }

    // https://www.youtube.com/watch?v=69C1ljfDvl0

    @Override
    public void onItemClick(int position) {
        // https://stackoverflow.com/questions/18461990/pop-up-window-to-display-some-stuff-in-a-fragment
        View popupView = getLayoutInflater().inflate(R.layout.fragment_event, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        TimelineItem item = timelineItems.get(position);

        TextView title_view = popupView.findViewById(R.id.event_title);
        TextView desc_view = popupView.findViewById(R.id.event_desc);
        TextView creator_view = popupView.findViewById(R.id.event_creator);
        TextView visibility_view = popupView.findViewById(R.id.event_visibility);
        TextView start_view = popupView.findViewById(R.id.event_start);
        TextView end_view = popupView.findViewById(R.id.event_end);
        TextView location_view = popupView.findViewById(R.id.event_location);
        Button close_btn = popupView.findViewById(R.id.button_close_viewevent);

        title_view.setText(item.getTitle());
        desc_view.setText(item.getDescription());
        creator_view.setText(item.getCreator());
        visibility_view.setText(item.getVisibility());
        start_view.setText(item.getStart());
        end_view.setText(item.getEnd());
        Geocoder geo = new Geocoder(getContext());
        double lat = item.getLatitude();
        double lon = item.getLongitude();
        List<Address> addresses = null;
        try {
            addresses = geo.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String locStr;
        if (addresses != null) {
            locStr = "\n" + addresses.get(0).getAddressLine(0);
        } else {
            locStr = String.format("%.2f, %.2f", lat, lon);
        }
        location_view.setText(locStr);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        int location[] = new int[2];
        View anchorView = getView();
        anchorView.getLocationOnScreen(location);
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorView.getHeight());

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        /*
        // fragmentTransaction.replace(R.id.recyclerView, fragment);
        // fragmentTransaction.commit(); // save the changes

        final Dialog buildEvent = new Dialog(getContext());
        buildEvent.setContentView(R.layout.view_event);

        // create EventFragment
        EventFragment eventFragment = new EventFragment();
        TimelineItem item = timelineItems.get(position);
        Bundle extras = new Bundle();
        extras.putString("title", item.getTitle());
        extras.putString("visibility", item.getVisibility());
        extras.putString("creator", item.getCreator());
        extras.putString("description", item.getDescription());

        // add fragment_event to view_event layout's @id/event_container
        FragmentManager manager = getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.add(R.id.event_container, eventFragment, "event_fragment");

        buildEvent.setTitle("Event");

        Button viewClose = buildEvent.findViewById(R.id.button_close_viewevent);

        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildEvent.dismiss();
            }
        });

        buildEvent.setCancelable(false);
        buildEvent.show();
         */
    }
}