package com.fairshare.app;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Member m);

    @Insert
    void insertAll(List<Member> members);

    @Query("SELECT * FROM members ORDER BY name")
    LiveData<List<Member>> getAll();

    @Query("SELECT COUNT(*) FROM members")
    int count();

    @Query("SELECT * FROM members WHERE id=:id LIMIT 1")
    Member byId(long id);
}
