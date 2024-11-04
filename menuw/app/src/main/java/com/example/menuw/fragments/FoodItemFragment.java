package com.example.menuw.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.menuw.R;
import com.example.menuw.data.FoodItem;
import com.example.menuw.utilities.DBHelper;
import com.example.menuw.utilities.ImageConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FoodItemFragment extends Fragment {

    private RadioGroup radioGroup;
    private RadioButton radioButtonCreate, radioButtonUpdate;
    private EditText editTextFoodItemRestaurant, editTextFoodItemMainINgredients, editTextFoodItemPrice;
    private Button btnSaveFoodItem;
    private ImageView imageView;
    private AutoCompleteTextView editTextFoodItemDishName;
    private DBHelper dbHelper;
    private FoodItem foodItem;
    private int foodItemId;
    private String bitmapString = "";
    private ActivityResultLauncher<Intent> cameraResultLauncher;

    public FoodItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_item, container, false);
        radioGroup = view.findViewById(R.id.radio_group_food_item_frag);
        radioButtonCreate = view.findViewById(R.id.radio_button_create_food_item_frag);
        radioButtonUpdate = view.findViewById(R.id.radio_button_update_food_item_frag);
        editTextFoodItemDishName = view.findViewById(R.id.editTextFoodItemDishName);
        editTextFoodItemRestaurant = view.findViewById(R.id.editTextFoodItemRestaurant);
        editTextFoodItemMainINgredients = view.findViewById(R.id.editTextFoodItemMainINgredients);
        editTextFoodItemPrice = view.findViewById(R.id.editTextFoodItemPrice);
        btnSaveFoodItem = view.findViewById(R.id.btnSaveFooditem);
        imageView = view.findViewById(R.id.food_item_camera_iv);

        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(getActivity() != null) {
                            getActivity();
                            if (o.getResultCode() == Activity.RESULT_OK) {
                                Intent data = o.getData();
                                if (data != null && data.getExtras() != null) {
                                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                                    if (bitmap != null) {
                                        bitmapString = ImageConverter.convertBitmapToBase64(bitmap);
                                    }
                                    imageView.setImageURI((Uri) Objects.requireNonNull(data.getExtras()).get("data"));
                                }
                            }
                        }
                    }
                });

                dbHelper = DBHelper.getInstance(getActivity());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_create_food_item_frag) {
                    editTextFoodItemDishName.setEnabled(true);
                    editTextFoodItemRestaurant.setEnabled(true);
                    editTextFoodItemPrice.setEnabled(true);
                    editTextFoodItemRestaurant.setClickable(true);
                    editTextFoodItemRestaurant.setFocusable(true);
                    editTextFoodItemMainINgredients.setEnabled(true);
                    btnSaveFoodItem.setText("Save");
                } else if (checkedId == R.id.radio_button_update_food_item_frag) {
                    editTextFoodItemDishName.setEnabled(true);
                    editTextFoodItemRestaurant.setEnabled(false);
                    editTextFoodItemRestaurant.setClickable(false);
                    editTextFoodItemRestaurant.setFocusable(false);
                    editTextFoodItemPrice.setEnabled(true);
                    editTextFoodItemMainINgredients.setEnabled(true);
                    btnSaveFoodItem.setText("Update");
                    autoCompleteRestaurantValues();
                }
            }
        });

        editTextFoodItemDishName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButtonUpdate.isChecked()) {
                    autoCompleteRestaurantValues();
                }
            }
        });

        btnSaveFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButtonCreate.isChecked()) {
                    createFoodItem();
                } else if (radioButtonUpdate.isChecked()) {
                    updateFoodItem();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        return view;
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResultLauncher.launch(cameraIntent);
    }

    private void autoCompleteRestaurantValues() {
        // Fetch restaurant names from database
        List<FoodItem> foodItems = dbHelper.getAllFoodItems();

        // Create autocomplete adapter
        ArrayAdapter<String> adapterFoodItem = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, foodItems.stream().map(FoodItem::getDishName).collect(Collectors.toList()));
       /* ArrayAdapter<String> adapterType = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, restaurantNames.stream().map(Restaurant::getStyle).collect(Collectors.toList()));
        ArrayAdapter<String> adapterLocation = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, restaurantNames.stream().map(Restaurant::getLocation).collect(Collectors.toList()));
        List<String> minOrders = restaurantNames.stream()
                .map(restaurant -> String.valueOf(restaurant.getMinOrder()))
                .collect(Collectors.toList());

        ArrayAdapter<String> adapterMinOrder = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_item,
                minOrders
        );*/

        editTextFoodItemDishName.setThreshold(1);
        editTextFoodItemDishName.setAdapter(adapterFoodItem);

// Fetch restaurant data from database for update
        String selectedDishItem = editTextFoodItemDishName.getText().toString();
        for (FoodItem foodItem : foodItems) {
            if (foodItem.getDishName().equals(selectedDishItem)) {
                editTextFoodItemMainINgredients.setText(TextUtils.join(", ", foodItem.getMainIngredients()));
                editTextFoodItemRestaurant.setText(foodItem.getRestaurant());
                editTextFoodItemPrice.setText(String.valueOf(foodItem.getPrice()));
                bitmapString = foodItem.getImage();
                Bitmap bitmap = ImageConverter.convertBase64ToBitmap(bitmapString);
                imageView.setImageBitmap(bitmap);
                foodItemId = foodItem.getId();
                break;
            }
        }
    }

    private void createFoodItem() {
        String name = editTextFoodItemDishName.getText().toString();
        String restaurant = editTextFoodItemRestaurant.getText().toString();
        List<String> mainIngredients = Arrays.asList(editTextFoodItemMainINgredients.getText().toString().split(","));
        String image = bitmapString;
        float price = Float.parseFloat(editTextFoodItemPrice.getText().toString());

        if (!name.isEmpty() && !restaurant.isEmpty() && !mainIngredients.isEmpty() && price > 0) {
            FoodItem foodItem1 = new FoodItem(0, name, restaurant, mainIngredients, price, image);
            dbHelper.insertFoodItem(foodItem1);
            Toast.makeText(getActivity(), "Food item created successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFoodItem() {
        String name = editTextFoodItemDishName.getText().toString();
        String restaurant = editTextFoodItemRestaurant.getText().toString();
        List<String> mainIngredients = Arrays.asList(editTextFoodItemMainINgredients.getText().toString().split(","));
        String image = bitmapString;
        float price = Float.parseFloat(editTextFoodItemPrice.getText().toString());

        if (!name.isEmpty() && !restaurant.isEmpty() && !mainIngredients.isEmpty() && price > 0) {
            FoodItem foodItem1 = new FoodItem(foodItemId, name, restaurant, mainIngredients, price, image);
            dbHelper.insertFoodItem(foodItem1);
            Toast.makeText(getActivity(), "Food item updated successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextFoodItemDishName.setText("");
        editTextFoodItemMainINgredients.setText("");
        editTextFoodItemRestaurant.setText("");
        editTextFoodItemPrice.setText("");
    }
}