package com.example.menuw.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.menuw.data.Restaurant;
import com.example.menuw.utilities.DBHelper;
import com.example.menuw.utilities.ImageConverter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class RestaurantFragment extends Fragment {


    private RadioGroup radioGroup;
    private RadioButton radioButtonCreate, radioButtonUpdate;
    private AutoCompleteTextView editTextRestaurantName;
    private EditText editTextRestaurantType, editTextRestaurantLocation, editTextRestaurantMinOrder;
    private Button btnSaveRestaurant;
    private DBHelper dbHelper;
    private Restaurant restaurant;
    private int restaurantId;
    private ActivityResultLauncher<Intent> cameraResultLauncher;
    private ImageView imageView;
    private String bitmapString = "";

    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        radioGroup = view.findViewById(R.id.radio_group_restaurant_frag);
        radioButtonCreate = view.findViewById(R.id.radio_button_create_restaurant_frag);
        radioButtonUpdate = view.findViewById(R.id.radio_button_update_restaurant_frag);
        editTextRestaurantName = view.findViewById(R.id.autoCompleteRestaurantName);
        editTextRestaurantType = view.findViewById(R.id.editTextRestaurantStyle);
        editTextRestaurantLocation = view.findViewById(R.id.editTextRestaurantLocation);
        editTextRestaurantMinOrder = view.findViewById(R.id.editTextRestaurantMinOrder);
        btnSaveRestaurant = view.findViewById(R.id.btnSaveRestaurant);
        imageView = view.findViewById(R.id.restaurant_camera_iv);

        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (getActivity() != null) {
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
                if (checkedId == R.id.radio_button_create_restaurant_frag) {
                    editTextRestaurantName.setEnabled(true);
                    editTextRestaurantType.setEnabled(true);
                    editTextRestaurantLocation.setEnabled(true);
                    editTextRestaurantMinOrder.setEnabled(true);
                    btnSaveRestaurant.setText("Save");
                } else if (checkedId == R.id.radio_button_update_restaurant_frag) {
                    editTextRestaurantName.setEnabled(true);
                    editTextRestaurantType.setEnabled(true);
                    editTextRestaurantLocation.setEnabled(true);
                    editTextRestaurantMinOrder.setEnabled(true);
                    btnSaveRestaurant.setText("Update");
                    autoCompleteRestaurantValues();
                }
            }
        });

        editTextRestaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButtonUpdate.isChecked()) {
                    autoCompleteRestaurantValues();
                }
            }
        });

        btnSaveRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButtonCreate.isChecked()) {
                    createRestaurant();
                } else if (radioButtonUpdate.isChecked()) {
                    updateRestaurant();
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
        List<Restaurant> restaurants = dbHelper.getAllRestaurants();

        // Create autocomplete adapter
        ArrayAdapter<String> adapterRestaurant = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, restaurants.stream().map(Restaurant::getName).collect(Collectors.toList()));
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

        editTextRestaurantName.setThreshold(1);
        editTextRestaurantName.setAdapter(adapterRestaurant);

// Fetch restaurant data from database for update
        String selectedRestaurantName = editTextRestaurantName.getText().toString();
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getName().equals(selectedRestaurantName)) {
                editTextRestaurantType.setText(restaurant.getStyle());
                editTextRestaurantLocation.setText(restaurant.getLocation());
                editTextRestaurantMinOrder.setText(String.valueOf(restaurant.getMinOrder()));
                bitmapString = restaurant.getImage();
                Bitmap bitmap = ImageConverter.convertBase64ToBitmap(bitmapString);
                imageView.setImageBitmap(bitmap);
                restaurantId = restaurant.getId();
                break;
            }
        }
    }

    private void createRestaurant() {
        String name = editTextRestaurantName.getText().toString();
        String type = editTextRestaurantType.getText().toString();
        String location = editTextRestaurantLocation.getText().toString();

        String image = bitmapString;
        float minOrder = Float.parseFloat(editTextRestaurantMinOrder.getText().toString());

        if (!name.isEmpty() && !type.isEmpty() && !location.isEmpty() && minOrder > 0) {
            Restaurant restaurant = new Restaurant(0, name, type, location, minOrder, image);
            dbHelper.insertRestaurant(restaurant);
            Toast.makeText(getActivity(), "Restaurant created successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRestaurant() {
        String name = editTextRestaurantName.getText().toString();
        String type = editTextRestaurantType.getText().toString();
        String location = editTextRestaurantLocation.getText().toString();
        String image = bitmapString; //TODO add image
        int minOrder = Integer.parseInt(editTextRestaurantMinOrder.getText().toString());

        if (!name.isEmpty() && !type.isEmpty() && !location.isEmpty() && minOrder > 0) {
            Restaurant restaurant = new Restaurant(restaurantId, name, type, location, minOrder, image);
            dbHelper.updateRestaurant(restaurant);
            Toast.makeText(getActivity(), "Restaurant updated successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextRestaurantName.setText("");
        editTextRestaurantType.setText("");
        editTextRestaurantLocation.setText("");
        editTextRestaurantMinOrder.setText("");
    }
}