package com.shubhamkumarwinner.hisaabrakho.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shubhamkumarwinner.hisaabrakho.MainActivity;
import com.shubhamkumarwinner.hisaabrakho.R;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerDao;
import com.shubhamkumarwinner.hisaabrakho.database.CustomerRoomDatabase;
import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.CustomerViewModel;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.TransactionViewModel;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.TransactionViewModelFactory;

import java.util.Calendar;

public class CustomerProfileActivity extends AppCompatActivity {
    TextView profileTitle, profileName, profileNumber;
    Toolbar profileToolBar;
    ActionBar actionBar;
    Button btnDelete;
    CustomerDao mDao;
    CustomerRoomDatabase database;
    private CustomerViewModel mCustomerViewModel;
    private TransactionViewModel mTransactionViewModel;
    TransactionViewModelFactory factory;
    String name;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //here may be mistake for transition
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Transition enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.tool_slide);
        getWindow().setEnterTransition(enterTransition);
        super.onCreate(savedInstanceState);
        setTitle("Customer Profile");
        setContentView(R.layout.activity_customer_profile);
        initialization();

        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        String title = intent.getStringExtra("title");
        number = intent.getStringExtra("number");

        profileTitle.setText(title);
        profileNumber.setText(number);
        profileName.setText(name);

        setUpViewModel();
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        CustomerProfileActivity.this, R.style.BottomSheetTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.delete_bottom_sheet_layout,
                                (LinearLayout)findViewById(R.id.bottomSheetDeleteContainer));
                bottomSheetView.findViewById(R.id.delete_cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.delete_confirm_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // here some changes
                        Customer customer = new Customer(name, number, Calendar.getInstance().getTimeInMillis(), 5);
                        mCustomerViewModel.deleteCustomer(customer);
                        mTransactionViewModel.deleteAllTransactions(database, number);
                        Toast.makeText(CustomerProfileActivity.this, "Your customer has been deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(CustomerProfileActivity.this, MainActivity.class);
                        startActivity(mainIntent, bundle);
                        finishAffinity();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.show();
            }
        });
    }

    private void setUpViewModel() {
        database = CustomerRoomDatabase.getDatabase(this);
        mDao = database.customerDao();
        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        factory = new TransactionViewModelFactory(database,number);
        mTransactionViewModel = new ViewModelProvider(this, factory).get(TransactionViewModel.class);
    }

    private void initialization() {
        profileTitle = findViewById(R.id.profile_title);
        profileName = findViewById(R.id.profile_name);
        profileNumber = findViewById(R.id.profile_number);
        btnDelete = findViewById(R.id.profile_delete);
        profileToolBar = findViewById(R.id.custom_profile_toolbar);
        setSupportActionBar(profileToolBar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finishAfterTransition();
        return true;
    }
}