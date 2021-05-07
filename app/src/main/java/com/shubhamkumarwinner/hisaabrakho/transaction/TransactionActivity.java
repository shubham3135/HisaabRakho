package com.shubhamkumarwinner.hisaabrakho.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.shubhamkumarwinner.hisaabrakho.R;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerRoomDatabase;
import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.tables.CustomerTransaction;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.CustomerViewModel;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.TransactionViewModel;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.TransactionViewModelFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionActivity extends AppCompatActivity implements TransactionAdapter.HandleTransactionClick {
    private static final int PERMISSIONS_REQUEST_MAKE_CALL = 101;
    private TransactionViewModel mTransactionViewModel;
    private CustomerViewModel mCustomerViewModel;
    private Toolbar transactionToolBar;
    RecyclerView recyclerView;
    TextView nameText, titleText, giveTakeText, giveTakeAmount;
    Button gaveBtn, gotBtn;
    CustomerRoomDatabase mDatabase;
    AppBarLayout cardView;
    int bal;
    String number;
    String name;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //here may be mistake for transition
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        initialization();

        Intent intent = getIntent();
        name = intent.getStringExtra("NAME");
        number = intent.getStringExtra("NUMBER");
        title = intent.getStringExtra("TITLE");
        int balance = intent.getIntExtra("BALANCE", 0);
        nameText.setText(name);
        titleText.setText(title);

        mDatabase = CustomerRoomDatabase.getDatabase(getApplicationContext());

        // set up recycler view
        final TransactionAdapter adapter = new TransactionAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set up view model
        setUpViewModel(adapter);


        clickListeners(name, title);
    }

    private void clickListeners(String name, String title) {
        gaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for transition activity
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TransactionActivity.this);
                Intent gaveIntent = new Intent(TransactionActivity.this, GaveActivity.class);
                gaveIntent.putExtra("NUMBER_TRANSACTION", number);
                gaveIntent.putExtra("bal", bal);
                gaveIntent.putExtra("name", name);
                startActivity(gaveIntent, options.toBundle());
            }
        });
        gotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for transition activity
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TransactionActivity.this);
                Intent gotIntent = new Intent(TransactionActivity.this, GotActivity.class);
                gotIntent.putExtra("NUMBER_TRANSACTION", number);
                gotIntent.putExtra("bal", bal);
                gotIntent.putExtra("name", name);
                startActivity(gotIntent, options.toBundle());
            }
        });

        transactionToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TransactionActivity.this);
                Intent profileIntent = new Intent(TransactionActivity.this, CustomerProfileActivity.class);
                profileIntent.putExtra("number", number);
                profileIntent.putExtra("name", name);
                profileIntent.putExtra("title", title);
                startActivity(profileIntent, options.toBundle());
            }
        });
    }

    private void setUpViewModel(TransactionAdapter adapter) {
        TransactionViewModelFactory factory = new TransactionViewModelFactory(mDatabase,number);
        mTransactionViewModel = new ViewModelProvider(this, factory).get(TransactionViewModel.class);
        mTransactionViewModel.getListOfTransaction().observe(this, new Observer<List<CustomerTransaction>>() {
            @Override
            public void onChanged(List<CustomerTransaction> customerTransactions) {
                adapter.setTransactionCustomers(customerTransactions);
                if (customerTransactions.size()>0) {
                    cardView.setVisibility(View.VISIBLE);
                }
                else {
                    cardView.setVisibility(View.GONE);
                }
            }
        });

        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        mTransactionViewModel.getTransactionBalance().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null) {
                } else {
                    bal = integer;
                    if (integer > 0) {
                        cardView.setVisibility(View.VISIBLE);
                        giveTakeAmount.setTextColor(getResources().getColor(R.color.colorPrimaryGot));
                        giveTakeAmount.setText("₹ " + integer);
                        giveTakeText.setText("You will give");
                    } else if (integer < 0) {
                        cardView.setVisibility(View.VISIBLE);
                        giveTakeAmount.setTextColor(getResources().getColor(R.color.colorPrimaryGave));
                        giveTakeAmount.setText("₹ " + (-integer));
                        giveTakeText.setText("You will get");
                    } else {
                        giveTakeAmount.setText("₹ 0");
                        giveTakeText.setText("Settled Up");
                    }
                }
            }
        });

    }

    private void initialization() {
        nameText = findViewById(R.id.custom_profile_name);
        titleText = findViewById(R.id.custom_profile_title);
        gaveBtn = findViewById(R.id.gave_btn);
        gotBtn = findViewById(R.id.got_btn);
        giveTakeText = findViewById(R.id.give_take_text);
        giveTakeAmount = findViewById(R.id.give_take_amount);
        cardView = findViewById(R.id.card_view);
        recyclerView = findViewById(R.id.transaction_recycler_view);

        transactionToolBar = findViewById(R.id.transaction_toolbar);
        setSupportActionBar(transactionToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.transaction_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.transaction_call){
            makeCall();
        }
        if (item.getItemId() == android.R.id.home){
            // here may be mistake
            finishAfterTransition();
//            onBackPressed();
        }
        return true;
    }

    private void makeCall() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    PERMISSIONS_REQUEST_MAKE_CALL);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            // to make call
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+91" + number));
                startActivity(callIntent);
            }catch (ActivityNotFoundException e) {
                Toast.makeText(TransactionActivity.this, "Error in your phone call"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_MAKE_CALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                makeCall();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot make call", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void itemClick(CustomerTransaction customerTransaction, View view) {
        // for transition activity
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444);
        ActivityOptions options = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0);
        Intent entryIntent = new Intent(TransactionActivity.this, EntryActivity.class);
        entryIntent.putExtra("Name", name);
        entryIntent.putExtra("Title", title);
        entryIntent.putExtra("Number", number);
        entryIntent.putExtra("TransactionId", customerTransaction.getDetailId());
        entryIntent.putExtra("Date", customerTransaction.getDateTransaction());
        entryIntent.putExtra("RunningBalance", customerTransaction.getBal());
        entryIntent.putExtra("TotalBalance", bal);
        entryIntent.putExtra("GaveMoney", customerTransaction.getGaveMoney());
        entryIntent.putExtra("GotMoney", customerTransaction.getGotMoney());
        startActivity(entryIntent, options.toBundle());
    }
}