package edu.fsu.cs.ven_u;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class EventFragment extends Fragment {
    private final String TAG = "EventFragment.java";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_event, container, false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            final String title = bundle.getString("title");
            final String creator = bundle.getString("creator");
            Database db = new Database();
            db.eventDb.orderByChild("title").equalTo(title)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //For all entries with given title
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                try {
                                    Database.Event old_event = child.getValue(Database.Event.class);
                                    if (creator.equals(old_event.getCreator())) {
                                        TextView title_view = v.findViewById(R.id.event_title);
                                        TextView desc_view = v.findViewById(R.id.event_desc);
                                        TextView creator_view = v.findViewById(R.id.event_creator);
                                        TextView start_view = v.findViewById(R.id.event_start);
                                        TextView end_view = v.findViewById(R.id.event_end);
                                        TextView visibility_view = v.findViewById(R.id.event_visibility);

                                        title_view.setText(old_event.getTitle());
                                        desc_view.setText(old_event.getDescription());
                                        creator_view.setText(old_event.getCreator());

                                        String start_date = old_event.getStartTime().substring(0,
                                                old_event.getStartTime().indexOf(" "));
                                        String start_time = old_event.getStartTime().substring(
                                                old_event.getStartTime().indexOf(" ") + 1);
                                        String end_date = old_event.getEndTime().substring(0,
                                                old_event.getEndTime().indexOf(" "));
                                        String end_time = old_event.getEndTime().substring(
                                                old_event.getEndTime().indexOf(" ") + 1);

                                        String start = start_date + ", " + start_time;
                                        String end = end_date + ", " + end_time;
                                        start_view.setText(start);
                                        end_view.setText(end);

                                        visibility_view.setText(old_event.getVisibility());
                                    }
                                }
                                catch(Exception e){
                                    Log.e(TAG,"Exception in EventFragment listener: "
                                            + e.getMessage());
                                }
                            }
                        }
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
        }
        return v;
    }
}
