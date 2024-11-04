package com.example.menuw.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuw.R;
import com.example.menuw.adapters.OrderHistoryRecyclerViewAdapter;
import com.example.menuw.data.Order;
import com.example.menuw.utilities.AppConstants;
import com.example.menuw.utilities.DBHelper;

import java.util.List;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderHistoryRecyclerViewAdapter adapter;
    private DBHelper dbHelper;
    private List<Order> orderList;

    public OrderHistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerView = view.findViewById(R.id.order_history_recycler_view);
        dbHelper = DBHelper.getInstance(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(AppConstants.USER_NAME, "");
        orderList = dbHelper.getConfirmedOrders(username);
        if (!orderList.isEmpty()) {
            adapter = new OrderHistoryRecyclerViewAdapter(orderList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}