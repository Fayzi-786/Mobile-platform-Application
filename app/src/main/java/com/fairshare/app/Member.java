package com.fairshare.app;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "members")
public class Member {
    @PrimaryKey(autoGenerate = true) public long id;
    @NonNull public String name = "";
    public String colorHex;

    public Member(@NonNull String name, String colorHex) {
        this.name = name;
        this.colorHex = colorHex;
    }
}
