package tz.co.kiwelu.water.ui.customer;

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

public class MyBillsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipe;
    private LinearLayout llContent;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list_simple);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("My Bills"); getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
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
                        TextView tv = new TextView(MyBillsActivity.this);
                        tv.setPadding(32, 20, 32, 20);
                        tv.setText("📄 " + b.billMonth + "\n" +
                            "Amount: TZS " + String.format("%,.0f", b.amount) +
                            "  |  Units: " + b.unitsConsumed + "\n" +
                            "Status: " + b.status.toUpperCase() + "  |  Due: " + (b.dueDate != null ? b.dueDate : "—"));
                        int bg = "paid".equals(b.status) ? Color.parseColor("#e8f5e9") :
                                 "overdue".equals(b.status) ? Color.parseColor("#ffebee") :
                                 Color.parseColor("#fff8e1");
                        tv.setBackgroundColor(bg); tv.setTextSize(13);
                        llContent.addView(tv);
                        View div = new View(MyBillsActivity.this);
                        div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        div.setBackgroundColor(Color.LTGRAY);
                        llContent.addView(div);
                    }
                    if (r.body().data.bills.isEmpty()) {
                        TextView tv = new TextView(MyBillsActivity.this);
                        tv.setText("No bills found."); tv.setPadding(32,32,32,32);
                        tv.setGravity(android.view.Gravity.CENTER); llContent.addView(tv);
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponse<Models.BillsData>> c, Throwable t) { swipe.setRefreshing(false); }
        });
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
