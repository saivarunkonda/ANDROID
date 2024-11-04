package com.example.menuw.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuw.R;
import com.example.menuw.data.Order;

import java.util.List;

public class OrderHistoryRecyclerViewAdapter extends RecyclerView.Adapter<OrderHistoryRecyclerViewAdapter.ViewHolder> {

    private List<Order> orders;

    public OrderHistoryRecyclerViewAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderHistoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderHistoryRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryRecyclerViewAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.txtRestaurant.setText(order.getRestaurant());
        holder.txtDish.setText(order.getFooditem());
        holder.txtPrice.setText(String.valueOf(order.getPrice()));
        if (order.getImage() != null && !order.getImage().isEmpty()) {
            byte[] decodedBytes = Base64.decode(order.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.imgDish.setImageBitmap(bitmap);
        } else {
            holder.imgDish.setImageResource(R.drawable.food_item_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtRestaurant;
        private TextView txtDish;
        private TextView txtPrice;
        private ImageView imgDish;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRestaurant = itemView.findViewById(R.id.restaurant_order);
            txtDish = itemView.findViewById(R.id.fooditem_order);
            txtPrice = itemView.findViewById(R.id.price_order);
            imgDish = itemView.findViewById(R.id.order_list_image);
        }
    }
}
