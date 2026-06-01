package tz.co.kiwelu.water.ui.admin;

import android.os.Bundle;
import android.widget.Toast;
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
import tz.co.kiwelu.water.ui.adapter.CustomerAdapter;

public class PendingCustomersActivity extends AppCompatActivity {
    private CustomerAdapter adapter;
    private List<Models.Customer> list = new ArrayList<>();
    private SwipeRefreshLayout swipe;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_customers);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pending Approvals");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        swipe = findViewById(R.id.swipeRefresh);
        RecyclerView rv = findViewById(R.id.recyclerView);
        adapter = new CustomerAdapter(list, c -> {});
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        swipe.setOnRefreshListener(this::load);
        load();
    }

    private void load() {
        swipe.setRefreshing(true);
        RetrofitClient.getApi().getCustomers(1, "").enqueue(new Callback<ApiResponse<Models.CustomersData>>() {
            @Override public void onResponse(Call<ApiResponse<Models.CustomersData>> c, Response<ApiResponse<Models.CustomersData>> r) {
                swipe.setRefreshing(false);
                if (r.isSuccessful() && r.body() != null) {
                    list.clear();
                    for (Models.Customer cu : r.body().data.customers)
                        if ("pending".equals(cu.approvalStatus)) list.add(cu);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<ApiResponse<Models.CustomersData>> c, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(PendingCustomersActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
