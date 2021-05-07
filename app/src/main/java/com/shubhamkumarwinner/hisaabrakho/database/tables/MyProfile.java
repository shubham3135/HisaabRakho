package com.shubhamkumarwinner.hisaabrakho.database.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "my_profile")
public class MyProfile {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String myName;
    private String myNumber;

    public MyProfile(String myName, String myNumber) {
        this.myName = myName;
        this.myNumber = myNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(String myNumber) {
        this.myNumber = myNumber;
    }
}
