package com.example.menuw.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuw.R;
import com.example.menuw.data.Order;
import com.example.menuw.fragments.FoodItemFragment;

import java.util.List;

public class OrdersRecylerViewAdapter extends RecyclerView.Adapter<OrdersRecylerViewAdapter.ViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrdersRecylerViewAdapter(Context context, List<Order> orderList) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrdersRecylerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersRecylerViewAdapter.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.fooditem.setText(order.getFooditem());
        holder.restaurant.setText(order.getRestaurant());
        holder.price.setText(order.getPrice());
        if (order.getImage() != null && !order.getImage().isEmpty()) {
            byte[] decodedBytes = Base64.decode(order.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.image.setImageBitmap(bitmap);
        } else {
            holder.image.setImageResource(R.drawable.food_item_placeholder);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOptions(holder.getAdapterPosition(), holder.itemView);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fooditem;
        private TextView restaurant;
        private TextView price;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fooditem = itemView.findViewById(R.id.fooditem_order);
            restaurant = itemView.findViewById(R.id.restaurant_order);
            price = itemView.findViewById(R.id.price_order);
            image = itemView.findViewById(R.id.order_list_image);
        }
    }

    private void showOptions(int position, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.popup_edit_delete_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.edit) {
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new FoodItemFragment())
                            .commit();
                } else if (item.getItemId() == R.id.delete) {
                    confirmDeletion(position);
                }
                return true;
            }
        });
        popupMenu.show();

    }

    private void confirmDeletion(int position) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        alertDialog.setTitle("Confirm Deletion");
        alertDialog.setMessage("Are you sure you want to delete this item?");
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            orderList.remove(position);
            notifyItemRemoved(position);
        });
        alertDialog.setNegativeButton("No", (dialog, which) -> {
        });
        alertDialog.show();
    }
}
