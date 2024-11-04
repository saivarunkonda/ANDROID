package com.example.menuw.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
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
import com.example.menuw.data.FoodItem;
import com.example.menuw.fragments.FoodItemFragment;

import java.util.ArrayList;


public class FoodItemListRecyclerViewAdapter extends RecyclerView.Adapter<FoodItemListRecyclerViewAdapter.ViewHolder> {


    private final ArrayList<FoodItem> foodItemList;
    private Context context;

    public FoodItemListRecyclerViewAdapter(ArrayList<FoodItem> foodItemList, Context context) {
        this.foodItemList = foodItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodItemListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fooditem_list_view, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemListRecyclerViewAdapter.ViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        if (foodItem != null) {
            holder.dish.setText(foodItem.getDishName());
            holder.restaurant.setText(foodItem.getRestaurant());
            holder.foodIngredients.setText(TextUtils.join(", ", foodItem.getMainIngredients()));
            holder.price.setText(String.valueOf(foodItem.getPrice()));
            if (foodItem.getImage() != null && !foodItem.getImage().isEmpty()) {
                byte[] decodedBytes = Base64.decode(foodItem.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.foodImage.setImageBitmap(bitmap);
            } else {
                holder.foodImage.setImageResource(R.drawable.restaurant_placeholder);
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
        return foodItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dish;
        private final TextView restaurant;
        private final TextView foodIngredients;
        private final TextView price;
        private final ImageView foodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dish = itemView.findViewById(R.id.fooditem_name_rv_tv);
            restaurant = itemView.findViewById(R.id.fooditem_restaurant_rv_tv);
            foodIngredients = itemView.findViewById(R.id.fooditem_mainingredients_rv_tv);
            price = itemView.findViewById(R.id.fooditem_price_rv_tv);
            foodImage = itemView.findViewById(R.id.fooditem_image_rv_iv);
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
            foodItemList.remove(position);
            notifyItemRemoved(position);
        });
        alertDialog.setNegativeButton("No", (dialog, which) -> {
        });
        alertDialog.show();
    }
}
