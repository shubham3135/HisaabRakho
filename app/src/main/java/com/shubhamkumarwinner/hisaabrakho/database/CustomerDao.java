package com.shubhamkumarwinner.hisaabrakho.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.tables.CustomerTransaction;
import com.shubhamkumarwinner.hisaabrakho.database.tables.MyProfile;

import java.util.Date;
import java.util.List;

@Dao
public interface CustomerDao {
    // for my profile
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMyProfile(MyProfile myProfile);

    @Query("SELECT * FROM my_profile where id = :id")
    LiveData<MyProfile> ifAvailable(int id);

    @Update
    void updateProfile(MyProfile myProfile);


    // for customer detail
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Customer customer);

    @Update
    void updateCustomer(Customer customer);

    @Delete
    void deleteCustomer(Customer customer);

    @Query("select sum(bal) from customer_table where bal > 0")
    LiveData<Integer> getPositiveBalance();

    @Query("select sum(bal) from customer_table where bal < 0")
    LiveData<Integer> getNegativeBalance();

    @Query("SELECT * FROM customer_table ORDER BY time_stamp DESC")
    LiveData<List<Customer>> getTimedCustomer();

    @Query("SELECT * FROM customer_table WHERE name LIKE '%' || :search || '%' or number like :search || '%' ")
    LiveData<List<Customer>> getSearchCustomer(String search);

    @Query("Select Exists(Select * from customer_table where number = :number)")
    LiveData<Boolean> ifExists(String number);

    //for detail transaction
    @Insert
    void customerTransactionInsert(CustomerTransaction customerTransaction);

    @Update
    void customerTransactionUpdate(CustomerTransaction customerTransaction);

    @Query("select * from customer_transaction_table where number = :number ORDER BY time_transaction_stamp DESC")
    LiveData<List<CustomerTransaction>> getAllCustomerTransaction(String number);

    @Query("select * from customer_transaction_table where detailId = :id")
    LiveData<CustomerTransaction> getCustomerTransaction(int id);

    @Query("delete from customer_transaction_table where number = :number")
    void deleteTransactions(String number);

    @Query("select sum(gotMoney-gaveMoney) from customer_transaction_table where number = :number")
    LiveData<Integer> getTransactionBalance(String number);

    @Query("Delete  from customer_transaction_table where detailId = :id")
    void deleteCustomerTransaction(int id);

    @Query("update customer_transaction_table set bal= bal + :balance where number = :number and time_transaction_stamp > :time")
    void updateCustomerTransaction(String number, int balance, long time);

    // here may be mistake
    @Query("Select Exists(Select * from customer_transaction_table where detailId = :id)")
    LiveData<Boolean> ifTransactionExists(int id);

}
