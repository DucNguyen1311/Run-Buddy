package com.example.group2_bigproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.Objects;

public class MapPageActivity extends FragmentActivity implements OnMapReadyCallback {
    private SharedPreferencesHelper spHelper;
    private ConstraintLayout mapSuggestedRoutesButton;
    private ConstraintLayout mapSavedRoutesButton;
    private TextView mapSuggestedRoutesButtonText;
    private TextView mapSavedRoutesButtonText;

    private TextView menuBarHomeButton;
    private TextView menuBarRoutesButton;
    private TextView menuBarMapButton;
    private TextView menuBarSocialButton;
    private TextView menuBarProfileButton;
    private ConstraintLayout mapStartTrackingButton;
    private ImageView mapWalkingButton;
    private ImageView mapCyclingButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private GoogleMap map;
    private LocationRequest locationRequest;
    private Location currentLocation;
    static int FINE_PERMISSION_CODE = 1;
    private String userID;

    private ArrayList<Route> routeList;
    private RecyclerView routeQuickViewRecyclerView;
    private RouteQuickViewAdapter routeQuickViewAdapter;
    public FirebaseHelper fbHelper;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);
        mapStartTrackingButton = findViewById(R.id.mapStartTrackingButton);
        menuBarHomeButton =findViewById(R.id.menuBarHomeButton);
        menuBarRoutesButton = findViewById(R.id.menuBarRoutesButton);
        menuBarMapButton = findViewById(R.id.menuBarMapButton);
        menuBarSocialButton = findViewById(R.id.menuBarSocialButton);
        menuBarProfileButton = findViewById(R.id.menuBarProfileButton);
        routeQuickViewRecyclerView = findViewById(R.id.routeQuickView);
//        mapWalkingButton = findViewById(R.id.mapWalkingButton);
//        mapCyclingButton = findViewById(R.id.mapCyclingButton);
        spHelper = new SharedPreferencesHelper(this);

        userID = spHelper.getSessionID();

        menuBarMapButton.setTextColor(R.color.light_grey);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(MapPageActivity.this);
        locationRequest = new LocationRequest.Builder(4000).setMinUpdateDistanceMeters(10).setMinUpdateIntervalMillis(2000).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(MapPageActivity.this);
                    }
                }
            }
        };
        getLastLocation();

        mapStartTrackingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapPageActivity.this, TrackingPageActivity.class);
            startActivity(intent);
        });

        menuBarHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapPageActivity.this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        menuBarRoutesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapPageActivity.this, RoutesPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        menuBarSocialButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapPageActivity.this, SocialPageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        menuBarProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapPageActivity.this, ProfilePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

//        mapCyclingButton.setOnClickListener(v -> {
//            mapCyclingButton.setImageResource(R.drawable.circle_shape);
//            mapWalkingButton.setImageResource(R.drawable.black_circle_shape);
//        });
//
//        mapWalkingButton.setOnClickListener(v -> {
//            mapCyclingButton.setImageResource(R.drawable.black_circle_shape);
//            mapWalkingButton.setImageResource(R.drawable.circle_shape);
//        });

        //Route Quick View setup here
        fbHelper = new FirebaseHelper(this);
        fbHelper.searchAllSavedRoute(userID, routes -> {
            routeQuickViewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            routeQuickViewAdapter = new RouteQuickViewAdapter(routes, this);
            routeQuickViewRecyclerView.setAdapter(routeQuickViewAdapter);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                Log.d("successful updated location", "location is ");
                currentLocation = location;
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("Starting location"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location is denied, please allow permission", Toast.LENGTH_LONG   ).show();
            }

        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (currentLocation != null){
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .title("current location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 20), 200, null);
        }
    }

}
