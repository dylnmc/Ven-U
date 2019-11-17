package edu.fsu.cs.ven_u.ui.map;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

import edu.fsu.cs.ven_u.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private final static String TAG = "MAP_FRAGMENT";

    MapViewModel mapViewModel;
    GoogleMap mapAPI;
    SupportMapFragment mapFragment;
    Button createEvent;
    Button findEvent;

    String eventName;
    String eventType;
    String eventDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

        createEvent = root.findViewById(R.id.addEvent);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog buildEvent = new Dialog(getContext());
                buildEvent.setContentView(R.layout.create_event);
                buildEvent.setTitle("Create Event");

                final EditText evntName = buildEvent.findViewById(R.id.eventName);
                final EditText evntDesc = buildEvent.findViewById(R.id.eventDesc);
                final RadioGroup evntType = buildEvent.findViewById(R.id.eventType);

                Button eventAccept = buildEvent.findViewById(R.id.eventCreate);
                Button eventCancel = buildEvent.findViewById(R.id.eventCancel);

                eventAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (evntName.getText().toString().length() < 5) {
                            Toast.makeText(getActivity(), "Please create an Event name at least 5 characters long",
                                    Toast.LENGTH_SHORT).show();
                        } else if (evntDesc.getText().toString().length() < 20) {
                            Toast.makeText(getActivity(), "Please write a description at least 20 characters long",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            eventName = evntName.getText().toString();
                            eventDesc = evntDesc.getText().toString();
                            RadioButton radButton = buildEvent.findViewById(evntType.getCheckedRadioButtonId());
                            eventType = radButton.getText().toString();

                            LatLng eventPosition = mapAPI.getCameraPosition().target;
                            mapAPI.addMarker(new MarkerOptions().position(eventPosition).title(eventName));
                            mapAPI.moveCamera(CameraUpdateFactory.newLatLng(eventPosition));
                            mapAPI.animateCamera(CameraUpdateFactory.zoomTo(19));

                            buildEvent.dismiss();
                        }
                    }
                });

                eventCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buildEvent.dismiss();
                    }
                });

                buildEvent.setCancelable(false);
                buildEvent.show();
            }
        });

        findEvent = root.findViewById(R.id.findEvent);
        findEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Set Up find Event",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // setup google map autocomplete search
        // https://developers.google.com/places/android-sdk/autocomplete#add_a_placeselectionlistener_to_an_activity
        // https://stackoverflow.com/questions/54774648/im-using-the-new-places-sdk-in-android-the-autocompletesupportfragment-and-it
        // https://stackoverflow.com/questions/14654758/google-places-api-request-denied-for-android-autocomplete-even-with-the-right-a
        // https://developers.google.com/places/android-sdk/autocomplete#constrain_autocomplete_results
        // https://developers.google.com/places/android-sdk/reference/com/google/android/libraries/places/widget/AutocompleteSupportFragment
        // https://iteritory.com/android-google-places-autocomplete-feature-using-new-places-sdk/?fbclid=IwAR3-nV3ELTilG_W79xmC-UhI9Gkag58Odykg0fW8THisBH7aNb46PCmAnz4


        Resources res = getResources();

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.mapAutocomplete);

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), res.getString(R.string.map_key));
        }
        PlacesClient placesClient = Places.createClient(getActivity());

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                LatLng latlon = place.getLatLng();
                Log.i(TAG, "Place: " + place.getName() + " @ " + latlon.toString());
                mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon, 14));
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        mapAPI.getUiSettings().setMapToolbarEnabled(false);
        LatLng FSU = new LatLng(30.441365, -84.298080);
        mapAPI.addMarker(new MarkerOptions().position(FSU).title("FSU"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(FSU));
        mapAPI.animateCamera(CameraUpdateFactory.zoomTo(17));
    }
}