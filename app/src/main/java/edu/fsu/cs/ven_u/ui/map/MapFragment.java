package edu.fsu.cs.ven_u.ui.map;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.text.LoginFilter;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Permission;
import java.util.List;

import edu.fsu.cs.ven_u.Database;
import edu.fsu.cs.ven_u.NavigationActivity;
import edu.fsu.cs.ven_u.R;

import static androidx.core.content.ContextCompat.getSystemService;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    public static String TAG = "MapFragment";

    public static int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 30;
    public static int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 31;
    public static int MY_PERMISSIONS_REQUEST_INTERNET = 32;

    private LocationManager mLocationManager;

    MapViewModel mapViewModel;
    public GoogleMap mapAPI;
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

        // set up location manager
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        FragmentActivity activity = getActivity();

        // https://developer.android.com/training/permissions/requesting
        // request access for coarse and find location as well as internet if they are not already created
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapAPI);
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

                            final Database db = new Database();
                            Database.Event event = new Database.Event(eventName, eventDesc,
                                    ((NavigationActivity) getActivity()).getCurrentUsername(),
                                    eventPosition.latitude, eventPosition.longitude, eventType);
                            db.addEvent(event);
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

        return root;
    }

    private Location getLocation() {
        // https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null/20465781#20465781
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    continue;
                }
            }
            Location loc = mLocationManager.getLastKnownLocation(provider);
            if (loc == null) {
                continue;
            }
            if (bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = loc;
            }
        }
        return bestLocation;

        /*
        // https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/location/locationmanager.html
        final LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Retrieve a list of location providers that have course accuracy, no monetary cost, etc
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);
        String providerName = mLocationManager.getBestProvider(criteria, true);
        Location loc = null;
        String title = null;
        if (providerName != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "onMapReady: permission.ACCESS_COARSE_LOCATION not enabled ... requesting permission.");
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
                } else {
                    Log.i(TAG, "onMapReady: Using provider '" + providerName + "' to determine last known location.");
                    loc = mLocationManager.getLastKnownLocation(providerName);
                }
            }
        } else {
            Log.w(TAG, "onMapReady: Provider Name is null");
        }
        return loc;
         */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        mapAPI.getUiSettings().setMapToolbarEnabled(false);

        Location loc = getLocation();
        LatLng latlng;
        String title;
        if (loc == null) {
            Log.w(TAG, "onMapReady: Failed to set Latitude/Longitude; defaulting to 'FSU'@(30.441365,-84.298080)");
            latlng = new LatLng(30.441365, -84.298080);
            title = "FSU";
        } else {
            latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
            title = "Current Location";
        }

        mapAPI.addMarker(new MarkerOptions().position(latlng).title(title));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mapAPI.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Toast.makeText(getActivity(), "MAP PERMISSION REQUESTED!", Toast.LENGTH_SHORT).show();
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay!
            Toast.makeText(getActivity(), "Yay we can track you.", Toast.LENGTH_SHORT).show();
            if (mapAPI != null) {
                onMapReady(mapAPI);
            }
        } else {
            // permission denied, boo!
            Toast.makeText(getActivity(), "How are we supposed to track you?", Toast.LENGTH_SHORT).show();
        }
    }
    */

}