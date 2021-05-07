package com.shubhamkumarwinner.hisaabrakho.transaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.snackbar.Snackbar;
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

public class GaveActivity extends AppCompatActivity{
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 103;
    private EditText amountGaveEt;
    private Button btnGave;
    TextView dateGaveTextView;
    CustomerTransaction customerTransaction;
    CustomerDao mDao;
    CustomerRoomDatabase database;
    private TransactionViewModel mTransactionViewModel;
    private CustomerViewModel mCustomerViewModel;
    private Toolbar toolbar;
    String number, amountGave, name, sms, myProfileName;
    int balance, bal, id, gaveMoney, gotMoney, runningBalance;
    long date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Transition enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide);
        getWindow().setEnterTransition(enterTransition);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gave);
        amountGaveEt = findViewById(R.id.amount_gave);
        btnGave = findViewById(R.id.btn_gave);
        dateGaveTextView = findViewById(R.id.date_gave);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#B71C1C"));
        }*/
        toolbar = findViewById(R.id.app_bar_gave);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.colorPrimaryGave));
        actionBar.setBackgroundDrawable(colorDrawable);

        database = CustomerRoomDatabase.getDatabase(this);
        mDao = database.customerDao();

        Intent getIntent = getIntent();
        number = getIntent.getStringExtra("NUMBER_TRANSACTION");
        balance = getIntent.getIntExtra("bal", 0);
        name = getIntent.getStringExtra("name");
        id = getIntent.getIntExtra("id", 0);
        gaveMoney = getIntent.getIntExtra("gaveMoney", 0);
        gotMoney = getIntent.getIntExtra("gotMoney", 0);
        runningBalance = getIntent.getIntExtra("RunningBalance", 0);
        date = getIntent.getLongExtra("Date", 0);
        actionBar.setTitle("Gave Money to "+name);

        if (date==0){
            date = Calendar.getInstance().getTimeInMillis();
        }
        Date date1 = new Date(date);
        dateGaveTextView.setText(formatDate(date1));

        if (gaveMoney==0){
            amountGaveEt.setText("");
        }else {
            amountGaveEt.setText(gaveMoney + "");
        }

        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        TransactionViewModelFactory factory = new TransactionViewModelFactory(database,number);
        mTransactionViewModel = new ViewModelProvider(this, factory).get(TransactionViewModel.class);
        mCustomerViewModel.ifAvailable(1).observe(this, new Observer<MyProfile>() {
            @Override
            public void onChanged(MyProfile myProfile) {
                myProfileName = myProfile.getMyName();
            }
        });

        btnGave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountGave = amountGaveEt.getText().toString();
                if (TextUtils.isEmpty(amountGave)){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    amountGaveEt.setFocusable(false);
                    amountGaveEt.setFocusableInTouchMode(true);
                    Snackbar .make(v, "Please enter amount !", Snackbar.LENGTH_SHORT).show();
                }else {
                    int amountGiven = Integer.parseInt(amountGave);
                    if (amountGiven == 0){
                        Toast.makeText(GaveActivity.this, "Please enter a valid amount...", Toast.LENGTH_SHORT).show();
                    }else {
                        bal = balance - amountGiven + gaveMoney;
                        customerTransaction = new CustomerTransaction(number, amountGiven, 0,
                                bal, Calendar.getInstance().getTimeInMillis());

                        if (id==0){
                            mTransactionViewModel.insert(database, customerTransaction);
//                            mTransactionViewModel.updateForAdding(database, customerTransaction);
                        }else {
                            mTransactionViewModel.getCustomerTransaction(database, id).observe(GaveActivity.this,
                                    new Observer<CustomerTransaction>() {
                                        @Override
                                        public void onChanged(CustomerTransaction customerTransaction) {
                                            customerTransaction.setBal(runningBalance + gaveMoney - amountGiven);
                                            customerTransaction.setGaveMoney(amountGiven);
                                            mTransactionViewModel.update(database, customerTransaction);
                                        }
                                    });

                            CustomerTransaction mCustomerTransaction = new CustomerTransaction(number, gaveMoney-amountGiven, gotMoney,
                                    runningBalance-amountGiven + gaveMoney, date);
                            mTransactionViewModel.updateCustomerTransaction(database, mCustomerTransaction);
                        }


                        Customer customer = new Customer(name, number, Calendar.getInstance().getTimeInMillis(), bal);
                        mCustomerViewModel.updateCustomer(customer);

                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                GaveActivity.this, R.style.BottomSheetTheme);
                        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.send_sms_bottom_sheet_layout,
                                        (LinearLayout)findViewById(R.id.bottomSheetSendSmsContainer));

                        bottomSheetView.findViewById(R.id.sms_cancel_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(GaveActivity.this, "Transaction added successfully", Toast.LENGTH_SHORT).show();
                                finishAfterTransition();
                            }
                        });
                        bottomSheetView.findViewById(R.id.sms_confirm_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendSMS(number, amountGave, bal);
                                bottomSheetDialog.dismiss();
                                finishAfterTransition();
                            }
                        });
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.setCancelable(false);
                        bottomSheetDialog.show();
                    }
                }

            }
        });

    }

    public void sendSMS(String mNumber, String mAmountGave, int mBal) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    PERMISSIONS_REQUEST_SEND_SMS);
        }else {
            try
            {
                if (mBal<0) {
                    sms = "You took ₹ " +mAmountGave+ " from "+ myProfileName +  "." + " You will now give ₹ " + (-mBal) + " in total.";
                }else {
                    sms = "You took ₹ " + mAmountGave + " from "+ myProfileName + "." + " You will now receive ₹ " + mBal + " in total.";
                }
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(mNumber, null, sms, null, null);
                Toast.makeText(getApplicationContext(), "Transaction added successfully \n\t\t\t\t\tMessage Sent",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception ErrVar)
            {
                Toast.makeText(getApplicationContext(), ErrVar.getMessage(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                sendSMS(number, amountGave, bal);
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot send sms", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yy");
        return dateFormat.format(dateObject);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

}