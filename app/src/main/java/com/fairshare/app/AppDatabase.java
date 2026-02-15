package com.fairshare.app;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.Arrays;
import java.util.concurrent.Executors;

@Database(
        entities = { Member.class, Expense.class, ExpenseShare.class },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MemberDao memberDao();
    public abstract ExpenseDao expenseDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                                    AppDatabase.class, "fairshare.db")
                            .addCallback(new Callback() {
                                @Override public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        // Seed 3 members
                                        AppDatabase d = get(ctx);
                                        d.memberDao().insertAll(Arrays.asList(
                                                new Member("Alice", "#9C27B0"),
                                                new Member("Bob",   "#2196F3"),
                                                new Member("Charlie", "#4CAF50")));
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
