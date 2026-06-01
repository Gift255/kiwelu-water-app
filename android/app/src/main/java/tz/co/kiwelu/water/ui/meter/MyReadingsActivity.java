package tz.co.kiwelu.water.ui.meter;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.model.Models;
import tz.co.kiwelu.water.network.RetrofitClient;

public class MyReadingsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipe;
    private LinearLayout llContent;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list_simple);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("My Readings"); getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        swipe = findViewById(R.id.swipeRefresh);
        llContent = findViewById(R.id.llContent);
        swipe.setOnRefreshListener(this::load);
        load();
    }

    private void load() {
        swipe.setRefreshing(true);
        RetrofitClient.getApi().getReadings(1, "").enqueue(new Callback<ApiResponse<Models.ReadingsData>>() {
            @Override public void onResponse(Call<ApiResponse<Models.ReadingsData>> c, Response<ApiResponse<Models.ReadingsData>> r) {
                swipe.setRefreshing(false);
                llContent.removeAllViews();
                if (r.isSuccessful() && r.body() != null && r.body().success) {
                    for (Models.Reading rd : r.body().data.readings) {
                        TextView tv = new TextView(MyReadingsActivity.this);
                        tv.setPadding(32, 20, 32, 20);
                        tv.setText("📊 " + rd.customerName + "\n" +
                            rd.previousReading + " → " + rd.currentReading + " m³  (" + rd.unitsConsumed + " units)\n" +
                            rd.readingDate + "  |  " + rd.status.toUpperCase());
                        int bg = "approved".equals(rd.status) ? Color.parseColor("#e8f5e9") :
                                 "rejected".equals(rd.status) ? Color.parseColor("#ffebee") : Color.WHITE;
                        tv.setBackgroundColor(bg);
                        tv.setTextSize(13);
                        llContent.addView(tv);
                        View div = new View(MyReadingsActivity.this);
                        div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        div.setBackgroundColor(Color.LTGRAY);
                        llContent.addView(div);
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponse<Models.ReadingsData>> c, Throwable t) { swipe.setRefreshing(false); }
        });
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
