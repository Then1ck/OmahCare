package com.example.myapplication.main_func.caregiver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CaregiverDateActivity extends AppCompatActivity {

    private GridLayout gridDays;
    private TextView tvMonthYear;
    private TextView selectedDayView = null;

    // Views from included caregiver card
    private ImageView imageCaregiver;
    private TextView textName, textExperience, textDistance;

    private ImageView btnBack;

    // Data
    private String caregiverName, caregiverImg;
    private double caregiverExp, caregiverDist, caregiverCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_date);

        // Calendar UI
        gridDays = findViewById(R.id.gridDays);
        tvMonthYear = findViewById(R.id.tvMonthYear);

        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        // Caregiver card UI
        imageCaregiver = findViewById(R.id.imageCaregiver);
        textName = findViewById(R.id.textName);
        textExperience = findViewById(R.id.textExperience);
        textDistance = findViewById(R.id.textDistance);

        // Get caregiver data from Intent
        getIntentData();

        // Set data into card
        bindCaregiverData();

        // Setup the calendar view
        setupCalendar();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            caregiverName = getIntent().getStringExtra("caregiverName");
            caregiverImg = getIntent().getStringExtra("caregiverImg");
            caregiverExp = getIntent().getDoubleExtra("caregiverExp", 0);
            caregiverDist = getIntent().getDoubleExtra("caregiverDist", 0);
            caregiverCost = getIntent().getDoubleExtra("caregiverCost", 0);
        }
    }

    private void bindCaregiverData() {
        textName.setText(caregiverName != null ? caregiverName : "Unknown Caregiver");
        textExperience.setText(String.format(Locale.getDefault(), "%.0f tahun pengalaman", caregiverExp));
        textDistance.setText(String.format(Locale.getDefault(), "%.1f km dari lokasi anda", caregiverDist));

        // Load image (Glide is safe & efficient)
        if (caregiverImg != null && !caregiverImg.isEmpty()) {
            Glide.with(this)
                    .load(caregiverImg)
                    .placeholder(R.drawable.ic_caregiver)
                    .error(R.drawable.ic_caregiver)
                    .circleCrop()
                    .into(imageCaregiver);
        } else {
            imageCaregiver.setImageResource(R.drawable.ic_caregiver);
        }
    }

    private void setupCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(monthFormat.format(calendar.getTime()));

        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        gridDays.removeAllViews();

        for (int i = 0; i < firstDayOfWeek + daysInMonth; i++) {
            TextView tv = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, 24, 0, 24);
            tv.setTextSize(16f);

            if (i >= firstDayOfWeek) {
                final int day = i - firstDayOfWeek + 1;
                tv.setText(String.valueOf(day));

                if (day == today) {
                    tv.setTypeface(null, Typeface.BOLD);
                }

                tv.setOnClickListener(v -> selectDay(tv, day, month, year));
            }

            gridDays.addView(tv);
        }
    }

    private void selectDay(TextView tv, int day, int month, int year) {
        // Deselect previous
        if (selectedDayView != null) {
            selectedDayView.setBackgroundResource(android.R.color.transparent);
            selectedDayView.setTextColor(getResources().getColor(android.R.color.black));
            selectedDayView.setTypeface(null, Typeface.NORMAL);
        }

        // Select new
        tv.setBackgroundResource(R.drawable.bg_date_selected);
        tv.setTextColor(getResources().getColor(android.R.color.white));
        tv.setTypeface(null, Typeface.BOLD);
        selectedDayView = tv;

        String datePicked = String.format(Locale.getDefault(), "%02d %s %d", day,
                new SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().getTime()), year);

        Toast.makeText(this, "Picked: " + datePicked, Toast.LENGTH_SHORT).show();
    }
}
