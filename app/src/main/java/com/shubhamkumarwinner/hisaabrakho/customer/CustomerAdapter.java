package com.shubhamkumarwinner.hisaabrakho.customer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shubhamkumarwinner.hisaabrakho.R;
import com.shubhamkumarwinner.hisaabrakho.database.tables.Customer;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    int bal;
    private final LayoutInflater mInflater;
    private List<Customer> mCustomers; // Cached copy of customers
    private HandleCustomerClick clickListener;

    public CustomerAdapter(Context context, HandleCustomerClick clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.customer_items, parent, false);
        return new CustomerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = mCustomers.get(position);
        bal = customer.getBal();
//        here some changes
        String time = (System.currentTimeMillis() - customer.getDate())/1000+"";
        holder.bind(customer.getName(), time);
        if (bal>0){
            holder.textViewTotalResult.setTextColor(Color.parseColor("#26ab1c"));
            holder.textViewTotalResult.setText("₹ "+bal);
            holder.textViewGiveTake.setText("You will give");
        }else if (bal<0){
            holder.textViewTotalResult.setVisibility(View.VISIBLE);
            holder.textViewTotalResult.setTextColor(Color.parseColor("#F44336"));
            holder.textViewTotalResult.setText("₹ "+(-bal));
            holder.textViewGiveTake.setText("You will get");
        }else if (bal == 0){
            holder.textViewTotalResult.setTextColor(Color.BLACK);
            holder.textViewTotalResult.setText("₹ "+bal);
            holder.textViewGiveTake.setText("");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.itemClick(customer,holder.titleTextView, holder.nameTextView);
            }
        });
    }

    public void setCustomers(List<Customer> customers){
        mCustomers = customers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mCustomers != null)
            return mCustomers.size();
        else return 0;
    }


    class CustomerViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView, timeTextView, titleTextView, textViewTotalResult, textViewGiveTake;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.customer_name);
            titleTextView = itemView.findViewById(R.id.customer_title);
            timeTextView = itemView.findViewById(R.id.customer_time);
            textViewTotalResult = itemView.findViewById(R.id.total_result);
            textViewGiveTake = itemView.findViewById(R.id.give_take);
        }

        public void bind(String name, String time){
            nameTextView.setText(name);
            String title = name.charAt(0) + "";
            titleTextView.setText(title);
            if (Integer.parseInt(time)<60) {
                timeTextView.setText(time + " seconds ago");
            }else if (Integer.parseInt(time)>=60 &&Integer.parseInt(time)<120){
                String timeMinutes = Integer.parseInt(time)/60 + "";
                timeTextView.setText(timeMinutes+" minute ago");
            }else if (Integer.parseInt(time)>120 && Integer.parseInt(time)<3600){
                String timeMinutes = Integer.parseInt(time)/60 + "";
                timeTextView.setText(timeMinutes+" minutes ago");
            }else if (Integer.parseInt(time)>=3600 && Integer.parseInt(time)<7200){
                String timeHours = Integer.parseInt(time)/3600 + "";
                timeTextView.setText(timeHours+" hour ago");
            }else if (Integer.parseInt(time)>=7200 && Integer.parseInt(time)<86400){
                String timeHours = Integer.parseInt(time)/3600 + "";
                timeTextView.setText(timeHours+" hours ago");
            }else if (Integer.parseInt(time)>=86400 && Integer.parseInt(time)<172800){
                String timeDays = Integer.parseInt(time)/86400 + "";
                timeTextView.setText(timeDays+" day ago");
            }else if (Integer.parseInt(time)>=172800 && Integer.parseInt(time)<604800){
                String timeDays = Integer.parseInt(time)/86400 + "";
                timeTextView.setText(timeDays+" days ago");
            }else if (Integer.parseInt(time)>=604800 && Integer.parseInt(time)<1209600){
                String timeWeek = Integer.parseInt(time)/604800 + "";
                timeTextView.setText(timeWeek+" week ago");
            }else if (Integer.parseInt(time)>=1209600){
                String timeWeeks = Integer.parseInt(time)/604800 + "";
                timeTextView.setText(timeWeeks+" weeks ago");
            }
        }
    }

    public interface HandleCustomerClick{
        void itemClick(Customer customer, TextView titleTextView, TextView nameTextView);
    }
}
