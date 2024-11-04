package com.example.menuw.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menuw.R;
import com.example.menuw.data.FoodItem;
import com.example.menuw.data.Restaurant;
import com.example.menuw.utilities.AppConstants;
import com.example.menuw.utilities.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final List<Restaurant> groupList;
    private DBHelper dbHelper;
    private final Map<String, List<FoodItem>> childMap;

    public ExpandableListAdapter(Context context, List<Restaurant> groupList) {
        this.context = context;
        this.groupList = groupList;
        this.dbHelper = DBHelper.getInstance(context);
        this.childMap = new HashMap<>();
        for (Restaurant restaurant : groupList) {
            if (dbHelper.getFoodItems(restaurant.getName()) != null) {
                childMap.put(restaurant.getName(), dbHelper.getFoodItems(restaurant.getName()));
            } else {
                childMap.put(restaurant.getName(), new ArrayList<>());
            }
        }
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String group = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, null);
        }
        TextView restaurantName = convertView.findViewById(R.id.restaurant_name_rv_tv);
        TextView restaurantStyle = convertView.findViewById(R.id.restaurant_style_rv_tv);
        TextView restaurantLocation = convertView.findViewById(R.id.restaurant_location_rv_tv);
        TextView restaurantMinimumOrder = convertView.findViewById(R.id.restaurant_minimum_order_rv_tv);
        ImageView restaurantImage = convertView.findViewById(R.id.restaurant_image_rv_iv);
        if (groupList.get(groupPosition).getImage() != null) {
            byte[] decodedBytes = Base64.decode(groupList.get(groupPosition).getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            restaurantImage.setImageBitmap(bitmap);
        } else {
            restaurantImage.setImageResource(R.drawable.restaurant_placeholder);
        }
        restaurantName.setText(groupList.get(groupPosition).getName());
        restaurantStyle.setText(groupList.get(groupPosition).getStyle());
        restaurantLocation.setText(groupList.get(groupPosition).getLocation());
        restaurantMinimumOrder.setText(String.valueOf(groupList.get(groupPosition).getMinOrder()));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String child = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_item, null);
        }
        TextView food = convertView.findViewById(R.id.fooditem_name_tv);
        TextView ingredients = convertView.findViewById(R.id.fooditem_mainingredients_tv);
        TextView price = convertView.findViewById(R.id.fooditem_price_tv);
        TextView restaurant = convertView.findViewById(R.id.fooditem_restaurant_tv);
        ImageView image = convertView.findViewById(R.id.fooditem_image_iv);
        if (Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition) != null) {
            if (Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getImage() != null) {
                byte[] decodedBytes = Base64.decode(groupList.get(groupPosition).getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                image.setImageBitmap(bitmap);
            } else {
                image.setImageResource(R.drawable.food_item_placeholder);
            }
            food.setText(Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getDishName());
            ingredients.setText(TextUtils.join(",", Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getMainIngredients()));
            price.setText(String.valueOf(Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getPrice()));
            restaurant.setText(Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getRestaurant());
        }
        NumberPicker counter = convertView.findViewById(R.id.number_picker);
        counter.setMinValue(1);
        counter.setMaxValue(10);
        counter.setValue(1);
        counter.setOrientation(NumberPicker.HORIZONTAL);
        ImageButton addButton = convertView.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
                String username = sharedPreferences.getString(AppConstants.USER_NAME, "");
                if (!username.isEmpty()) {
                    dbHelper.createOrder(username,
                            Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getDishName(),
                            Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getRestaurant(),
                            (int) (Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getPrice() * counter.getValue()),
                            Objects.requireNonNull(childMap.get(groupList.get(groupPosition).getName())).get(childPosition).getImage(),
                            counter.getValue());
                }
                Toast.makeText(context, "Order Created Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
