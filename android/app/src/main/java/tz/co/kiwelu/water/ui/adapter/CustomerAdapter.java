package tz.co.kiwelu.water.ui.adapter;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import tz.co.kiwelu.water.R;
import tz.co.kiwelu.water.model.Models;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.VH> {

    public interface OnClick { void onClick(Models.Customer c); }

    private final List<Models.Customer> list;
    private final OnClick listener;

    public CustomerAdapter(List<Models.Customer> list, OnClick listener) {
        this.list = list; this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_customer, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Models.Customer c = list.get(pos);
        h.tvName.setText(c.name);
        h.tvId.setText(c.customerId);
        h.tvMobile.setText(c.mobile);
        h.tvZone.setText(c.zone != null ? c.zone : "—");

        int color;
        switch (c.approvalStatus != null ? c.approvalStatus : "") {
            case "approved": color = Color.parseColor("#2E7D32"); break;
            case "rejected": color = Color.parseColor("#C62828"); break;
            default:         color = Color.parseColor("#E65100"); break;
        }
        h.tvStatus.setText(c.approvalStatus != null ? c.approvalStatus.toUpperCase() : "—");
        h.tvStatus.setTextColor(color);

        h.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvId, tvMobile, tvZone, tvStatus;
        VH(View v) {
            super(v);
            tvName   = v.findViewById(R.id.tvName);
            tvId     = v.findViewById(R.id.tvId);
            tvMobile = v.findViewById(R.id.tvMobile);
            tvZone   = v.findViewById(R.id.tvZone);
            tvStatus = v.findViewById(R.id.tvStatus);
        }
    }
}
