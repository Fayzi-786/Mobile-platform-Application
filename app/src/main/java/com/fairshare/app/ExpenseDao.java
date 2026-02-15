package com.fairshare.app;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert long insert(Expense e);

    @Insert void insertShares(List<ExpenseShare> shares);

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY dateMillis DESC, id DESC")
    LiveData<List<ExpenseWithShares>> ledger();
}
