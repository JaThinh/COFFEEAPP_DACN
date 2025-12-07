package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Address> addressList;
    private OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onDeleteClick(Address address);
        void onAddressClick(Address address);
    }

    public AddressAdapter(List<Address> addressList, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address, listener);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void updateList(List<Address> newList) {
        this.addressList = newList;
        notifyDataSetChanged();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAddress;
        ImageButton btnDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAddressName);
            tvPhone = itemView.findViewById(R.id.tvAddressPhone);
            tvAddress = itemView.findViewById(R.id.tvAddressFull);
            btnDelete = itemView.findViewById(R.id.btnDeleteAddress);
        }

        public void bind(Address address, OnAddressClickListener listener) {
            tvName.setText(address.getName());
            tvPhone.setText(address.getPhone());
            tvAddress.setText(address.getFullAddress());

            btnDelete.setOnClickListener(v -> listener.onDeleteClick(address));
            itemView.setOnClickListener(v -> listener.onAddressClick(address));
        }
    }
}
