package edu.fsu.cs.ven_u;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.fsu.cs.ven_u.ui.map.MapFragment;

public class NavigationActivity extends AppCompatActivity {

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        currentUsername = extras.getString("username");
        Toast.makeText(this,"Logged in as " + currentUsername + ".",
                Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_profile, R.id.navigation_timeline).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.logout:
                Toast.makeText(this, "Logged out of account " + currentUsername + ".",
                        Toast.LENGTH_SHORT).show();
                currentUsername = null;
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MapFragment.MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                || requestCode == MapFragment.MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                || requestCode == MapFragment.MY_PERMISSIONS_REQUEST_INTERNET) {
            MapFragment fragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && fragment != null && fragment.mapAPI != null) {
                // well, I tried ...
                fragment.onMapReady(fragment.mapAPI);
            }
        }
    }

}
