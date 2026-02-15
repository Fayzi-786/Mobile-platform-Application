package com.fairshare.app;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class ExpenseWithShares {
    @Embedded public Expense expense;
    @Relation(parentColumn = "id", entityColumn = "expenseId", entity = ExpenseShare.class)
    public List<ExpenseShare> shares;
}
