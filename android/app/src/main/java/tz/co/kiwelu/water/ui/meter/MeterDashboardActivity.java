package tz.co.kiwelu.water.ui.meter;

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

public class MeterDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvAssigned, tvPending, tvToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvWelcome  = findViewById(R.id.tvWelcome);
        tvAssigned = findViewById(R.id.tvAssigned);
        tvPending  = findViewById(R.id.tvPending);
        tvToday    = findViewById(R.id.tvToday);

        tvWelcome.setText("Welcome, " + App.getSession().getName());

        MaterialCardView cardAddReading = findViewById(R.id.cardAddReading);
        cardAddReading.setOnClickListener(v ->
            startActivity(new Intent(this, AddReadingActivity.class)));

        MaterialCardView cardMyReadings = findViewById(R.id.cardMyReadings);
        cardMyReadings.setOnClickListener(v ->
            startActivity(new Intent(this, MyReadingsActivity.class)));

        MaterialCardView cardAddCustomer = findViewById(R.id.cardAddCustomer);
        cardAddCustomer.setOnClickListener(v ->
            startActivity(new Intent(this, AddCustomerActivity.class)));

        loadDashboard();
    }

    private void loadDashboard() {
        RetrofitClient.getApi().dashboard().enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                   Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Map<String, Object> d = response.body().data;
                    tvAssigned.setText(String.valueOf(((Double) d.getOrDefault("assigned_customers", 0.0)).intValue()));
                    tvPending.setText(String.valueOf(((Double) d.getOrDefault("pending_readings", 0.0)).intValue()));
                    tvToday.setText(String.valueOf(((Double) d.getOrDefault("today_readings", 0.0)).intValue()));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(MeterDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
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
            finish(); return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
