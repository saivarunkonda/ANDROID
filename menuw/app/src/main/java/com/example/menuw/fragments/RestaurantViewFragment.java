package com.example.menuw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuw.R;
import com.example.menuw.adapters.RestaurantListRecyclerViewAdapter;
import com.example.menuw.data.Restaurant;
import com.example.menuw.utilities.DBHelper;

import java.util.ArrayList;

public class RestaurantViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private RestaurantListRecyclerViewAdapter adapter;
    private ArrayList<Restaurant> restaurantList = new ArrayList<>();
    private DBHelper dbHelper;

    public RestaurantViewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_restaurant_list);
        dbHelper = DBHelper.getInstance(getActivity());
        if (dbHelper.getRestaurantCount() > 0) {
            restaurantList = (ArrayList<Restaurant>) dbHelper.getAllRestaurants();
        }
        adapter = new RestaurantListRecyclerViewAdapter(restaurantList, getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}