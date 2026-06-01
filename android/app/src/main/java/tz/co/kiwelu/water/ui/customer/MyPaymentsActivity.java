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

public class MyPaymentsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipe;
    private LinearLayout llContent;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list_simple);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("My Payments"); getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        swipe = findViewById(R.id.swipeRefresh);
        llContent = findViewById(R.id.llContent);
        swipe.setOnRefreshListener(this::load);
        load();
    }

    private void load() {
        swipe.setRefreshing(true);
        RetrofitClient.getApi().getPayments(1).enqueue(new Callback<ApiResponse<Models.PaymentsData>>() {
            @Override public void onResponse(Call<ApiResponse<Models.PaymentsData>> c, Response<ApiResponse<Models.PaymentsData>> r) {
                swipe.setRefreshing(false);
                llContent.removeAllViews();
                if (r.isSuccessful() && r.body() != null && r.body().success) {
                    for (Models.Payment p : r.body().data.payments) {
                        TextView tv = new TextView(MyPaymentsActivity.this);
                        tv.setPadding(32, 20, 32, 20);
                        tv.setText("💳 " + p.paymentDate + "\n" +
                            "Paid: TZS " + String.format("%,.0f", p.amountPaid) +
                            "  |  Balance: TZS " + String.format("%,.0f", p.balance) + "\n" +
                            "Method: " + p.paymentMethod.toUpperCase() +
                            (p.reference != null && !p.reference.isEmpty() ? "  |  Ref: " + p.reference : ""));
                        tv.setBackgroundColor(Color.parseColor("#e8f5e9"));
                        tv.setTextSize(13);
                        llContent.addView(tv);
                        View div = new View(MyPaymentsActivity.this);
                        div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        div.setBackgroundColor(Color.LTGRAY);
                        llContent.addView(div);
                    }
                    if (r.body().data.payments.isEmpty()) {
                        TextView tv = new TextView(MyPaymentsActivity.this);
                        tv.setText("No payments found."); tv.setPadding(32,32,32,32);
                        tv.setGravity(android.view.Gravity.CENTER); llContent.addView(tv);
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponse<Models.PaymentsData>> c, Throwable t) { swipe.setRefreshing(false); }
        });
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
