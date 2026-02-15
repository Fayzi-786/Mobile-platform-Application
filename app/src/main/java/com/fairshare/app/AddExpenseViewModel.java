package com.fairshare.app;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.List;

public class AddExpenseViewModel extends AndroidViewModel {
    private final AppRepository repo;
    public final LiveData<List<Member>> members;

    public AddExpenseViewModel(@NonNull Application app) {
        super(app);
        repo = new AppRepository(app);
        members = repo.members();
    }

    public long save(String title, double amount, long dateMs, String category,
                     long paidById, List<Long> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) participantIds = new ArrayList<>();
        return repo.addExpense(new Expense(title, amount, dateMs, category, paidById), participantIds);
    }
}
