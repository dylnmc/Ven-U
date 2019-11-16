package edu.fsu.cs.ven_u;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// created with help of
// https://www.youtube.com/watch?v=17NbUcEts9c

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {
    private ArrayList<TimelineItem> mTimelineList;

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewName;
        public TextView mTextViewVisibility;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.timeline_event_name);
            mTextViewVisibility = itemView.findViewById(R.id.timeline_event_visibility);
        }
    }

    public TimelineAdapter(ArrayList<TimelineItem> timelineList) {
        mTimelineList = timelineList;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);
        TimelineViewHolder tvh = new TimelineViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        TimelineItem currentItem = mTimelineList.get(position);
        holder.mTextViewName.setText(currentItem.getTitle());
        holder.mTextViewVisibility.setText(currentItem.getVisibility());
    }

    @Override
    public int getItemCount() {
        return mTimelineList.size();
    }
}
