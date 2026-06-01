package tz.co.kiwelu.water.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.ApiResponse;
import tz.co.kiwelu.water.model.Models;
import tz.co.kiwelu.water.network.RetrofitClient;
import tz.co.kiwelu.water.ui.adapter.CustomerAdapter;

public class CustomersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private EditText etSearch;
    private TextView tvEmpty;
    private CustomerAdapter adapter;
    private List<Models.Customer> customers = new ArrayList<>();
    private int currentPage = 1;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView  = findViewById(R.id.recyclerView);
        swipeRefresh  = findViewById(R.id.swipeRefresh);
        etSearch      = findViewById(R.id.etSearch);
        tvEmpty       = findViewById(R.id.tvEmpty);

        adapter = new CustomerAdapter(customers, c -> {
            Intent i = new Intent(this, CustomerDetailActivity.class);
            i.putExtra("id", c.id);
            i.putExtra("name", c.name);
            startActivity(i);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(() -> { currentPage = 1; loadCustomers(); });

        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                searchQuery = s.toString().trim();
                currentPage = 1;
                loadCustomers();
            }
            public void afterTextChanged(Editable s) {}
        });

        loadCustomers();
    }

    private void loadCustomers() {
        swipeRefresh.setRefreshing(true);
        RetrofitClient.getApi().getCustomers(currentPage, searchQuery)
            .enqueue(new Callback<ApiResponse<Models.CustomersData>>() {
                @Override
                public void onResponse(Call<ApiResponse<Models.CustomersData>> call,
                                       Response<ApiResponse<Models.CustomersData>> response) {
                    swipeRefresh.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        customers.clear();
                        customers.addAll(response.body().data.customers);
                        adapter.notifyDataSetChanged();
                        tvEmpty.setVisibility(customers.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Models.CustomersData>> call, Throwable t) {
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(CustomersActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
