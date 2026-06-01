package tz.co.kiwelu.water.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.model.Models;
import tz.co.kiwelu.water.network.RetrofitClient;

public class BillsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipe;
    private LinearLayout llContent;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list_simple);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("Bills"); getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        swipe = findViewById(R.id.swipeRefresh);
        llContent = findViewById(R.id.llContent);
        swipe.setOnRefreshListener(this::load);
        load();
    }

    private void load() {
        swipe.setRefreshing(true);
        RetrofitClient.getApi().getBills(1, "").enqueue(new Callback<ApiResponse<Models.BillsData>>() {
            @Override public void onResponse(Call<ApiResponse<Models.BillsData>> c, Response<ApiResponse<Models.BillsData>> r) {
                swipe.setRefreshing(false);
                llContent.removeAllViews();
                if (r.isSuccessful() && r.body() != null && r.body().success) {
                    for (Models.Bill b : r.body().data.bills) {
                        TextView tv = new TextView(BillsActivity.this);
                        tv.setPadding(32,20,32,20);
                        tv.setText("📄 " + b.customerName + " (" + b.custNo + ")\n" +
                            "Month: " + b.billMonth + "  |  Amount: TZS " + String.format("%,.0f", b.amount) +
                            "\nStatus: " + b.status.toUpperCase() + "  |  Ref: " + b.billReference);
                        tv.setTextSize(13);
                        int bg = "paid".equals(b.status) ? Color.parseColor("#e8f5e9") :
                                 "overdue".equals(b.status) ? Color.parseColor("#ffebee") : Color.WHITE;
                        tv.setBackgroundColor(bg);
                        llContent.addView(tv);
                        View div = new View(BillsActivity.this);
                        div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        div.setBackgroundColor(Color.LTGRAY);
                        llContent.addView(div);
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponse<Models.BillsData>> c, Throwable t) { swipe.setRefreshing(false); }
        });
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
