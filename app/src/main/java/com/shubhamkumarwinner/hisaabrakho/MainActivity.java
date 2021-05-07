package com.shubhamkumarwinner.hisaabrakho;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.shubhamkumarwinner.hisaabrakho.contact.ContactActivity;
import com.shubhamkumarwinner.hisaabrakho.customer.CustomerAdapter;
import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.tables.MyProfile;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.CustomerViewModel;
import com.shubhamkumarwinner.hisaabrakho.transaction.TransactionActivity;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomerAdapter.HandleCustomerClick {

    private CustomerViewModel mCustomerViewModel;

    private ExtendedFloatingActionButton fab;
    private Toolbar toolbar;
    private TextView giveBalance, getBalance, mTitle;
    private EditText searchEditText;
    String profileName, profileNumber, oldNumber, oldName;
    RecyclerView recyclerView;
    CustomerAdapter adapter;
    AppBarLayout appBarLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Fade enterTransition = new Fade();
        enterTransition.setDuration(1000);
        getWindow().setEnterTransition(enterTransition);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_HisaabRakho);
        setContentView(R.layout.activity_main);

        initialization();
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen) {
                fab.setVisibility(View.INVISIBLE);
                appBarLayout.setExpanded(false, true);
                searchEditText.setFocusable(true);

            }else {
                fab.setVisibility(View.VISIBLE);
                appBarLayout.setExpanded(true, true);
                searchEditText.setFocusable(false);
                searchEditText.setFocusableInTouchMode(true);
            }
        });

        setupRecyclerView();

        setupViewModel();

        listeners();

    }

    private void initialization() {
        toolbar = findViewById(R.id.app_bar_main);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        appBarLayout = findViewById(R.id.app_bar_layout_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        giveBalance = findViewById(R.id.give_balance);
        getBalance = findViewById(R.id.get_balance);
        searchEditText = findViewById(R.id.edit_text_search);
        fab = findViewById(R.id.fab);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new CustomerAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupViewModel() {
        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        mCustomerViewModel.getAllCustomers().observe(this, new Observer<List<Customer>>() {
            @Override
            public void onChanged(@Nullable final List<Customer> customers) {
                // Update the cached copy of the words in the adapter.
                adapter.setCustomers(customers);
            }
        });

        mCustomerViewModel.ifAvailable(1).observe(this, new Observer<MyProfile>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(MyProfile myProfile) {
                if (myProfile==null){
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            MainActivity.this, R.style.BottomSheetTheme);

                    bottomSheetDialog.setContentView(R.layout.profile_bottom_sheet);
                    bottomSheetDialog.setCancelable(false);
                    bottomSheetDialog.findViewById(R.id.profile_cancel_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });

                    bottomSheetDialog.findViewById(R.id.profile_confirm_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText nameEditText = bottomSheetDialog.findViewById(R.id.profile_set_name);
                            profileName = nameEditText.getText().toString().trim();
                            EditText numberEditText = bottomSheetDialog.findViewById(R.id.profile_set_number);
                            profileNumber = numberEditText.getText().toString().trim();
                            if (profileName.length()==0 || profileNumber.length()==0){
                                Toast.makeText(MainActivity.this, "Please enter your name and number", Toast.LENGTH_SHORT).show();
                            }else {
                                if (profileNumber.length()!=10){
                                    Toast.makeText(MainActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                                }else {
                                    MyProfile myProfile1 = new MyProfile(profileName, profileNumber);
                                    mCustomerViewModel.insertMyProfile(myProfile1);
                                    bottomSheetDialog.dismiss();
                                }
                            }
                        }
                    });
                    bottomSheetDialog.show();
                }else {
                    oldName = myProfile.getMyName();
                    oldNumber = myProfile.getMyNumber();
                    mTitle.setText(oldName);
                }
            }
        });

        mCustomerViewModel.getPositiveBalance().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null){
                    giveBalance.setText("₹ 0");
                }else {
                    giveBalance.setText("₹ " + integer);
                }
            }
        });

        mCustomerViewModel.getNegativeBalance().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null){
                    getBalance.setText("₹ 0");
                }else {
                    getBalance.setText("₹ " + (-integer));
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void listeners() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.length()>0) {
                    mCustomerViewModel.getSearchCustomer(text).observe(MainActivity.this, new Observer<List<Customer>>() {
                        @Override
                        public void onChanged(List<Customer> customers) {
                            adapter.setCustomers(customers);
                        }
                    });
                }else {
                    mCustomerViewModel.getAllCustomers().observe(MainActivity.this, new Observer<List<Customer>>() {
                        @Override
                        public void onChanged(List<Customer> customers) {
                            adapter.setCustomers(customers);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for transition
                Pair[] pair = new Pair[1];
                pair[0] = new Pair<View, String>(fab, "fab_transition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pair);
                Intent contactIntent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contactIntent, options.toBundle());
            }
        });


        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY + 12 && fab.isExtended()) {
                    fab.shrink();
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !fab.isExtended()) {
                    fab.extend();
                }
            }
        });
    }


    @Override
    public void itemClick(Customer customer, TextView titleTextView, TextView nameTextView) {
        // for transition
        Pair[] pair = new Pair[2];
        pair[0] = new Pair<View, String>(titleTextView, "logo_shared");
        pair[1] = new Pair<View, String>(nameTextView, "name_shared");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pair);
        char title = customer.getName().charAt(0);
        Intent transactionIntent = new Intent(MainActivity.this, TransactionActivity.class);
        transactionIntent.putExtra("NAME", customer.getName());
        transactionIntent.putExtra("NUMBER", customer.getNumber());
        transactionIntent.putExtra("TITLE", title + "");
        transactionIntent.putExtra("BALANCE", customer.getBal());
        transactionIntent.putExtra("DATE", new Date(customer.getDate()).getTime());
        startActivity(transactionIntent, options.toBundle());
    }

}