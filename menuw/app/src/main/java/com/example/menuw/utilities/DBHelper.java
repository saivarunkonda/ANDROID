package com.example.menuw.utilities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.core.util.Pair;

import com.example.menuw.data.FoodItem;
import com.example.menuw.data.Order;
import com.example.menuw.data.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;
    private static final String DATABASE_NAME = "menuw.db";
    private static final int DATABASE_VERSION = 1;

    // Orders table
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_FOODITEM = "fooditem";
    private static final String COLUMN_RESTAURANT = "restaurant";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_UNITS = "units";
    private static final String COLUMN_DATE = "date";

    // Restaurant table
    private static final String TABLE_RESTAURANT = "restaurant";
    private static final String COL_RESTAURANT_ID = "id";
    private static final String COL_RESTAURANT_NAME = "name";
    private static final String COL_RESTAURANT_STYLE = "style";
    private static final String COL_RESTAURANT_LOCATION = "location";
    private static final String COL_RESTAURANT_MIN_ORDER = "min_order";
    private static final String COL_RESTAURANT_IMAGE = "image";

    // Food items table
    private static final String TABLE_FOOD_ITEMS = "food_items";
    private static final String COL_FOOD_ID = "id";
    private static final String COL_FOOD_DISH_NAME = "dish_name";
    private static final String COL_FOOD_RESTAURANT = "restaurant";
    private static final String COL_FOOD_MAIN_INGREDIENTS = "main_ingredients";
    private static final String COL_FOOD_PRICE = "price";
    private static final String COL_FOOD_IMAGE = "image";

    // Confirmed Orders table
    private static final String TABLE_CONFIRMED_ORDERS = "confirmed_orders";

    private Context context;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRestaurantTable = "CREATE TABLE " + TABLE_RESTAURANT + " (" +
                COL_RESTAURANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RESTAURANT_NAME + " TEXT NOT NULL, " +
                COL_RESTAURANT_STYLE + " TEXT NOT NULL, " +
                COL_RESTAURANT_LOCATION + " TEXT NOT NULL, " +
                COL_RESTAURANT_MIN_ORDER + " REAL NOT NULL, " +
                COL_RESTAURANT_IMAGE + " TEXT NOT NULL)";
        String createFoodItemsTable = "CREATE TABLE " + TABLE_FOOD_ITEMS + " (" +
                COL_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FOOD_DISH_NAME + " TEXT NOT NULL, " +
                COL_FOOD_RESTAURANT + " TEXT NOT NULL, " +
                COL_FOOD_MAIN_INGREDIENTS + " TEXT NOT NULL, " +
                COL_FOOD_PRICE + " REAL NOT NULL, " +
                COL_FOOD_IMAGE + " TEXT NOT NULL)";
        db.execSQL(createRestaurantTable);
        db.execSQL(createFoodItemsTable);
        db.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT)");
        db.execSQL("INSERT INTO users (username, password) VALUES ('admin', 'admin')");
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_FOODITEM + " TEXT, " +
                COLUMN_RESTAURANT + " TEXT, " +
                COLUMN_PRICE + " INTEGER, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_UNITS + " INTEGER, " +
                COLUMN_DATE + " TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_CONFIRMED_ORDERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                "orders TEXT" +  // store orders as JSON string
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIRMED_ORDERS);
        onCreate(db);
    }

    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = db.insert("users", null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    public int numberOfUsers() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public Pair<String, String> getUserLoginCredentials(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        String password = null;
        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
        }
        cursor.close();
        db.close();
        return password != null ? new Pair<>(username, password) : null;
    }

    public void insertRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_RESTAURANT_NAME, restaurant.getName());
        contentValues.put(COL_RESTAURANT_STYLE, restaurant.getStyle());
        contentValues.put(COL_RESTAURANT_LOCATION, restaurant.getLocation());
        contentValues.put(COL_RESTAURANT_MIN_ORDER, restaurant.getMinOrder());
        contentValues.put(COL_RESTAURANT_IMAGE, restaurant.getImage());
        db.insert(TABLE_RESTAURANT, null, contentValues);
        db.close();
    }

    public Restaurant getRestaurant(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESTAURANT, new String[]{COL_RESTAURANT_ID, COL_RESTAURANT_NAME, COL_RESTAURANT_STYLE, COL_RESTAURANT_LOCATION, COL_RESTAURANT_MIN_ORDER, COL_RESTAURANT_IMAGE}, COL_RESTAURANT_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Restaurant restaurant = null;
        if (cursor.moveToFirst()) {
            restaurant = new Restaurant(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getFloat(4),
                    cursor.getString(5)
            );
        }
        cursor.close();
        db.close();
        return restaurant;
    }

    public void insertFoodItem(FoodItem foodItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FOOD_DISH_NAME, foodItem.getDishName());
        contentValues.put(COL_FOOD_RESTAURANT, foodItem.getRestaurant());
        contentValues.put(COL_FOOD_MAIN_INGREDIENTS, String.join(",", foodItem.getMainIngredients()));
        contentValues.put(COL_FOOD_PRICE, foodItem.getPrice());
        contentValues.put(COL_FOOD_IMAGE, foodItem.getImage());
        db.insert(TABLE_FOOD_ITEMS, null, contentValues);
        db.close();
    }

    public List<FoodItem> getFoodItems(String restaurant) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOOD_ITEMS, new String[]{COL_FOOD_ID, COL_FOOD_DISH_NAME, COL_FOOD_RESTAURANT, COL_FOOD_MAIN_INGREDIENTS, COL_FOOD_PRICE, COL_FOOD_IMAGE}, COL_FOOD_RESTAURANT + " = ?", new String[]{restaurant}, null, null, null);
        List<FoodItem> foodItems = new ArrayList<>();
        while (cursor.moveToNext()) {
            foodItems.add(new FoodItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    Arrays.asList(cursor.getString(3).split(",")),
                    cursor.getFloat(4),
                    cursor.getString(5)
            ));
        }
        cursor.close();
        db.close();
        return foodItems;
    }

    public ArrayList<FoodItem> getAllFoodItems() {
        ArrayList<FoodItem> foodItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOOD_ITEMS, null);
        //Cursor cursor = db.query(TABLE_FOOD_ITEMS, new String[]{COL_FOOD_ID, COL_FOOD_DISH_NAME, COL_FOOD_RESTAURANT, COL_FOOD_MAIN_INGREDIENTS, COL_FOOD_PRICE, COL_FOOD_IMAGE}, COL_FOOD_RESTAURANT + " = ?", new String[]{restaurant}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                foodItems.add(new FoodItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        Arrays.asList(cursor.getString(3).split(",")),
                        cursor.getFloat(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return foodItems;
    }

    public void updateRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_RESTAURANT_NAME, restaurant.getName());
        contentValues.put(COL_RESTAURANT_STYLE, restaurant.getStyle());
        contentValues.put(COL_RESTAURANT_LOCATION, restaurant.getLocation());
        contentValues.put(COL_RESTAURANT_MIN_ORDER, restaurant.getMinOrder());
        contentValues.put(COL_RESTAURANT_IMAGE, restaurant.getImage());
        db.update(TABLE_RESTAURANT, contentValues, COL_RESTAURANT_ID + " = ?", new String[]{String.valueOf(restaurant.getId())});
        db.close();
    }

    public void updateFoodItem(FoodItem foodItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FOOD_DISH_NAME, foodItem.getDishName());
        contentValues.put(COL_FOOD_RESTAURANT, foodItem.getRestaurant());
        contentValues.put(COL_FOOD_MAIN_INGREDIENTS, String.join(",", foodItem.getMainIngredients()));
        contentValues.put(COL_FOOD_PRICE, foodItem.getPrice());
        contentValues.put(COL_FOOD_IMAGE, foodItem.getImage());
        db.update(TABLE_FOOD_ITEMS, contentValues, COL_FOOD_ID + " = ?", new String[]{String.valueOf(foodItem.getId())});
        db.close();
    }

    public void deleteRestaurant(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RESTAURANT, COL_RESTAURANT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteFoodItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_FOOD_ITEMS, COL_FOOD_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getRestaurantCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESTAURANT, new String[]{"COUNT(" + COL_RESTAURANT_ID + ")"}, null, null, null, null, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getFoodItemCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOOD_ITEMS, new String[]{"COUNT(" + COL_FOOD_ID + ")"}, null, null, null, null, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESTAURANT, null);

        if (cursor.moveToFirst()) {
            do {
                Restaurant restaurant = new Restaurant();
                restaurant.setId(cursor.getInt(0));
                restaurant.setName(cursor.getString(1));
                restaurant.setStyle(cursor.getString(2));
                restaurant.setLocation(cursor.getString(3));
                restaurant.setMinOrder(cursor.getFloat(4));
                restaurant.setImage(cursor.getString(5));
                restaurants.add(restaurant);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return restaurants;
    }

    // Orders table operations

    public long createOrder(String username, String fooditem, String restaurant, int price, String image, int units) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_FOODITEM, fooditem);
        contentValues.put(COLUMN_RESTAURANT, restaurant);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_IMAGE, image);
        contentValues.put(COLUMN_UNITS, units);
        contentValues.put(COLUMN_DATE, new Date().toString());
        return db.insert(TABLE_ORDERS, null, contentValues);
    }

    public int updateOrder(int id, String username, String fooditem, String restaurant, int price, String image, int units) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_FOODITEM, fooditem);
        contentValues.put(COLUMN_RESTAURANT, restaurant);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_IMAGE, image);
        contentValues.put(COLUMN_UNITS, units);
        return db.update(TABLE_ORDERS, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteOrder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ORDERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public ArrayList<Order> getOrders(String username) {
        ArrayList<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_DATE + " = ?", new String[]{username, new Date().toString()});
        while (cursor.moveToNext()) {
            orders.add(new Order(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_FOODITEM)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_RESTAURANT)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_UNITS))
            ));
        }
        cursor.close();
        return orders;
    }

    // Confirmed Orders table operations

    public long createConfirmedOrder(String username, ArrayList<Order> orders) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_DATE, new Date().toString());
        contentValues.put("orders", orders.toString()); // store orders as JSON string
        return db.insert(TABLE_CONFIRMED_ORDERS, null, contentValues);
    }

    public int updateConfirmedOrder(int id, String username, ArrayList<Order> orders) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_DATE, new Date().toString());
        contentValues.put("orders", orders.toString()); // store orders as JSON string
        return db.update(TABLE_CONFIRMED_ORDERS, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteConfirmedOrder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CONFIRMED_ORDERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public ArrayList<Order> getConfirmedOrders(String username) {
        ArrayList<Order> confirmedOrders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM confirmed_orders WHERE username = ?", new String[]{username});
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String ordersJson = cursor.getString(cursor.getColumnIndex("orders"));
            // Convert JSON string to ArrayList<Order>
            // You can use Gson or Jackson library for JSON parsing
            // For simplicity, assume ordersJson is a comma-separated string
            String[] orderStrings = ordersJson.split(",");
            for (String orderString : orderStrings) {
                String[] orderData = orderString.split("-");
                confirmedOrders.add(new Order(
                        Integer.parseInt(orderData[0]),
                        orderData[1],
                        orderData[2],
                        Integer.parseInt(orderData[3]),
                        orderData[4],
                        Integer.parseInt(orderData[5])
                ));
            }
        }
        cursor.close();
        return confirmedOrders;
    }
}