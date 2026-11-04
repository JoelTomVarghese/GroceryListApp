package com.example.grocerylistapp;

import android.os.Bundle;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etItem;
    Button btnAdd;
    LinearLayout itemContainer;
    GroceryDBHelper dbHelper;

    LinearLayout pendingContainer;
    LinearLayout purchasedContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etItem = findViewById(R.id.etItem);
        btnAdd = findViewById(R.id.btnAdd);
        itemContainer = findViewById(R.id.itemContainer);

        dbHelper = new GroceryDBHelper(this);

        loadItems();

        btnAdd.setOnClickListener(v -> {
            String name = etItem.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter an item",
                        Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = dbHelper.addItem(name);

                if (inserted) {
                    etItem.setText("");
                    loadItems();

                    Toast.makeText(this, "Item added successfully",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add item",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadItems() {

        itemContainer.removeAllViews();

        // Create sections inside the existing XML container
        TextView pendingTitle = new TextView(this);
        pendingTitle.setText("My Items");
        pendingTitle.setTextSize(20);
        pendingTitle.setTextColor(Color.BLACK);
        pendingTitle.setPadding(8, 12, 8, 8);
        itemContainer.addView(pendingTitle);

        pendingContainer = new LinearLayout(this);
        pendingContainer.setOrientation(LinearLayout.VERTICAL);
        itemContainer.addView(pendingContainer);

        TextView purchasedTitle = new TextView(this);
        purchasedTitle.setText("Purchased Items");
        purchasedTitle.setTextSize(20);
        purchasedTitle.setTextColor(Color.rgb(46, 125, 50));
        purchasedTitle.setPadding(8, 20, 8, 8);
        itemContainer.addView(purchasedTitle);

        purchasedContainer = new LinearLayout(this);
        purchasedContainer.setOrientation(LinearLayout.VERTICAL);
        itemContainer.addView(purchasedContainer);

        Cursor cursor = dbHelper.getAllItems();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int purchased = cursor.getInt(2);

                addItemRow(id, name, purchased);

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void addItemRow(int id, String name, int purchased) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(8, 4, 8, 4);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(name);
        checkBox.setTextSize(16);
        checkBox.setChecked(purchased == 1);

        if (purchased == 1) {
            checkBox.setPaintFlags(
                    checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        }

        LinearLayout.LayoutParams checkParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        row.addView(checkBox, checkParams);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.updatePurchased(id, isChecked ? 1 : 0);

            // Refresh so item moves to the correct section
            loadItems();
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(12);
        deleteButton.setTextColor(Color.WHITE);

        deleteButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(211, 47, 47)
                )
        );

        row.addView(deleteButton);

        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteItem(id);
            loadItems();

            Toast.makeText(this, "Item deleted",
                    Toast.LENGTH_SHORT).show();
        });

        if (purchased == 1) {
            purchasedContainer.addView(row);
        } else {
            pendingContainer.addView(row);
        }
    }
}