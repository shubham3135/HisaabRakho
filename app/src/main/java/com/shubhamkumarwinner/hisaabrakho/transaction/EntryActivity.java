package com.shubhamkumarwinner.hisaabrakho.transaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shubhamkumarwinner.hisaabrakho.R;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerDao;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerRoomDatabase;
import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.tables.CustomerTransaction;
import com.shubhamkumarwinner.hisaabrakho.database.tables.MyProfile;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.CustomerViewModel;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.TransactionViewModel;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.TransactionViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EntryActivity extends AppCompatActivity {
    TextView titleTextView, nameTextView, dateTextView, balTextView, gaveTakeTextView, runningBalTextView, smsDetailTextView;
    Button btnDelete, btnShare;
    FrameLayout editEntry;
    String name, number, title, sms, myProfileName, myProfileNumber;
    int runningBal, totalBal, gaveMoney, gotMoney, id;
    long date;
    Date date1;
    Intent intent;

    private CustomerViewModel mCustomerViewModel;
    private TransactionViewModel mTransactionViewModel;
    CustomerRoomDatabase mDatabase;
    CustomerDao mDao;
    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for transition
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        Transition enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.entry_slide);
//        getWindow().setEnterTransition(enterTransition);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        initialization();

        getIntents();

        date1 = new Date(date);


        setUpActivityProfiles();

        setUpViewModel(date1);


        clickListeners();
    }

    private void getIntents() {
        intent = getIntent();
        name = intent.getStringExtra("Name");
        number = intent.getStringExtra("Number");
        title = intent.getStringExtra("Title");
        runningBal = intent.getIntExtra("RunningBalance", 0);
        totalBal = intent.getIntExtra("TotalBalance", 0);
        gaveMoney = intent.getIntExtra("GaveMoney", 0);
        gotMoney = intent.getIntExtra("GotMoney", 0);
        id = intent.getIntExtra("TransactionId", 0);
        date = intent.getLongExtra("Date", 0);
    }

    private void setUpActivityProfiles() {
        titleTextView.setText(title);
        nameTextView.setText(name);
        if (gaveMoney==0){
            balTextView.setText("₹ "+gotMoney);
            balTextView.setTextColor(getResources().getColor(R.color.colorPrimaryGot));
            gaveTakeTextView.setText("You Got");
        }else if (gotMoney==0){
            balTextView.setText("₹ "+gaveMoney);
            balTextView.setTextColor(getResources().getColor(R.color.colorPrimaryGave));
            gaveTakeTextView.setText("You Gave");
        }
        if (runningBal<0) {
            runningBalTextView.setText("₹ "+(-runningBal));
            runningBalTextView.setTextColor(getResources().getColor(R.color.colorPrimaryGave));
        }else if (runningBal>0){
            runningBalTextView.setText("₹ "+runningBal);
            runningBalTextView.setTextColor(getResources().getColor(R.color.colorPrimaryGot));
        }
        dateTextView.setText(formatDate(date1)+"  "+ formatTime(date1));
    }

    private void initialization() {
        toolbar = findViewById(R.id.entry_app_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Entry Details");

        titleTextView = findViewById(R.id.entry_title);
        nameTextView = findViewById(R.id.entry_name);
        dateTextView = findViewById(R.id.entry_time);
        balTextView = findViewById(R.id.entry_bal);
        gaveTakeTextView = findViewById(R.id.entry_give_take);
        runningBalTextView = findViewById(R.id.entry_running_bal);
        btnDelete = findViewById(R.id.delete_btn);
        btnShare = findViewById(R.id.share_btn);
        editEntry = findViewById(R.id.edit_entry);
        smsDetailTextView = findViewById(R.id.sms_detail);
    }

    private void setUpViewModel(Date date1) {
        mDatabase = CustomerRoomDatabase.getDatabase(this);
        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        mDao = mDatabase.customerDao();
        TransactionViewModelFactory factory = new TransactionViewModelFactory(mDatabase,number);
        mTransactionViewModel = new ViewModelProvider(this, factory).get(TransactionViewModel.class);
        mCustomerViewModel.ifAvailable(1).observe(this, new Observer<MyProfile>() {
            @Override
            public void onChanged(MyProfile myProfile) {
                myProfileName = myProfile.getMyName();
                myProfileNumber = myProfile.getMyNumber();
                smsDetail(date1);
                smsDetailTextView.setText(sms+"\n\n");
            }
        });
    }

    private void clickListeners() {
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        EntryActivity.this, R.style.BottomSheetTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.delete_entry_bottom_sheet,
                                (LinearLayout)findViewById(R.id.bottomSheetDeleteEntryContainer));
                bottomSheetView.findViewById(R.id.delete_cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.delete_confirm_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomerTransaction customerTransaction = new CustomerTransaction(number,
                                gaveMoney, gotMoney, runningBal, date);
                        mTransactionViewModel.deleteTransaction(mDatabase, id);
                        CustomerTransaction mCustomerTransaction = new CustomerTransaction(number,
                                customerTransaction.getGaveMoney(),
                                customerTransaction.getGotMoney(),
                                customerTransaction.getGaveMoney() - customerTransaction.getGotMoney(),
                                date);
                        mTransactionViewModel.updateCustomerTransaction(mDatabase, mCustomerTransaction);
                        Customer customer = new Customer(name, number,
                                Calendar.getInstance().getTimeInMillis(),
                                totalBal + customerTransaction.getGaveMoney() - customerTransaction.getGotMoney());
                        mCustomerViewModel.updateCustomer(customer);
                        Toast.makeText(EntryActivity.this, "Your entry has been deleted successfully", Toast.LENGTH_SHORT).show();
                        finishAfterTransition();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.show();

            }
        });

        editEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
                ActivityOptions options = ActivityOptions.makeThumbnailScaleUpAnimation(v, bitmap, 0, 0);
                Intent editIntent = null;
                if (gaveMoney==0){
                    editIntent = new Intent(EntryActivity.this, GotActivity.class);
                }else if (gotMoney==0){
                    editIntent = new Intent(EntryActivity.this, GaveActivity.class);
                }

                editIntent.putExtra("name",name);
                editIntent.putExtra("NUMBER_TRANSACTION",number);
                editIntent.putExtra("bal",totalBal);
                editIntent.putExtra("id", id);
                editIntent.putExtra("gaveMoney", gaveMoney);
                editIntent.putExtra("gotMoney", gotMoney);
                editIntent.putExtra("RunningBalance", runningBal);
                editIntent.putExtra("Date", date);
                startActivity(editIntent, options.toBundle());
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, sms);
                startActivity(Intent.createChooser(intent, "Share using"));
            }
        });
    }

    private void smsDetail(Date date1) {
        if (gotMoney==0) {
            if (totalBal>=0) {
                sms = "#Hisaab Rakho...\n\n You took ₹ " + gaveMoney+ " from "+ myProfileName + "(" + myProfileNumber + ")" + " on " + formatDate(date1) + "  " + formatTime(date1) +
                        "." + " You will now receive ₹ " + totalBal + " in total. " +
                        "If you think this transaction is wrong come and beat me up.";
            }else {
                sms = "#Hisaab Rakho...\n\n You took ₹ " + gaveMoney +" from "+myProfileName + "(" + myProfileNumber + ")" + " on " + formatDate(date1) + "  " + formatTime(date1) +
                        "." + " You will now give ₹ " + (-totalBal) + " in total. " +
                        "If you think this transaction is wrong come and beat me up.";
            }

        }else if (gaveMoney==0){
            if (totalBal>=0) {
                sms = "#Hisaab Rakho...\n\n You gave "+myProfileName + "(" + myProfileNumber + ")" + " ₹ " + gotMoney + " on " + formatDate(date1) + "  " + formatTime(date1) +
                        "." + " You will now receive ₹ " + totalBal + " in total. " +
                        "If you think this transaction is wrong come and beat me up.";
            }else {
                sms = "#Hisaab Rakho...\n\n You gave " +myProfileName + "(" + myProfileNumber + ")" +" ₹ " + gotMoney + " on " + formatDate(date1) + "  " + formatTime(date1) +
                        "." + " You will now give ₹ " + (-totalBal) + " in total. " +
                        "If you think this transaction is wrong come and beat me up.";
            }
        }
    }


    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            finishAfterTransition();
        }
        return true;
    }

}