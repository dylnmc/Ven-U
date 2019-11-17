package edu.fsu.cs.ven_u.ui.timeline;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.fsu.cs.ven_u.Database;
import edu.fsu.cs.ven_u.R;
import edu.fsu.cs.ven_u.TimelineAdapter;
import edu.fsu.cs.ven_u.TimelineItem;

public class TimelineFragment extends Fragment{
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
                        ArrayList<TimelineItem> timelineList = new ArrayList<>();
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            Database.Event event = child.getValue(Database.Event.class);
                            //Create location object
                            Location location = new Location(event.getTitle());
                            location.setLatitude(event.getLatitude());
                            location.setLongitude(event.getLongitude());

                            //Check if within radius(currently 5 miles)
                            if(current_location.distanceTo(location) <= RADIUS){
                                timelineList.add(new TimelineItem(event.getTitle(),
                                        event.getVisibility()));
                            }
                        }
                        mRecyclerView = root.findViewById(R.id.recyclerView);
                        mRecyclerView.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(getContext());

                        //If no events found, insert message
                        if(timelineList.size() == 0) {
                            timelineList.add(new TimelineItem(
                                    "No events found", ""));
                        }
                        mAdapter = new TimelineAdapter(timelineList);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
        return root;
    }

}