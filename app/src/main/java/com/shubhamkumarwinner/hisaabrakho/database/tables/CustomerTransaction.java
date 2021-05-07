package com.shubhamkumarwinner.hisaabrakho.database.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customer_transaction_table")
public class CustomerTransaction {
    @PrimaryKey(autoGenerate = true)
    public int detailId;
    @ColumnInfo(name = "number")
    private String phoneNumber;
    private int gaveMoney;
    private int gotMoney;
    private int bal;
    @ColumnInfo(name = "time_transaction_stamp")
    private long dateTransaction;

    public CustomerTransaction(String phoneNumber, int gaveMoney, int gotMoney, int bal, long dateTransaction) {
        this.phoneNumber = phoneNumber;
        this.gaveMoney = gaveMoney;
        this.gotMoney = gotMoney;
        this.bal = bal;
        this.dateTransaction = dateTransaction;
    }

    public int getDetailId() {
        return detailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getGaveMoney() {
        return gaveMoney;
    }

    public int getGotMoney() {
        return gotMoney;
    }

    public int getBal() {
        return bal;
    }

    public long getDateTransaction() {
        return dateTransaction;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGaveMoney(int gaveMoney) {
        this.gaveMoney = gaveMoney;
    }

    public void setGotMoney(int gotMoney) {
        this.gotMoney = gotMoney;
    }

    public void setBal(int bal) {
        this.bal = bal;
    }

    public void setDateTransaction(long dateTransaction) {
        this.dateTransaction = dateTransaction;
    }
}
