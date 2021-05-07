package com.shubhamkumarwinner.hisaabrakho.database.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.shubhamkumarwinner.hisaabrakho.database.CustomerDao;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerRoomDatabase;
import com.shubhamkumarwinner.hisaabrakho.database.tables.CustomerTransaction;

import java.util.List;

public class TransactionViewModel extends ViewModel {
    private LiveData<List<CustomerTransaction>> listOfTransaction;
    private CustomerDao mCustomerDao;
    private LiveData<Integer> mTransactionNetBalance;

    public TransactionViewModel(CustomerRoomDatabase database, String number){
        listOfTransaction = database.customerDao().getAllCustomerTransaction(number);
        mTransactionNetBalance = database.customerDao().getTransactionBalance(number);
    }

    public LiveData<List<CustomerTransaction>> getListOfTransaction() {
        return listOfTransaction;
    }

    // here may be mistake
    public LiveData<Boolean> ifTransactionExists(CustomerRoomDatabase database, int id){
        return database.customerDao().ifTransactionExists(id);
    }

    public LiveData<CustomerTransaction> getCustomerTransaction(CustomerRoomDatabase database, int id){
        return database.customerDao().getCustomerTransaction(id);
    }


    public void insert(CustomerRoomDatabase database, CustomerTransaction customerTransaction){
        mCustomerDao = database.customerDao();
        new TransactionViewModel.insertAsyncTask(mCustomerDao).execute(customerTransaction);
    }

    public void update(CustomerRoomDatabase database, CustomerTransaction customerTransaction){
        mCustomerDao = database.customerDao();
        new updateCustomerTransactionAsyncTask(mCustomerDao).execute(customerTransaction);
    }

    public void updateForAdding(CustomerRoomDatabase database, CustomerTransaction customerTransaction){
        mCustomerDao = database.customerDao();
        new updateTransactionForAddingAsyncTask(mCustomerDao).execute(customerTransaction);
    }


    public void deleteAllTransactions(CustomerRoomDatabase database, String number){
        mCustomerDao = database.customerDao();
        new deleteAllTransactionsAsyncTask(mCustomerDao).execute(number);
    }

    public void deleteTransaction(CustomerRoomDatabase database, int id){
        mCustomerDao = database.customerDao();
        new deleteTransactionAsyncTask(mCustomerDao).execute(id);
    }

    public LiveData<Integer> getTransactionBalance(){
        return mTransactionNetBalance;
    }


    public void updateCustomerTransaction(CustomerRoomDatabase database, CustomerTransaction customerTransaction){
        new updateTransactionAsyncTask(database.customerDao()).execute(customerTransaction);
    }

    private static class updateTransactionAsyncTask extends AsyncTask<CustomerTransaction, Void, Void>{
        private CustomerDao mAsyncTaskDao;
        updateTransactionAsyncTask(CustomerDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(CustomerTransaction... customerTransactions) {
            mAsyncTaskDao.updateCustomerTransaction(customerTransactions[0].getPhoneNumber(),
                    customerTransactions[0].getGaveMoney()-customerTransactions[0].getGotMoney(),
                    customerTransactions[0].getDateTransaction());
            return null;
        }
    }

    private static class updateTransactionForAddingAsyncTask extends AsyncTask<CustomerTransaction, Void, Void>{
        private CustomerDao mAsyncTaskDao;
        updateTransactionForAddingAsyncTask(CustomerDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(CustomerTransaction... customerTransactions) {
            mAsyncTaskDao.updateCustomerTransaction(customerTransactions[0].getPhoneNumber(),
                    customerTransactions[0].getGotMoney()-customerTransactions[0].getGaveMoney(),
                    customerTransactions[0].getDateTransaction());
            return null;
        }
    }

    private static class updateCustomerTransactionAsyncTask extends AsyncTask<CustomerTransaction, Void, Void>{
        private CustomerDao mAsyncTaskDao;
        updateCustomerTransactionAsyncTask(CustomerDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(CustomerTransaction... customerTransactions) {
            mAsyncTaskDao.customerTransactionUpdate(customerTransactions[0]);
            return null;
        }
    }


    private static class insertAsyncTask extends AsyncTask<CustomerTransaction, Void, Void> {
        private CustomerDao mAsyncTaskDao;

        insertAsyncTask(CustomerDao dao) {
            mAsyncTaskDao = dao;
        }


        @Override
        protected Void doInBackground(CustomerTransaction... customerTransactions) {
            mAsyncTaskDao.customerTransactionInsert(customerTransactions[0]);
            return null;
        }
    }

    private static class deleteAllTransactionsAsyncTask extends AsyncTask<String, Void, Void> {

        private CustomerDao mAsyncTaskDao;

        deleteAllTransactionsAsyncTask(CustomerDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mAsyncTaskDao.deleteTransactions(strings[0]);
            return null;
        }

    }

    private static class deleteTransactionAsyncTask extends AsyncTask<Integer,  Void, Void>{
        private CustomerDao mAsyncTaskDao;

        deleteTransactionAsyncTask(CustomerDao dao){
            mAsyncTaskDao = dao;
        }


        @Override
        protected Void doInBackground(Integer... integers) {
            mAsyncTaskDao.deleteCustomerTransaction(integers[0]);
            return null;
        }
    }
}
