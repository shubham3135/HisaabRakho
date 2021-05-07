package com.shubhamkumarwinner.hisaabrakho.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.tables.CustomerTransaction;
import com.shubhamkumarwinner.hisaabrakho.database.tables.MyProfile;

import java.util.Calendar;

@Database(entities = {Customer.class, CustomerTransaction.class, MyProfile.class}, version = 1, exportSchema = false)
public abstract class CustomerRoomDatabase extends RoomDatabase {
    public abstract CustomerDao customerDao();

    private static volatile CustomerRoomDatabase INSTANCE;

    public static CustomerRoomDatabase getDatabase(final Context context){
        if (INSTANCE ==null){
            synchronized (CustomerRoomDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CustomerRoomDatabase.class,
                            "customer_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /*private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final CustomerDao mDao;

        PopulateDbAsync(CustomerRoomDatabase db) {
            mDao = db.customerDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // here some changes
            CustomerTransaction customerTransaction = new CustomerTransaction("9504002017",
                    5, 0, 5, Calendar.getInstance().getTimeInMillis());
            mDao.customerTransactionInsert(customerTransaction);

            // here some changes
            CustomerTransaction customerTransaction1 = new CustomerTransaction("7250377272",
                    0, 10, 5, Calendar.getInstance().getTimeInMillis());
            mDao.customerTransactionInsert(customerTransaction1);
            return null;
        }
    }*/
}
