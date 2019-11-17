package edu.fsu.cs.ven_u.ui.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.ArrayList;

import edu.fsu.cs.ven_u.R;
import edu.fsu.cs.ven_u.TimelineAdapter;
import edu.fsu.cs.ven_u.TimelineItem;

public class TimelineFragment extends Fragment {
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
        View root = inflater.inflate(R.layout.fragment_timeline, container, false);

        // final TextView textView = root.findViewById(R.id.text_timeline);
        // timelineViewModel.getText().observe(this, new Observer<String>() {
        //     @Override
        //     public void onChanged(@Nullable String s) {
        //         // textView.setText(s);
        //     }
        // });

        final ArrayList<TimelineItem> timelineList = new ArrayList<>();
        timelineList.add(new TimelineItem("Gina's Party", "public"));
        timelineList.add(new TimelineItem("RubiCorp Symposium", "public"));
        timelineList.add(new TimelineItem("Bob's Party", "public"));

        mRecyclerView = root.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new TimelineAdapter(timelineList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }
}