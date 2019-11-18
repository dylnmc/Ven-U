package edu.fsu.cs.ven_u;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnRegisterFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button registerButton;
    private TextView accountExistsText;
    private EditText editTextArray[] = new EditText[5];
    private String name;
    private String username;
    private String password;
    private String confirmPassword;
    private String biography;

    private OnRegisterFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // if view events button is clicked, hide button and display list view
        final View root = inflater.inflate(R.layout.fragment_register, container, false);

        editTextArray[0] = root.findViewById(R.id.edit_name);
        editTextArray[1] = root.findViewById(R.id.edit_username);
        editTextArray[2] = root.findViewById(R.id.edit_password);
        editTextArray[3] = root.findViewById(R.id.edit_confirm_password);
        editTextArray[4] = root.findViewById(R.id.edit_biography);

        accountExistsText = root.findViewById(R.id.text_account_exists);
        accountExistsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).loadFragment(new LoginFragment());
            }
        });

        registerButton = root.findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister();
            }
        });
        return root;
    }

    private void onRegister() {
        name = editTextArray[0].getText().toString();
        username = editTextArray[1].getText().toString();
        password = editTextArray[2].getText().toString();
        confirmPassword = editTextArray[3].getText().toString();
        biography = editTextArray[4].getText().toString();

        if(!formIsFilled()) {
            Toast.makeText(getActivity(),"Please fill in all missing fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // xml file already restricts username unwanted characters

        final Database db = new Database();
        db.userDb.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //For all entries with given title
                        if(dataSnapshot.getChildrenCount() != 0) {
                            Toast.makeText(getActivity(), "Username taken. Try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if (password.length() < 6) {
                                Toast.makeText(getActivity(),
                                        "Password must be at least 6 characters. Try again.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!password.equals(confirmPassword)) {
                                Toast.makeText(getActivity(),"Passwords do not match. Try again.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Database.User user = new Database.User(name, username, biography, password);
                            db.addUser(user);
                            Intent intent = new Intent(getActivity(), NavigationActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                    }
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    public boolean formIsFilled() {
        boolean filled = true;
        // make sure that all fields are filled in
        for (int i = 0; i < editTextArray.length; i++) {
            if (editTextArray[i].getText().toString().isEmpty()) {
                filled = false;
            }
            // make hint color red to signify that fields must be filled out
            editTextArray[i].setHintTextColor(getResources().
                    getColor(R.color.colorHintInvalid));
        }
        return filled;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnRegisterFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRegisterFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnRegisterFragmentInteraction(Uri uri);
    }

}
