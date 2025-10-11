package com.example.myapplication.main_func.omah_ride;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TargetLocationActivity extends AppCompatActivity {

    private EditText etPickup, etDestination;
    private RecyclerView rvResults;
    private LocationAdapter adapter;

    private List<LocationItem> locationList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private double userLat = 0;
    private double userLon = 0;

    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private String mode = "destination"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omahride);

        etPickup = findViewById(R.id.etPickup);
        etDestination = findViewById(R.id.etDestination);
        rvResults = findViewById(R.id.rvResults);
        rvResults.setLayoutManager(new LinearLayoutManager(this));

        // Get data from intent
        mode = getIntent().getStringExtra("mode");
        String curLocText = getIntent().getStringExtra("curLocText");
        String targetLocText = getIntent().getStringExtra("targetLocText");
        etPickup.setText(curLocText);
        etDestination.setText(targetLocText);


        userLat = getIntent().getDoubleExtra("lat", 0);
        userLon = getIntent().getDoubleExtra("lon", 0);

        // Select which field to edit
        EditText activeField = mode.equals("current") ? etPickup : etDestination;
        activeField.setSelection(activeField.getText().length());


        adapter = new LocationAdapter(locationList, locItem -> {
            Intent intent = new Intent();
            intent.putExtra("selectedLocation", locItem.displayName);
            intent.putExtra("lat", locItem.lat);
            intent.putExtra("lon", locItem.lon);
            intent.putExtra("mode", mode); // tell OmahRideActivity which field to update
            setResult(RESULT_OK, intent);
            finish();
        });
        rvResults.setAdapter(adapter);

        // Attach search listener to active field
        activeField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> searchLocation(s.toString());
                handler.postDelayed(searchRunnable, 500);
            }
        });
    }

    private void searchLocation(String query) {
        if (query.isEmpty()) {
            locationList.clear();
            runOnUiThread(() -> adapter.notifyDataSetChanged());
            return;
        }

        new Thread(() -> {
            try {
                String url = "https://nominatim.openstreetmap.org/search?q="
                        + query.replace(" ", "+")
                        + "&format=json&addressdetails=0&limit=10";

                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "OmahRideApp/1.0")
                        .build();

                Response response = client.newCall(request).execute();
                String json = response.body().string();
                JSONArray results = new JSONArray(json);

                List<LocationItem> tempList = new ArrayList<>();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj = results.getJSONObject(i);
                    String displayName = obj.getString("display_name");
                    double lat = obj.getDouble("lat");
                    double lon = obj.getDouble("lon");
                    tempList.add(new LocationItem(displayName, lat, lon));
                }

                // Sort by distance
                Collections.sort(tempList, Comparator.comparingDouble(loc ->
                        distance(userLat, userLon, loc.lat, loc.lon)
                ));

                locationList.clear();
                locationList.addAll(tempList);

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Haversine distance
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    static class LocationItem {
        String displayName;
        double lat, lon;
        LocationItem(String displayName, double lat, double lon) {
            this.displayName = displayName;
            this.lat = lat;
            this.lon = lon;
        }
    }
}
