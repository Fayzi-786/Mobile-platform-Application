package com.fairshare.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.fairshare.app.databinding.FragmentAddExpenseBinding;
import java.util.*;

public class AddExpenseFragment extends Fragment {
    private FragmentAddExpenseBinding binding;
    private AddExpenseViewModel vm;
    private long chosenDateMs = System.currentTimeMillis();
    private final List<Member> currentMembers = new ArrayList<>();

    @Override public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle b){
        binding = FragmentAddExpenseBinding.inflate(i,c,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle b){
        super.onViewCreated(v,b);
        vm = new ViewModelProvider(this).get(AddExpenseViewModel.class);

        // Date picker
        updateDateText();
        binding.txtDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance(); cal.setTimeInMillis(chosenDateMs);
            new DatePickerDialog(requireContext(), (d, y, m, day) -> {
                Calendar c2 = Calendar.getInstance(); c2.set(y, m, day, 12,0,0);
                chosenDateMs = c2.getTimeInMillis(); updateDateText();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Members live
        vm.members.observe(getViewLifecycleOwner(), members -> {
            currentMembers.clear(); if (members!=null) currentMembers.addAll(members);
            // Payer spinner
            ArrayAdapter<String> a = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    mapNames(members));
            binding.spPayer.setAdapter(a);
            // Checkboxes
            binding.boxMembers.removeAllViews();
            if (members!=null) for (Member m: members){
                CheckBox cb = new CheckBox(requireContext());
                cb.setText(m.name); cb.setTag(m.id); cb.setChecked(true);
                binding.boxMembers.addView(cb);
            }
        });

        binding.btnSave.setOnClickListener(view -> {
            String title = String.valueOf(binding.editTitle.getText()).trim();
            String cat   = String.valueOf(binding.editCategory.getText()).trim();
            double amount;
            try { amount = Double.parseDouble(String.valueOf(binding.editAmount.getText())); }
            catch (Exception ex){ binding.editAmount.setError("Enter a valid amount"); return; }

            long paidById = currentMembers.get(((Spinner)binding.spPayer).getSelectedItemPosition()).id;

            List<Long> participants = new ArrayList<>();
            for (int i=0;i<binding.boxMembers.getChildCount();i++){
                View child = binding.boxMembers.getChildAt(i);
                if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                    participants.add((Long) child.getTag());
                }
            }
            if (participants.isEmpty()){
                android.widget.Toast.makeText(requireContext(),"Select at least one participant", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            vm.save(title, amount, chosenDateMs, cat, paidById, participants);
            Navigation.findNavController(view).popBackStack();
        });
    }

    private void updateDateText(){
        java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("EEE, d MMM yyyy", java.util.Locale.UK);
        binding.txtDate.setText(f.format(new java.util.Date(chosenDateMs)));
    }

    private static List<String> mapNames(List<Member> list){
        List<String> r = new ArrayList<>(); if (list!=null) for (Member m: list) r.add(m.name); return r;
    }

    @Override public void onDestroyView(){ super.onDestroyView(); binding=null; }
}
