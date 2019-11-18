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

public class TimelineRecyclerAdapter extends RecyclerView.Adapter<TimelineRecyclerAdapter.TimelineViewHolder> {
    private ArrayList<TimelineItem> mTimelineItems;
    private OnTimelineItemListener mTimelineListener;

    public static class TimelineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextViewName;
        public TextView mTextViewVisibility;

        public OnTimelineItemListener onTimelineItemListener;

        public TimelineViewHolder(@NonNull View itemView, OnTimelineItemListener onTimelineItemListener) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.timeline_event_name);
            mTextViewVisibility = itemView.findViewById(R.id.timeline_event_visibility);
            itemView.setOnClickListener(this);

            this.onTimelineItemListener = onTimelineItemListener;
        }

        @Override
        public void onClick(View view) {
            onTimelineItemListener.onItemClick(getAdapterPosition());
        }
    }

    public TimelineRecyclerAdapter(ArrayList<TimelineItem> timelineList, OnTimelineItemListener timelineListener) {
        mTimelineItems = timelineList;
        mTimelineListener = timelineListener;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);
        TimelineViewHolder tvh = new TimelineViewHolder(v, mTimelineListener);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        TimelineItem currentItem = mTimelineItems.get(position);
        holder.mTextViewName.setText(currentItem.getTitle());
        holder.mTextViewVisibility.setText(currentItem.getVisibility());
    }

    @Override
    public int getItemCount() {
        return mTimelineItems.size();
    }


    /*
    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragment);
        fragmentTransaction.commit(); // save the changes
    }
    */

    // https://www.youtube.com/watch?v=69C1ljfDvl0

    public interface OnTimelineItemListener {
        void onItemClick(int position);
    }
}
