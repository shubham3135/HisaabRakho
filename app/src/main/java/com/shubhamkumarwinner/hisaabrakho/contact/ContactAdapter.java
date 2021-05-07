package com.shubhamkumarwinner.hisaabrakho.contact;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shubhamkumarwinner.hisaabrakho.R;
import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;
import com.shubhamkumarwinner.hisaabrakho.database.viewmodel.CustomerViewModel;
import com.shubhamkumarwinner.hisaabrakho.transaction.TransactionActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {
    private CustomerViewModel mCustomerViewModel;
    private final LayoutInflater layoutInflater;
    private Contacts contact;
    private final List<Contacts> contactsList;
    private final ArrayList<Contacts> arrayList;
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Contacts> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(arrayList);
            } else {
                for (Contacts contact : arrayList) {
                    if (contact.getName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            contact.getPhone().contains(constraint.toString())) {
                        filteredList.add(contact);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactsList.clear();
            contactsList.addAll((Collection<? extends Contacts>) results.values);
            notifyDataSetChanged();
        }
    };

    public ContactAdapter(LayoutInflater inflater, List<Contacts> contactsList) {
        this.layoutInflater = inflater;
        this.contactsList = contactsList;
        this.arrayList = new ArrayList<>(contactsList);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.contact_items, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        contact = contactsList.get(position);
        String name = contact.getName().trim();
        char title = name.charAt(0);
        String number = contact.getPhone();
        mCustomerViewModel = new ViewModelProvider((FragmentActivity) holder.itemView.getContext())
                .get(CustomerViewModel.class);

        mCustomerViewModel.ifExists(number).observe((LifecycleOwner) holder.itemView.getContext(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    holder.alreadyCustomer.setVisibility(View.VISIBLE);
                } else {
                    holder.alreadyCustomer.setVisibility(View.INVISIBLE);
                }
            }
        });

        holder.numberTextView.setText(number);
        holder.titleTextView.setText(title + "");
        holder.nameTextView.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
                ActivityOptions options = ActivityOptions.makeThumbnailScaleUpAnimation(v, bitmap, 0, 0);
                Intent transactionIntent = new Intent(holder.itemView.getContext(), TransactionActivity.class);
                transactionIntent.putExtra("NAME", name);
                transactionIntent.putExtra("NUMBER", number);
                transactionIntent.putExtra("TITLE", title + "");
                // here some changes
                Customer customer = new Customer(name, number, Calendar.getInstance().getTimeInMillis(), 0);
                mCustomerViewModel.insert(customer);
                holder.itemView.getContext().startActivity(transactionIntent, options.toBundle());
                ((FragmentActivity) holder.itemView.getContext()).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, numberTextView, titleTextView, alreadyCustomer;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.contact_title);
            nameTextView = itemView.findViewById(R.id.contact_name);
            numberTextView = itemView.findViewById(R.id.contact_number);
            alreadyCustomer = itemView.findViewById(R.id.already_customer);
        }
    }
}
