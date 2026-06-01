package tz.co.kiwelu.water.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.model.Models;
import tz.co.kiwelu.water.network.RetrofitClient;

public class CustomerDetailActivity extends AppCompatActivity {
    private LinearLayout llContent;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list_simple);
        int id = getIntent().getIntExtra("id", 0);
        String name = getIntent().getStringExtra("name");
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle(name); getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        llContent = findViewById(R.id.llContent);
        if (id > 0) load(id);
    }

    private void load(int id) {
        RetrofitClient.getApi().getCustomer(id).enqueue(new Callback<ApiResponse<Models.CustomerDetail>>() {
            @Override public void onResponse(Call<ApiResponse<Models.CustomerDetail>> c, Response<ApiResponse<Models.CustomerDetail>> r) {
                if (r.isSuccessful() && r.body() != null && r.body().success) {
                    Models.CustomerDetail d = r.body().data;
                    Models.Customer cu = d.customer;

                    addRow("Customer ID", cu.customerId);
                    addRow("Name", cu.name);
                    addRow("Mobile", cu.mobile);
                    addRow("Zone", cu.zone != null ? cu.zone : "—");
                    addRow("Address", cu.address != null ? cu.address : "—");
                    addRow("Status", cu.status);
                    addRow("Approval", cu.approvalStatus);

                    addSectionHeader("Recent Bills");
                    if (d.bills != null) for (Models.Bill b : d.bills)
                        addRow(b.billMonth, "TZS " + String.format("%,.0f", b.amount) + " — " + b.status);

                    addSectionHeader("Recent Payments");
                    if (d.payments != null) for (Models.Payment p : d.payments)
                        addRow(p.paymentDate, "TZS " + String.format("%,.0f", p.amountPaid) + " via " + p.paymentMethod);
                }
            }
            @Override public void onFailure(Call<ApiResponse<Models.CustomerDetail>> c, Throwable t) {}
        });
    }

    private void addRow(String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(32, 16, 32, 16);
        TextView lv = new TextView(this); lv.setText(label); lv.setTextColor(Color.GRAY); lv.setTextSize(13);
        lv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        TextView rv = new TextView(this); rv.setText(value); rv.setTextSize(13); rv.setTextStyle(android.graphics.Typeface.BOLD);
        rv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(lv); row.addView(rv);
        llContent.addView(row);
    }

    private void addSectionHeader(String title) {
        TextView tv = new TextView(this);
        tv.setText(title); tv.setTextSize(14); tv.setTextStyle(android.graphics.Typeface.BOLD);
        tv.setPadding(32, 24, 32, 8); tv.setTextColor(Color.parseColor("#1565C0"));
        tv.setBackgroundColor(Color.parseColor("#e3f2fd"));
        llContent.addView(tv);
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
