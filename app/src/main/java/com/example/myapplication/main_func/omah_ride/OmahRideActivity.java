package com.example.myapplication.main_func.omah_ride;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OmahRideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private MapView mapView;
    private boolean autoUpdateCurLoc = false;

    private Button btnTambah;
    private GoogleMap googleMap;
    private TextView curLoc, targetLoc;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker userMarker;
    private ImageView driverImage;
    private TextView driverNameCar, vehicleNumber;
    private String riderNameGlobal;
    private String riderIdGlobal;

    private LatLng curLatLon, targetLatLon;

    // GraphHopper API key (replace with your own)
    private static final String GRAPHHOPPER_API_KEY = "ffa18d6c-29fd-461c-b0ea-117b54ceb61a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Auto-update curLoc only if coming from HomeActivity
        if (getIntent().hasExtra("fromHome") && getIntent().getBooleanExtra("fromHome", false)) {
            autoUpdateCurLoc = true;
        }

        mapView = findViewById(R.id.Map);
        btnTambah = findViewById(R.id.btnTambah);
        curLoc = findViewById(R.id.curLoc);
        targetLoc = findViewById(R.id.targetLoc);
        driverImage = findViewById(R.id.driver_image);
        driverNameCar = findViewById(R.id.driver_name_car);
        vehicleNumber = findViewById(R.id.vehicle_number);

        loadRandomRider();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationCallback();

        curLoc.setOnClickListener(v -> openLocationActivity("current", curLoc.getText().toString(), curLatLon));
        targetLoc.setOnClickListener(v -> openLocationActivity("target", targetLoc.getText().toString(), targetLatLon));

        btnTambah.setOnClickListener(v ->
                Toast.makeText(this, "Tambah lokasi ditekan!", Toast.LENGTH_SHORT).show()
        );

        checkPermissionsAndStartLocation();

        LinearLayout llPesanOmahRide = findViewById(R.id.llPesanOmahRide);
        LinearLayout bottomMain = findViewById(R.id.bottomMain);
        ConstraintLayout bottomConfirmation = findViewById(R.id.bottomConfirmation);

        llPesanOmahRide.setOnClickListener(v -> {
            bottomMain.setVisibility(View.GONE);
            bottomConfirmation.setVisibility(View.VISIBLE);
        });


        CardView locationButton = findViewById(R.id.location_button);
        locationButton.setOnClickListener(v -> {
            if (riderNameGlobal != null && riderIdGlobal != null) {
                Intent intent = new Intent(OmahRideActivity.this, com.example.myapplication.system.ChatRiderActivity.class);
                intent.putExtra("chat_with_name", riderNameGlobal);
                intent.putExtra("chat_with_id", riderIdGlobal);
                startActivity(intent);
            } else {
                Toast.makeText(OmahRideActivity.this, "Rider info not available yet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadRandomRider() {
        ConstraintLayout driverBar = findViewById(R.id.bottomConfirmation); // your driver panel
        ImageView driverImage = findViewById(R.id.driver_image);
        TextView driverNameCar = findViewById(R.id.driver_name_car);
        TextView vehicleNumber = findViewById(R.id.vehicle_number);

        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("rider");

        riderRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Failed to fetch riders", Toast.LENGTH_SHORT).show();
                return;
            }

            DataSnapshot snapshot = task.getResult();
            if (snapshot == null || !snapshot.exists()) {
                Toast.makeText(this, "No riders available", Toast.LENGTH_SHORT).show();
                return;
            }

            List<DataSnapshot> riderList = new ArrayList<>();
            for (DataSnapshot riderSnap : snapshot.getChildren()) {
                riderList.add(riderSnap);
            }

            if (riderList.isEmpty()) return;

            // Pick a random rider
            int randomIndex = new Random().nextInt(riderList.size());
            DataSnapshot selectedRiderSnap = riderList.get(randomIndex);

            String name = selectedRiderSnap.child("name").getValue(String.class);
            String car = selectedRiderSnap.child("car").getValue(String.class);
            String plate = selectedRiderSnap.child("plate").getValue(String.class);
            String imgUrl = selectedRiderSnap.child("img").getValue(String.class);
            riderNameGlobal = name;
            riderIdGlobal = selectedRiderSnap.getKey();


            // Set texts
            driverNameCar.setText(name + " • " + car);
            vehicleNumber.setText(plate);

            // Load image with Glide
            Glide.with(this)
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_caregiver)
                    .error(R.drawable.ic_caregiver)
                    .circleCrop()
                    .into(driverImage);

            // Show driver panel
//            driverBar.setVisibility(View.VISIBLE);
        });
    }




    private void openLocationActivity(String mode, String currentText, LatLng latLon) {
        Intent intent = new Intent(this, TargetLocationActivity.class);
        intent.putExtra("mode", mode);
        intent.putExtra("currentText", currentText);
        intent.putExtra("curLocText", curLoc.getText().toString());
        intent.putExtra("targetLocText", targetLoc.getText().toString());
        intent.putExtra("lat", latLon != null ? latLon.latitude : 0);
        intent.putExtra("lon", latLon != null ? latLon.longitude : 0);
        startActivityForResult(intent, 1001);
    }

    private void updateMapLocation(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userMarker == null) {
            userMarker = googleMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title("Your Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f));
        } else {
            userMarker.setPosition(userLatLng);
        }

        // Reverse geocode
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String addressLine = addresses.get(0).getAddressLine(0);
                if (autoUpdateCurLoc) curLoc.setText(addressLine);
            } else {
                curLoc.setText("Location unknown");
            }
        } catch (IOException e) {
            e.printStackTrace();
            curLoc.setText("Unable to get address");
        }
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || googleMap == null) return;
                Location location = locationResult.getLastLocation();
                if (location != null) updateMapLocation(location);
            }
        };
    }

    private void checkPermissionsAndStartLocation() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
        } else startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) granted = false;
            }
            if (!granted)
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            else startLocationUpdates();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String selectedLocation = data.getStringExtra("selectedLocation");
            double lat = data.getDoubleExtra("lat", 0);
            double lon = data.getDoubleExtra("lon", 0);
            String mode = data.getStringExtra("mode");

            if ("target".equals(mode)) {
                targetLoc.setText(selectedLocation);
                targetLatLon = new LatLng(lat, lon);
            } else {
                curLoc.setText(selectedLocation);
                curLatLon = new LatLng(lat, lon);
            }

            // Draw route
            if (curLatLon != null && targetLatLon != null) {
                fetchGraphHopperRoute(curLatLon, targetLatLon);
            }
        }
    }

    // === GraphHopper routing ===
    private void fetchGraphHopperRoute(LatLng start, LatLng end) {
        String url = "https://graphhopper.com/api/1/route" +
                "?point=" + start.latitude + "," + start.longitude +
                "&point=" + end.latitude + "," + end.longitude +
                "&vehicle=car&locale=en&points_encoded=false&key=" + GRAPHHOPPER_API_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(OmahRideActivity.this, "Failed to fetch route", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) return;
                String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    JSONArray paths = json.getJSONArray("paths");
                    JSONArray points = paths.getJSONObject(0).getJSONObject("points").getJSONArray("coordinates");

                    List<LatLng> routePoints = new ArrayList<>();
                    for (int i = 0; i < points.length(); i++) {
                        JSONArray point = points.getJSONArray(i);
                        double lon = point.getDouble(0);
                        double lat = point.getDouble(1);
                        routePoints.add(new LatLng(lat, lon));
                    }

                    runOnUiThread(() -> drawGraphHopperRoute(routePoints));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void drawGraphHopperRoute(List<LatLng> routePoints) {
        if (googleMap == null || routePoints.isEmpty()) return;

        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(curLatLon).title("Current Location"));
        googleMap.addMarker(new MarkerOptions().position(targetLatLon).title("Target Location"));

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints)
                .width(8)
                .color(Color.BLUE);

        googleMap.addPolyline(polylineOptions);

        // Adjust camera
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                new com.google.android.gms.maps.model.LatLngBounds.Builder()
                        .include(curLatLon)
                        .include(targetLatLon)
                        .build(), 100
        ));
    }
}
