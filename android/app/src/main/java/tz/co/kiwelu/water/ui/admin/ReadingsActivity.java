package tz.co.kiwelu.water.ui.admin;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.model.Models;
import tz.co.kiwelu.water.network.RetrofitClient;

public class ReadingsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipe;
    private LinearLayout llReadings;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list_simple);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("Meter Readings"); getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        swipe = findViewById(R.id.swipeRefresh);
        llReadings = findViewById(R.id.llContent);
        swipe.setOnRefreshListener(this::load);
        load();
    }

    private void load() {
        swipe.setRefreshing(true);
        RetrofitClient.getApi().getReadings(1, "pending").enqueue(new Callback<ApiResponse<Models.ReadingsData>>() {
            @Override public void onResponse(Call<ApiResponse<Models.ReadingsData>> c, Response<ApiResponse<Models.ReadingsData>> r) {
                swipe.setRefreshing(false);
                llReadings.removeAllViews();
                if (r.isSuccessful() && r.body() != null && r.body().success) {
                    for (Models.Reading rd : r.body().data.readings) {
                        TextView tv = new TextView(ReadingsActivity.this);
                        tv.setPadding(32,24,32,24);
                        tv.setText("📊 " + rd.customerName + " (" + rd.custNo + ")\n" +
                            "Reading: " + rd.currentReading + " m³  |  Units: " + rd.unitsConsumed +
                            "\nDate: " + rd.readingDate + "  |  By: " + rd.readerName);
                        tv.setTextSize(13);
                        tv.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
                        llReadings.addView(tv);
                    }
                    if (r.body().data.readings.isEmpty()) {
                        TextView tv = new TextView(ReadingsActivity.this);
                        tv.setText("No pending readings."); tv.setPadding(32,32,32,32);
                        tv.setGravity(android.view.Gravity.CENTER); llReadings.addView(tv);
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponse<Models.ReadingsData>> c, Throwable t) {
                swipe.setRefreshing(false);
            }
        });
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
