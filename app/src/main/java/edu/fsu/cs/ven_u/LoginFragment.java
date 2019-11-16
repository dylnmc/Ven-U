package edu.fsu.cs.ven_u;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button loginButton;
    private TextView accountExistsFalseText;
    private EditText editUsername;
    private EditText editPassword;
    private String username;
    private String password;

    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        final View root = inflater.inflate(R.layout.fragment_login, container, false);

        editUsername = root.findViewById(R.id.edit_username);
        editPassword = root.findViewById(R.id.edit_password);

        accountExistsFalseText = root.findViewById(R.id.text_account_exists_false);
        accountExistsFalseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).loadFragment(new RegisterFragment());
            }
        });

        loginButton = root.findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
        return root;
    }

    private void onLogin() {
        username = editUsername.getText().toString();
        password = editPassword.getText().toString();

        if(!formIsFilled()) {
            Toast.makeText(getActivity(),"Please fill in all missing fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getActivity(),
                    "Password must be at least 6 characters. Try again.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        /*  if (database does not contain username) {
                Toast.makeText(getActivity(),"Username does not exist.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username does not match password in database) {
                Toast.makeText(getActivity(),"Username and password do not match.",
                    Toast.LENGTH_SHORT).show();
                return;
            }
         */

        Intent intent = new Intent(getActivity(), NavigationActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public boolean formIsFilled() {
        boolean filled = true;

        // make sure that all fields are filled in
        if (username.isEmpty() || password.isEmpty()) {
            filled = false;
        }

        editUsername.setHintTextColor(getResources().
                getColor(R.color.colorHintInvalid));
        editPassword.setHintTextColor(getResources().
                getColor(R.color.colorHintInvalid));

        return filled;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnLoginFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
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
    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnLoginFragmentInteraction(Uri uri);
    }
}
