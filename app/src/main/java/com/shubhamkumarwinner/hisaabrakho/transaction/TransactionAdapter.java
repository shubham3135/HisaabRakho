package com.shubhamkumarwinner.hisaabrakho.transaction;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shubhamkumarwinner.hisaabrakho.R;
import com.shubhamkumarwinner.hisaabrakho.database.tables.CustomerTransaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final LayoutInflater mInflater;
    private List<CustomerTransaction> mTransactionCustomers;
    private HandleTransactionClick clickListener;

    public TransactionAdapter(Context context, HandleTransactionClick clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.transaction_detail, parent, false);
        return new TransactionAdapter.TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        CustomerTransaction customerTransaction = mTransactionCustomers.get(position);
            holder.transactionBal.setBackgroundColor(
                    Color.parseColor(customerTransaction.getBal()>=0?"#e2fcea":"#ffebee"));

        // here some changes
        holder.bind(new Date(customerTransaction.getDateTransaction()), customerTransaction.getBal(),
                customerTransaction.getGaveMoney(), customerTransaction.getGotMoney());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.itemClick(customerTransaction, v);
            }
        });

    }

    public void setTransactionCustomers(List<CustomerTransaction> transactionCustomers){
        mTransactionCustomers = transactionCustomers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mTransactionCustomers!=null) {
            return mTransactionCustomers.size();
        }else
            return 0;
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder{
        private TextView transactionDate, transactionBal, amountGave, amountGot;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionDate = itemView.findViewById(R.id.transaction_date);
            transactionBal = itemView.findViewById(R.id.transaction_bal);
            amountGave = itemView.findViewById(R.id.amount_gave);
            amountGot = itemView.findViewById(R.id.amount_got);
        }
        public void bind(Date date, int bal, int gaveAmount, int gotAmount){
            transactionDate.setText(formatDate(date)+"  "+ formatTime(date));
            if (bal>=0) {
                transactionBal.setText("Bal: ₹ " + bal);
            }else {
                transactionBal.setText("Bal: ₹ "+(-bal));
            }
            if (gaveAmount==0){
                amountGave.setText("");
            }else {
                amountGave.setText("₹ "+gaveAmount);
            }
            if (gotAmount==0) {
                amountGot.setText("");
            }else {
                amountGot.setText("₹ "+gotAmount);
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

    public interface HandleTransactionClick{
        void itemClick(CustomerTransaction customerTransaction, View view);
    }
}
