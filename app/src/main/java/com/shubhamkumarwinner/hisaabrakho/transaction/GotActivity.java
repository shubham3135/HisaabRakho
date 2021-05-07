package com.shubhamkumarwinner.hisaabrakho.transaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.WindowManager;
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

public class GotActivity extends AppCompatActivity {
    public static final int PERMISSIONS_REQUEST_SEND_SMS = 103;
    CustomerTransaction customerTransaction;
    CustomerDao mDao;
    CustomerRoomDatabase database;
    String number, amountGot, name, sms, myProfileName;
    int balance, bal, id, gaveMoney, gotMoney, runningBalance;
    long date;
    private EditText amountGotEt;
    private Button btnGot;
    TextView dataGotTextView;
    private TransactionViewModel mTransactionViewModel;
    private CustomerViewModel mCustomerViewModel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Transition enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide);
        getWindow().setEnterTransition(enterTransition);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_got);
        amountGotEt = findViewById(R.id.amount_got);
        btnGot = findViewById(R.id.btn_got);
        dataGotTextView = findViewById(R.id.date_got);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#036803"));
        }*/

        toolbar = findViewById(R.id.app_bar_got);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.colorPrimaryGot));
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
        actionBar.setTitle("Got Money from " + name);

        if (date==0){
            date = System.currentTimeMillis();
        }
        Date date1 = new Date(date);
        dataGotTextView.setText(formatDate(date1));

        if (gotMoney == 0) {
            amountGotEt.setText("");
        } else {
            amountGotEt.setText(gotMoney + "");
        }

        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        TransactionViewModelFactory factory = new TransactionViewModelFactory(database, number);
        mTransactionViewModel = new ViewModelProvider(this, factory).get(TransactionViewModel.class);
        mCustomerViewModel.ifAvailable(1).observe(this, new Observer<MyProfile>() {
            @Override
            public void onChanged(MyProfile myProfile) {
                myProfileName = myProfile.getMyName();
            }
        });

        btnGot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountGot = amountGotEt.getText().toString();
                if (TextUtils.isEmpty(amountGot)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    amountGotEt.setFocusable(false);
                    amountGotEt.setFocusableInTouchMode(true);
                    Snackbar.make(v, "Please enter amount !", Snackbar.LENGTH_SHORT).show();
                } else {
                    int amountGots = Integer.parseInt(amountGot);
                    if (amountGots == 0) {
                        Toast.makeText(GotActivity.this, "Please enter a valid amount...", Toast.LENGTH_SHORT).show();
                    } else {
                        bal = balance + amountGots - gotMoney;
                        customerTransaction = new CustomerTransaction(number, 0, amountGots, bal,
                                Calendar.getInstance().getTimeInMillis());
                        if (id == 0) {
                            mTransactionViewModel.insert(database, customerTransaction);
                        } else {
                            mTransactionViewModel.getCustomerTransaction(database, id).observe(GotActivity.this,
                                    new Observer<CustomerTransaction>() {
                                        @Override
                                        public void onChanged(CustomerTransaction customerTransaction) {
                                            customerTransaction.setBal(runningBalance + amountGots - gotMoney);
                                            customerTransaction.setGotMoney(amountGots);
                                            mTransactionViewModel.update(database, customerTransaction);
                                        }
                                    });

                            CustomerTransaction mCustomerTransaction = new CustomerTransaction(number, gaveMoney, (-amountGots + gotMoney),
                                    runningBalance + amountGots - gotMoney, date);
                            mTransactionViewModel.updateCustomerTransaction(database, mCustomerTransaction);
                        }

                        Customer customer = new Customer(name, number, Calendar.getInstance().getTimeInMillis(), bal);
                        mCustomerViewModel.updateCustomer(customer);

                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                GotActivity.this, R.style.BottomSheetTheme);
                        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.send_sms_bottom_sheet_layout,
                                        (LinearLayout) findViewById(R.id.bottomSheetSendSmsContainer));

                        bottomSheetView.findViewById(R.id.sms_cancel_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(GotActivity.this, "Transaction added successfully", Toast.LENGTH_SHORT).show();

                                finishAfterTransition();
                            }
                        });
                        bottomSheetView.findViewById(R.id.sms_confirm_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendSMS(number, amountGot, bal);
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

    public void sendSMS(String mNumber, String mAmountGot, int mBal) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            try {
                if (mBal < 0) {
                    sms = "You gave ₹ " + mAmountGot +" to "+ myProfileName+ "." + " You will now give ₹ " + (-mBal) + " in total.";
                } else {
                    sms = "You gave ₹ " + mAmountGot +" to "+ myProfileName+ "." + " You will now receive ₹ " + mBal + " in total.";
                }
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(mNumber, null, sms, null, null);
                Toast.makeText(getApplicationContext(), "Transaction added successfully \n\t\t\t\t\tMessage Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ErrVar) {
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
                sendSMS(number, amountGot, bal);
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

}