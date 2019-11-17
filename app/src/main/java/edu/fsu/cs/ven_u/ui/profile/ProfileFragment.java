package edu.fsu.cs.ven_u.ui.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    public TextView textNameEdit;
    public TextView textUsername;
    public TextView textBiography;
    public TextView textBiographyEdit;

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
        textNameEdit = root.findViewById(R.id.text_name_edit);
        textUsername = root.findViewById(R.id.text_username);
        textBiography = root.findViewById(R.id.text_biography);
        textBiographyEdit = root.findViewById(R.id.text_biography_edit);

        // textName.setText(get name from database);
        textUsername.setText( '@' + ((NavigationActivity)getActivity()).getCurrentUsername());
        // textBiography.setText(get biography from database);

        textNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog buildEvent = new Dialog(getContext());
                buildEvent.setContentView(R.layout.change_name);
                buildEvent.setTitle("Change Password");

                final EditText changename = buildEvent.findViewById(R.id.edit_change_name);

                Button nameChange = buildEvent.findViewById(R.id.button_confirm_namechange);
                Button nameCancel = buildEvent.findViewById(R.id.button_cancel_namechange);

                nameChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = changename.getText().toString();
                        Log.i("PROFILE", "changing name to: " + name);
                        buildEvent.dismiss();
                    }
                });

                nameCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buildEvent.dismiss();
                    }
                });

                buildEvent.setCancelable(false);
                buildEvent.show();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog buildEvent = new Dialog(getContext());
                buildEvent.setContentView(R.layout.change_password);
                buildEvent.setTitle("Change Password");

                final EditText changepwd = buildEvent.findViewById(R.id.edit_change_password);

                Button pwdChange = buildEvent.findViewById(R.id.button_confirm_pwdchange);
                Button pwdCancel = buildEvent.findViewById(R.id.button_cancel_pwdchange);

                pwdChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = changepwd.getText().toString();
                        Log.i("PROFILE", "changing password to: " + password);
                        buildEvent.dismiss();
                    }
                });

                pwdCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buildEvent.dismiss();
                    }
                });

                buildEvent.setCancelable(false);
                buildEvent.show();
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