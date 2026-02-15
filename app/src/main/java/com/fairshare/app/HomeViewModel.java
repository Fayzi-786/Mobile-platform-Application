package com.fairshare.app;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    private final AppRepository repo;
    public final LiveData<List<Member>> members;
    public final LiveData<List<ExpenseWithShares>> ledger;

    public HomeViewModel(@NonNull Application app) {
        super(app);
        repo = new AppRepository(app);
        members = repo.members();
        ledger  = repo.ledger();
    }

    /** net per member: paid - owed */
    public Map<Long, Double> computeNet(List<ExpenseWithShares> rows){
        Map<Long, Double> net = new HashMap<>();
        if (rows == null) return net;
        for (ExpenseWithShares r : rows) {
            long payer = r.expense.paidById;
            net.put(payer, net.getOrDefault(payer, 0.0) + r.expense.amount);
            for (ExpenseShare s : r.shares) {
                net.put(s.memberId, net.getOrDefault(s.memberId, 0.0) - s.shareAmount);
            }
        }
        return net;
    }
}
