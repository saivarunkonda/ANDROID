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
import com.example.menuw.data.Restaurant;
import com.example.menuw.fragments.RestaurantFragment;

import java.util.ArrayList;

public class RestaurantListRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantListRecyclerViewAdapter.ViewHolder> {


    private final ArrayList<Restaurant> restaurantList;
    private Context context;

    public RestaurantListRecyclerViewAdapter(ArrayList<Restaurant> restaurantList, Context context) {
        this.restaurantList = restaurantList;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_view, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantListRecyclerViewAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        if (restaurant != null) {
            holder.restaurantName.setText(restaurant.getName());
            holder.restaurantType.setText(restaurant.getStyle());
            holder.restaurantLocation.setText(restaurant.getLocation());
            holder.minimumOrder.setText(String.valueOf(restaurant.getMinOrder()));
            if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
                byte[] decodedBytes = Base64.decode(restaurant.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.restaurantImage.setImageBitmap(bitmap);
            } else {
                holder.restaurantImage.setImageResource(R.drawable.restaurant_placeholder);
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showOptions(holder.getAdapterPosition(), holder.itemView);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView restaurantName;
        private final TextView restaurantType;
        private final TextView restaurantLocation;
        private final TextView minimumOrder;
        private final ImageView restaurantImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name_rv_tv);
            restaurantType = itemView.findViewById(R.id.restaurant_style_rv_tv);
            restaurantLocation = itemView.findViewById(R.id.restaurant_location_rv_tv);
            minimumOrder = itemView.findViewById(R.id.restaurant_minimum_order_rv_tv);
            restaurantImage = itemView.findViewById(R.id.restaurant_image_rv_iv);
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
                            .replace(R.id.fragment_container, new RestaurantFragment())
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
            restaurantList.remove(position);
            notifyItemRemoved(position);
        });
        alertDialog.setNegativeButton("No", (dialog, which) -> {
        });
        alertDialog.show();
    }
}
