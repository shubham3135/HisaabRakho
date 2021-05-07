package com.shubhamkumarwinner.hisaabrakho.database.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerRoomDatabase;

public class TransactionViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final CustomerRoomDatabase mDb;
    String number;

    public TransactionViewModelFactory(CustomerRoomDatabase mDb, String number) {
        this.mDb = mDb;
        this.number = number;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TransactionViewModel(mDb, number);
    }
}
