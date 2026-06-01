package tz.co.kiwelu.water.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.App;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.network.RetrofitClient;
import tz.co.kiwelu.water.ui.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvCustomers, tvBills, tvCollected, tvPendingCusts, tvPendingReads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvWelcome      = findViewById(R.id.tvWelcome);
        tvCustomers    = findViewById(R.id.tvCustomers);
        tvBills        = findViewById(R.id.tvBills);
        tvCollected    = findViewById(R.id.tvCollected);
        tvPendingCusts = findViewById(R.id.tvPendingCusts);
        tvPendingReads = findViewById(R.id.tvPendingReads);

        tvWelcome.setText("Welcome, " + App.getSession().getName());

        // Card click listeners
        MaterialCardView cardCustomers = findViewById(R.id.cardCustomers);
        cardCustomers.setOnClickListener(v ->
            startActivity(new Intent(this, CustomersActivity.class)));

        MaterialCardView cardPending = findViewById(R.id.cardPending);
        cardPending.setOnClickListener(v ->
            startActivity(new Intent(this, PendingCustomersActivity.class)));

        MaterialCardView cardReadings = findViewById(R.id.cardReadings);
        cardReadings.setOnClickListener(v ->
            startActivity(new Intent(this, ReadingsActivity.class)));

        MaterialCardView cardBills = findViewById(R.id.cardBills);
        cardBills.setOnClickListener(v ->
            startActivity(new Intent(this, BillsActivity.class)));

        loadDashboard();
    }

    private void loadDashboard() {
        RetrofitClient.getApi().dashboard().enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                   Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Map<String, Object> d = response.body().data;
                    tvCustomers.setText(String.valueOf(((Double) d.getOrDefault("total_customers", 0.0)).intValue()));
                    tvBills.setText(String.valueOf(((Double) d.getOrDefault("active_bills", 0.0)).intValue()));
                    tvCollected.setText("TZS " + formatNum((Double) d.getOrDefault("total_collected", 0.0)));
                    tvPendingCusts.setText(String.valueOf(((Double) d.getOrDefault("pending_customers", 0.0)).intValue()));
                    tvPendingReads.setText(String.valueOf(((Double) d.getOrDefault("pending_readings", 0.0)).intValue()));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to load dashboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatNum(double v) {
        if (v >= 1_000_000) return String.format("%.1fM", v / 1_000_000);
        if (v >= 1_000)     return String.format("%.1fK", v / 1_000);
        return String.valueOf((int) v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Logout").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            App.getSession().clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
