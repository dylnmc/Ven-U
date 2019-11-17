package edu.fsu.cs.ven_u.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import edu.fsu.cs.ven_u.NavigationActivity;
import edu.fsu.cs.ven_u.R;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    public Button viewEventsButton;
    public Button changePasswordButton;
    public TextView textName;
    public TextView textUsername;
    public TextView textBiography;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);

        changePasswordButton = root.findViewById(R.id.button_change_password);
        viewEventsButton = root.findViewById(R.id.button_view_events);
        textName = root.findViewById(R.id.text_name);
        textUsername = root.findViewById(R.id.text_username);
        textBiography = root.findViewById(R.id.text_biography);

        // textName.setText(get name from database);
        textUsername.setText( '@' + ((NavigationActivity)getActivity()).getCurrentUsername());
        // textBiography.setText(get biography from database);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        // if view events button is clicked, hide button and display list view
        viewEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (list of events is not empty){
                    eventsListView = root.findViewById(R.id.list_view_events);
                    eventsListView.setVisibility(View.VISIBLE);
                    viewEventsButton.setVisibility(View.INVISIBLE);
                } */
                // else {
                Toast.makeText(getActivity(),"This user has no events.",
                        Toast.LENGTH_SHORT).show();
                // }
            }
        });

        return root;
    }

}