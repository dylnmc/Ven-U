package edu.fsu.cs.ven_u.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import edu.fsu.cs.ven_u.R;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    public Button viewEventsButton;
    public ListView eventsListView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // if button is clicked, hide button and display list view
        viewEventsButton = (Button) root.findViewById(R.id.button_view_events);
        viewEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsListView = (ListView) root.findViewById(R.id.list_view_events);
                eventsListView.setVisibility(View.VISIBLE);
                viewEventsButton.setVisibility(View.INVISIBLE);
            }
        });
        return root;
    }
}