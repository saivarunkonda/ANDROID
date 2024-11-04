package com.example.menuw.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuw.R;
import com.example.menuw.adapters.OrdersRecylerViewAdapter;
import com.example.menuw.data.Order;
import com.example.menuw.utilities.AppConstants;
import com.example.menuw.utilities.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersRecylerViewAdapter adapter;
    private List<Order> orders;
    private DBHelper dbHelper;
    private Button btnConfirm;
    private TextView txtTotal;

    public OrdersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_orders);
        btnConfirm = view.findViewById(R.id.place_order_button);
        txtTotal = view.findViewById(R.id.total_price_tv);
        dbHelper = DBHelper.getInstance(getActivity());
        orders = new ArrayList<>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConstants.SHARED_PREFS, getActivity().MODE_PRIVATE);
        String username = sharedPreferences.getString(AppConstants.USER_NAME, "");
        orders = dbHelper.getOrders(username);
        float total = 0;
        for (Order order : orders) {
            total += order.getPrice();
        }
        txtTotal.setText("Total price: " + total);
        if (orders == null) {
            orders = new ArrayList<>();
        }
        adapter = new OrdersRecylerViewAdapter(getActivity(), orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmOrder(username);
            }
        });
        return view;
    }

    private void confirmOrder(String username) {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Confirm Order");
        alertDialog.setMessage("Are you sure you want to confirm this order?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
            dbHelper.createConfirmedOrder(username, (ArrayList<Order>) orders);
            orders.clear();
            adapter = new OrdersRecylerViewAdapter(getActivity(), new ArrayList<>());
            recyclerView.setAdapter(adapter);
            Toast.makeText(getActivity(), "Order confirmed!", Toast.LENGTH_SHORT).show();
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, which) -> {
            Toast.makeText(getActivity(), "Order not confirmed!", Toast.LENGTH_SHORT).show();
        });
    }
}