package tz.co.kiwelu.water.ui.meter;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.model.Models;
import tz.co.kiwelu.water.network.RetrofitClient;

public class AddReadingActivity extends AppCompatActivity {

    private AutoCompleteTextView acCustomer;
    private EditText etCurrentReading, etComment;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private TextView tvPrevReading;

    private List<Models.Customer> customerList = new ArrayList<>();
    private Models.Customer selectedCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reading);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        acCustomer      = findViewById(R.id.acCustomer);
        etCurrentReading = findViewById(R.id.etCurrentReading);
        etComment       = findViewById(R.id.etComment);
        btnSubmit       = findViewById(R.id.btnSubmit);
        progressBar     = findViewById(R.id.progressBar);
        tvPrevReading   = findViewById(R.id.tvPrevReading);

        loadCustomers();

        acCustomer.setOnItemClickListener((parent, view, position, id) -> {
            selectedCustomer = customerList.get(position);
        });

        btnSubmit.setOnClickListener(v -> submitReading());
    }

    private void loadCustomers() {
        RetrofitClient.getApi().getCustomers(1, "")
            .enqueue(new Callback<ApiResponse<Models.CustomersData>>() {
                @Override
                public void onResponse(Call<ApiResponse<Models.CustomersData>> call,
                                       Response<ApiResponse<Models.CustomersData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        customerList = response.body().data.customers;
                        List<String> names = new ArrayList<>();
                        for (Models.Customer c : customerList)
                            names.add(c.name + " (" + c.customerId + ")");
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddReadingActivity.this,
                            android.R.layout.simple_dropdown_item_1line, names);
                        acCustomer.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Models.CustomersData>> call, Throwable t) {
                    Toast.makeText(AddReadingActivity.this, "Failed to load customers", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void submitReading() {
        if (selectedCustomer == null) {
            Toast.makeText(this, "Select a customer", Toast.LENGTH_SHORT).show(); return;
        }
        String currentStr = etCurrentReading.getText().toString().trim();
        if (currentStr.isEmpty()) {
            Toast.makeText(this, "Enter current reading", Toast.LENGTH_SHORT).show(); return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        Map<String, Object> body = new HashMap<>();
        body.put("customer_id", selectedCustomer.id);
        body.put("current_reading", Double.parseDouble(currentStr));
        body.put("reading_date", new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        body.put("comment", etComment.getText().toString().trim());

        RetrofitClient.getApi().submitReading(body).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                   Response<ApiResponse<Map<String, Object>>> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Map<String, Object> d = response.body().data;
                    double units = d.containsKey("units_consumed") ? ((Double) d.get("units_consumed")) : 0;
                    Toast.makeText(AddReadingActivity.this,
                        "Reading submitted! Units: " + units, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddReadingActivity.this,
                        response.body() != null ? response.body().message : "Failed",
                        Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(AddReadingActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
