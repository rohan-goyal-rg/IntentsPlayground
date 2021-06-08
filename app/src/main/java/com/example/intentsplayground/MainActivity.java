package com.example.intentsplayground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.intentsplayground.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding b;
    int qty = 0;
    private int minValue, maxValue;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialise
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        sharedPreferences = getPreferences(MODE_PRIVATE);

        //event handler
        eventHandler();
        //Receive data
        receiveData();

        //send data back
        sendDataBack();

        saveData(savedInstanceState);

    }

    /**
     * Save data in sharedPreference and savedInstanceState
     *
     * @param savedInstanceState Data save on configuration changes
     */
    private void saveData(Bundle savedInstanceState) {
        //check bundle
        if (savedInstanceState != null) {
            qty = savedInstanceState.getInt(Constants.COUNT_VAlUE);
        } else {
            qty = sharedPreferences.getInt(Constants.COUNT_VAlUE, 0);
        }

        b.qty.setText(qty + "");
    }


    /**
     * Send data back to IntentPlayground Activity
     */
    private void sendDataBack() {
        //click event handler
        b.btnSendBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check quantity
                if (qty >= minValue && qty <= maxValue) {
                    //Send data back
                    Intent replyIntent = new Intent(MainActivity.this, IntentPlaygroundActivity.class);
                    replyIntent.putExtra(Constants.FINAL_DATA, qty);

                    setResult(RESULT_OK, replyIntent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "NOT VALID", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * Receive data from IntentPlayground Activity
     */
    private void receiveData() {

        // check bundle is not null
        if (getIntent().getExtras() == null) {
            sharedPreferences.edit().putInt(Constants.COUNT_VAlUE, 0).apply();
            return;
        }
        minValue = getIntent().getExtras().getInt(Constants.MIN_VALUE, 0);
        maxValue = getIntent().getExtras().getInt(Constants.MAX_VALUE, 0);

        qty = getIntent().getExtras().getInt(Constants.INITIAL_DATA, Integer.MIN_VALUE);


        b.btnSendBack.setVisibility(View.VISIBLE);


        //Edit sharedPreferences
        sharedPreferences.edit().putInt(Constants.COUNT_VAlUE, qty).apply();
    }

    /**
     * Trigger Event handlers to listen the actions
     */
    private void eventHandler() {
        //click event handler on Decrease button
        b.decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });
        //click event handler on Increase
        b.incBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });
    }

    /**
     * To increase the quantity
     */
    private void increaseQuantity() {
        //update Quantity TextView
        b.qty.setText(++qty + "");
    }

    /**
     * To decrease the quantity
     */
    private void decreaseQuantity() {
       // check quantity
        if (qty == 0) {
            Toast.makeText(this, "Quantity is already 0", Toast.LENGTH_SHORT).show();
            return;
        }
        //update Quantity TextView
        b.qty.setText(--qty + "");
    }

    /**
     * Save data
     * @param outState Save data on configuration changes
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.COUNT_VAlUE, qty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //update sharedPreference
        sharedPreferences.edit().
                putInt(Constants.COUNT_VAlUE, qty)
                .apply();
    }
}