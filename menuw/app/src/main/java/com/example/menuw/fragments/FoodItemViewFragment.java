package com.example.menuw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuw.R;
import com.example.menuw.adapters.FoodItemListRecyclerViewAdapter;
import com.example.menuw.data.FoodItem;
import com.example.menuw.utilities.DBHelper;

import java.util.ArrayList;

public class FoodItemViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodItemListRecyclerViewAdapter adapter;
    private ArrayList<FoodItem> foodItemList = new ArrayList<>();
    private DBHelper dbHelper;

    public FoodItemViewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fooditem_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_fooditem_list);
        dbHelper = DBHelper.getInstance(getActivity());
        foodItemList = dbHelper.getAllFoodItems();
        adapter = new FoodItemListRecyclerViewAdapter(foodItemList, getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}