package com.fairshare.app;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "expenses",
        foreignKeys = @ForeignKey(
                entity = Member.class,
                parentColumns = "id",
                childColumns = "paidById",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("paidById")
)
public class Expense {
    @PrimaryKey(autoGenerate = true) public long id;
    public String title;
    public double amount;         // £ as double (simple)
    public long dateMillis;       //  ms
    public String category;
    public long paidById;         // member id

    public Expense(String title, double amount, long dateMillis, String category, long paidById) {
        this.title = title; this.amount = amount; this.dateMillis = dateMillis;
        this.category = category; this.paidById = paidById;
    }
}
