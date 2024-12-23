package com.example.kisan_buddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    private List<Crop> cropList;
    private OnItemClickListener onItemClickListener; // Listener for item clicks

    public CropAdapter(List<Crop> cropList) {
        this.cropList = cropList;
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crop_item, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = cropList.get(position);
        holder.cropNameTextView.setText(crop.getCropName());
        holder.cropPriceTextView.setText("Price: $" + crop.getPrice());
        holder.producerNameTextView.setText("Seller: " + crop.getProducerEmail());
        holder.bidCountTextView.setText("Bids: " + crop.getBidCount());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(crop);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cropList.size();
    }

    public static class CropViewHolder extends RecyclerView.ViewHolder {
        TextView cropNameTextView, cropPriceTextView, producerNameTextView, bidCountTextView;

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            cropNameTextView = itemView.findViewById(R.id.cropNameTextView);
            cropPriceTextView = itemView.findViewById(R.id.cropPriceTextView);
            producerNameTextView = itemView.findViewById(R.id.producerNameTextView);
            bidCountTextView = itemView.findViewById(R.id.bidCountTextView);
        }
    }

    // Method to update the entire crop list
    public void updateList(List<Crop> newCropList) {
        this.cropList = new ArrayList<>(newCropList); // Create a new list to avoid modifying the original reference
        notifyDataSetChanged();
    }

    // Method to filter crops based on a condition
    public void filterList(String query) {
        List<Crop> filteredList = new ArrayList<>();
        for (Crop crop : cropList) {
            if (crop.getCropName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(crop);
            }
        }
        updateList(filteredList);
    }

    // Method to sort crops by price
    public void sortByPrice() {
        List<Crop> sortedList = new ArrayList<>(cropList);
        sortedList.sort((crop1, crop2) -> {
            double price1 = Double.parseDouble(crop1.getPrice());
            double price2 = Double.parseDouble(crop2.getPrice());
            return Double.compare(price1, price2);
        });
        updateList(sortedList); // Update the adapter's list with the sorted data
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Crop crop);
    }

    // Method to set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
