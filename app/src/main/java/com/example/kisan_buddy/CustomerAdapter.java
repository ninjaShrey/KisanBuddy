package com.example.kisan_buddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private List<Customer> customerList;
    private OnItemClickListener listener;

    // Constructor
    public CustomerAdapter(List<Customer> customerList) {
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        // Set the customer data to the views
        holder.bidderNameTextView.setText(customer.getName());
        holder.bidAmountTextView.setText(String.valueOf(customer.getBidAmount()));
        holder.emailTextView.setText(customer.getEmail());

        // Handle item click events
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(customer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Interface for handling click events
    public interface OnItemClickListener {
        void onItemClick(Customer customer);
    }

    // ViewHolder class for RecyclerView items
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView bidderNameTextView;
        TextView bidAmountTextView;
        TextView emailTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            bidderNameTextView = itemView.findViewById(R.id.bidderNameTextView);
            bidAmountTextView = itemView.findViewById(R.id.bidAmountTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
        }
    }
}
