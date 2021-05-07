package com.shubhamkumarwinner.hisaabrakho.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.shubhamkumarwinner.hisaabrakho.database.repository.CustomerRepository;
import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.tables.CustomerTransaction;
import com.shubhamkumarwinner.hisaabrakho.database.tables.MyProfile;

import java.util.List;

public class CustomerViewModel extends AndroidViewModel {

    private CustomerRepository mRepository;

    private final LiveData<List<Customer>> mAllCustomers;

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CustomerRepository(application);
        mAllCustomers = mRepository.getAllCustomers();
    }

    public LiveData<Integer> getPositiveBalance(){
        return mRepository.getPositiveBalance();
    }

    public LiveData<Integer> getNegativeBalance(){
        return mRepository.getNegativeBalance();
    }

    public LiveData<List<Customer>> getAllCustomers(){
        return mAllCustomers;
    }

    public LiveData<Boolean> ifExists(String number){
        return mRepository.ifExists(number);
    }

    public LiveData<List<Customer>> getSearchCustomer(String search){
        return mRepository.getSearchCustomer(search);
    }

    public LiveData<MyProfile> ifAvailable(int id){
        return mRepository.ifAvailable(id);
    }

    public void updateMyProfile(MyProfile myProfile){
        mRepository.updateMyProfile(myProfile);
    }

    public void insertMyProfile(MyProfile myProfile){
        mRepository.insertMyProfile(myProfile);
    }

    public void insert(Customer customer){mRepository.insert(customer);}

    public void updateCustomer(Customer customer){
        mRepository.updateCustomer(customer);
    }

    public void  deleteCustomer(Customer customer){
        mRepository.deleteCustomer(customer);
    }


}
