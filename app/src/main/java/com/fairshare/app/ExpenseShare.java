package com.fairshare.app;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "expense_shares",
        foreignKeys = {
                @ForeignKey(entity = Expense.class, parentColumns = "id", childColumns = "expenseId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Member.class, parentColumns = "id", childColumns = "memberId", onDelete = ForeignKey.CASCADE)
        },
        indices = { @Index("expenseId"), @Index("memberId") }
)
public class ExpenseShare {
    @PrimaryKey(autoGenerate = true) public long id;
    public long expenseId;
    public long memberId;
    public double shareAmount;

    public ExpenseShare(long expenseId, long memberId, double shareAmount) {
        this.expenseId = expenseId; this.memberId = memberId; this.shareAmount = shareAmount;
    }
}
