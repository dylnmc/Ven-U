package edu.fsu.cs.ven_u.ui.map;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import edu.fsu.cs.ven_u.Database;
import edu.fsu.cs.ven_u.NavigationActivity;
import edu.fsu.cs.ven_u.R;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    MapViewModel mapViewModel; //Handle for MapViewModel
    GoogleMap mapAPI; //Handle on Google Maps
    Button createEvent; //Button to create a new event
    Button findEvent; //Button to find nearby events

    FusedLocationProviderClient mFusedLocationProviderClient; //Used to get last known location for a device
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234; //Random number for request code
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION; //Handle for the Fine Location permission
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION; //Handle for the Course Location permission
    boolean LocationPermissionGranted = false; //Used to determine if the user accepted location permissions

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Hooking up view model
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);
        //Creating the view for the user
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        //Getting the location permissions
        getLocationPermission();

        //If the permission was granted
        if (LocationPermissionGranted) {
            //We need explicit permission checks to verify permissions
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                mapAPI.setMyLocationEnabled(true);
            }
            getDeviceLocation();
        }

        createEvent = root.findViewById(R.id.addEvent);
        //This section of code creates the Event
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LatLng eventPostion = mapAPI.getCameraPosition().target;
                final double lattitude = eventPostion.latitude;
                final double longitude = eventPostion.longitude;
                final String user = ((NavigationActivity)getActivity()).getCurrentUsername();


                Intent intent = new Intent(getActivity(), CreateEvent.class);
                intent.putExtra("LatID", lattitude);
                intent.putExtra("LonID", longitude);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        //This needs to by synced with teh database to find an event
        findEvent = root.findViewById(R.id.findEvent);
        findEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap; //Setting GoogleMap
        mapAPI.getUiSettings().setMapToolbarEnabled(false);

        //Setting up location requests
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(2 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show the map

        //Marker example
//        LatLng FSU = new LatLng(30.441365, -84.298080);
//        mapAPI.addMarker(new MarkerOptions().position(FSU).title("FSU"));
//        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(FSU));
//        mapAPI.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    private void getDeviceLocation() {
        //Gets the device's current location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (LocationPermissionGranted) {
            Task location = mFusedLocationProviderClient.getLastLocation(); //New task for location
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        //Found Location
                        Location currentLocation = (Location) task.getResult();
                        LatLng currentLocLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(currentLocLatLng));
                        mapAPI.animateCamera(CameraUpdateFactory.zoomTo(17));
                    } else {
                        //Current location is null
                        Toast.makeText(getActivity(), "unable to get current location",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //This function asks the user for the Location
    private void getLocationPermission() {
        //Location permissions
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //If the user accepts the permissions
            if (ContextCompat.checkSelfPermission(getContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationPermissionGranted = true;
                initMap();
            } else {
                //Otherwise request permissions
                ActivityCompat.requestPermissions(getActivity(), permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            //Otherwise request permissions
            ActivityCompat.requestPermissions(getActivity(), permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //This function initializes the fragment with Google Maps API
    private void initMap() {
        Log.d(TAG, "initMap: initialization of map");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);
    }
}