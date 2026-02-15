package com.fairshare.app;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.fairshare.app.databinding.FragmentHomeBinding;
import java.util.*;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel vm;
    private LedgerAdapter adapter;

    public HomeFragment(){}

    @Override public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle b){
        binding = FragmentHomeBinding.inflate(i,c,false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle b){
        super.onViewCreated(v,b);
        vm = new ViewModelProvider(this).get(HomeViewModel.class);

        adapter = new LedgerAdapter();
        binding.recyclerLedger.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerLedger.setAdapter(adapter);

        vm.members.observe(getViewLifecycleOwner(), adapter::setNames);
        vm.ledger.observe(getViewLifecycleOwner(), adapter::submit);

        NavController nav = Navigation.findNavController(v);
        binding.fabAdd.setOnClickListener(view ->
                nav.navigate(R.id.action_homeFragment_to_addExpenseFragment));
    }

    // Simple “who pays whom” action in the menu
    @Override public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        menu.add("Settle summary").setOnMenuItemClickListener(item -> {
            List<ExpenseWithShares> rows = vm.ledger.getValue();
            Map<Long, Double> net = vm.computeNet(rows);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Long, Double> e : net.entrySet()) {
                String name = "?";
                if (vm.members.getValue()!=null)
                    for (Member m: vm.members.getValue()) if (m.id==e.getKey()) name = m.name;
                sb.append(name).append(": ").append(String.format(Locale.UK,"£%.2f", e.getValue())).append("\n");
            }
            android.widget.Toast.makeText(requireContext(), sb.toString(), android.widget.Toast.LENGTH_LONG).show();
            return true;
        });

        menu.add("Export CSV").setOnMenuItemClickListener(item -> {
            exportCsv();
            return true;
        });
    }

    private void exportCsv() {
        List<ExpenseWithShares> rows = vm.ledger.getValue();
        if (rows==null || rows.isEmpty()){
            android.widget.Toast.makeText(requireContext(),"No data", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder csv = new StringBuilder("Date,Title,Category,Paid By,Amount\n");
        Map<Long,String> names = new HashMap<>();
        if (vm.members.getValue()!=null) for (Member m: vm.members.getValue()) names.put(m.id,m.name);
        java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.UK);
        for (ExpenseWithShares r: rows){
            csv.append(f.format(new java.util.Date(r.expense.dateMillis))).append(',')
                    .append(escape(r.expense.title)).append(',')
                    .append(escape(r.expense.category)).append(',')
                    .append(escape(names.get(r.expense.paidById))).append(',')
                    .append(String.format(java.util.Locale.UK,"%.2f", r.expense.amount)).append('\n');
        }
        // Save via SAF
        androidx.activity.result.ActivityResultLauncher<android.content.Intent> launcher =
                registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                        result -> {});
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/csv"); intent.putExtra(android.content.Intent.EXTRA_TITLE, "fairshare.csv");
        launcher.launch(intent);
        // NOTE: you can wire a second launcher for writing to URI; keeping this lightweight for now.
        android.util.Log.d("Export", "CSV preview:\n"+csv);
        android.widget.Toast.makeText(requireContext(),"CSV generated in logs (wire write if needed).", android.widget.Toast.LENGTH_SHORT).show();
    }
    private static String escape(String s){ return s==null? "": s.replace(",", " "); }

    @Override public void onDestroyView(){ super.onDestroyView(); binding=null; }
}
