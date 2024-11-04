package com.example.menuw.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;

import com.example.menuw.R;
import com.example.menuw.adapters.ExpandableListAdapter;
import com.example.menuw.data.Restaurant;
import com.example.menuw.utilities.DBHelper;

import java.util.ArrayList;
import java.util.List;


public class MenusFragment extends Fragment {

    private EditText searchBar;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private DBHelper dbHelper;

    public MenusFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menus, container, false);
        searchBar = view.findViewById(R.id.search_bar);
        expandableListView = view.findViewById(R.id.expandable_list_container);

        List<Restaurant> restaurants = dbHelper.getAllRestaurants();
        adapter = new ExpandableListAdapter(getContext(), restaurants);
        expandableListView.setAdapter(adapter);

        dbHelper = DBHelper.getInstance(getContext());

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                List<Restaurant> restaurants = dbHelper.getAllRestaurants();
                adapter = new ExpandableListAdapter(getContext(), restaurants);
                expandableListView.setAdapter(adapter);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Restaurant> restaurants = dbHelper.getAllRestaurants();
                List<Restaurant> filteredRestaurants = new ArrayList<>();
                for (Restaurant restaurant : restaurants) {
                    if (s.toString().toLowerCase().contains(restaurant.getName().toLowerCase())) {
                        filteredRestaurants.add(restaurant);
                    }
                }
                adapter = new ExpandableListAdapter(getContext(), filteredRestaurants);
                expandableListView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }
}