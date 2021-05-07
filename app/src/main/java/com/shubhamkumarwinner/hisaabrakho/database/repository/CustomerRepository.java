package com.shubhamkumarwinner.hisaabrakho.database.repository;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerDao;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerRoomDatabase;
import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.tables.CustomerTransaction;
import com.shubhamkumarwinner.hisaabrakho.database.tables.MyProfile;

import java.util.List;

public class CustomerRepository {
    private CustomerDao mCustomerDao;
    private LiveData<List<Customer>> mAllCustomers;

    public CustomerRepository(Application application){
        CustomerRoomDatabase db = CustomerRoomDatabase.getDatabase(application);
        mCustomerDao = db.customerDao();
        mAllCustomers = mCustomerDao.getTimedCustomer();
    }

    public LiveData<MyProfile> ifAvailable(int id){
        return mCustomerDao.ifAvailable(id);
    }

    public void insertMyProfile(MyProfile myProfile){
        new insertMyProfileAsyncTask(mCustomerDao).execute(myProfile);
    }

    public void updateMyProfile(MyProfile myProfile){
        new updateMyProfileAsyncTask(mCustomerDao).execute(myProfile);
    }

    public LiveData<List<Customer>> getAllCustomers(){
        return mAllCustomers;
    }

    public LiveData<List<Customer>> getSearchCustomer(String search){
        return mCustomerDao.getSearchCustomer(search);
    }

    public LiveData<Integer> getPositiveBalance(){
        return mCustomerDao.getPositiveBalance();
    }

    public LiveData<Integer> getNegativeBalance(){
        return mCustomerDao.getNegativeBalance();
    }

    public LiveData<Boolean> ifExists(String number){
        return mCustomerDao.ifExists(number);
    }


    public void insert(Customer customer){
        new insertAsyncTask(mCustomerDao).execute(customer);
    }

    public void updateCustomer(Customer customer){
        new  updateAsyncTask(mCustomerDao).execute(customer);
    }

    public void  deleteCustomer(Customer customer){
        new deleteAsyncTask(mCustomerDao).execute(customer);
    }


    private static class insertMyProfileAsyncTask extends AsyncTask<MyProfile, Void, Void>{
        private CustomerDao mAsyncTaskDao;
        insertMyProfileAsyncTask(CustomerDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MyProfile... myProfiles) {
            mAsyncTaskDao.insertMyProfile(myProfiles[0]);
            return null;
        }
    }

    private static class updateMyProfileAsyncTask extends AsyncTask<MyProfile, Void, Void>{
        private CustomerDao mAsyncTaskDao;
        updateMyProfileAsyncTask(CustomerDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MyProfile... myProfiles) {
            mAsyncTaskDao.updateProfile(myProfiles[0]);
            return null;
        }
    }



    private static class insertAsyncTask extends AsyncTask<Customer, Void, Void> {

        private CustomerDao mAsyncTaskDao;

        insertAsyncTask(CustomerDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Customer... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Customer, Void, Void> {

        private CustomerDao mAsyncTaskDao;

        updateAsyncTask(CustomerDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Customer... params) {
            mAsyncTaskDao.updateCustomer(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Customer, Void, Void> {

        private CustomerDao mAsyncTaskDao;

        deleteAsyncTask(CustomerDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Customer... params) {
            mAsyncTaskDao.deleteCustomer(params[0]);
            return null;
        }
    }
}
