package com.fairshare.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.VH> {
    private List<ExpenseWithShares> data;
    private Map<Long, String> names = new HashMap<>();
    private final SimpleDateFormat fmt = new SimpleDateFormat("MMM d", Locale.UK);

    static class VH extends RecyclerView.ViewHolder {
        TextView title, meta, amount;
        VH(@NonNull View v){ super(v);
            title=v.findViewById(R.id.txtTitle); meta=v.findViewById(R.id.txtMeta); amount=v.findViewById(R.id.txtAmount); }
    }

    public void submit(List<ExpenseWithShares> d){ this.data = d; notifyDataSetChanged(); }
    public void setNames(List<Member> members){
        names.clear(); if (members!=null) for (Member m: members) names.put(m.id, m.name);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v){
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_ledger,p,false));
    }
    @Override public void onBindViewHolder(@NonNull VH h, int i){
        ExpenseWithShares r = data.get(i);
        h.title.setText(r.expense.title);
        String payer = names.getOrDefault(r.expense.paidById, "Member");
        h.meta.setText(fmt.format(new Date(r.expense.dateMillis)) + " • Paid by " + payer +
                (r.expense.category==null? "" : " • " + r.expense.category));
        h.amount.setText(String.format(Locale.UK,"£%.2f", r.expense.amount));
    }
    @Override public int getItemCount(){ return data==null?0:data.size(); }
}
