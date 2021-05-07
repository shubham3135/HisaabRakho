package com.shubhamkumarwinner.hisaabrakho.database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "customer_table")
public class Customer {

    @NonNull
    @ColumnInfo(name = "bal")
    int mBal;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "number")
    private String mNumber;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    private long date;

    public Customer(@NonNull String mName, @NonNull String mNumber, @NonNull long date, @NonNull int mBal) {
        this.mName = mName;
        this.mNumber = mNumber;
        this.date = date;
        this.mBal = mBal;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getNumber() {
        return mNumber;
    }


    @NonNull
    public long getDate() {
        return date;
    }

    public int getBal() {
        return mBal;
    }
}
