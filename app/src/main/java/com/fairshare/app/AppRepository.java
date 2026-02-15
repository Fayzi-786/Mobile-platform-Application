package com.fairshare.app;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.List;

public class AppRepository {
    private final MemberDao memberDao;
    private final ExpenseDao expenseDao;

    public AppRepository(Context ctx) {
        AppDatabase db = AppDatabase.get(ctx);
        memberDao = db.memberDao();
        expenseDao = db.expenseDao();
    }

    public LiveData<List<Member>> members() { return memberDao.getAll(); }
    public LiveData<List<ExpenseWithShares>> ledger() { return expenseDao.ledger(); }

    public long addExpense(Expense e, List<Long> participantIds) {
        long id = expenseDao.insert(e);
        double per = round2(e.amount / Math.max(1, participantIds.size()));
        List<ExpenseShare> shares = new ArrayList<>();
        for (Long pid : participantIds) shares.add(new ExpenseShare(id, pid, per));
        expenseDao.insertShares(shares);
        return id;
    }

    private static double round2(double v){ return Math.round(v * 100.0) / 100.0; }
}
