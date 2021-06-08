package com.example.intentsplayground;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.intentsplayground.databinding.ActivityIntentPlaygroundBinding;

public class IntentPlaygroundActivity extends AppCompatActivity {
    private static final int REQUEST_COUNT = 100;

    ActivityIntentPlaygroundBinding b;

    int finalCountValue = Integer.MIN_VALUE;
    private static final String FINAL_COUNT_VALUE = "finalCountValue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize binding
        setupLayout();
        //Handel configuration change
        handelConfigurationChanges(savedInstanceState);

        loadSharedPreference();

        //Initialize Explicit Intent
        setupExplicitIntent();

        //Initialize Implicit Intent
        setupImplicitIntent();

        //Send data
        sendDataToMainActivity();


    }

    /**
     *
     * @param savedInstanceState load data from bundle
     */
    private void handelConfigurationChanges(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            finalCountValue = savedInstanceState.getInt(FINAL_COUNT_VALUE);
            if (finalCountValue != Integer.MIN_VALUE) {
                b.finalData.setText("The final count value is " + finalCountValue);
                b.finalData.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Get data from sharedPreference
     */
    private void loadSharedPreference() {
        SharedPreferences sharedPreferences=getPreferences(MODE_PRIVATE);

        //update views
        b.data.setText(sharedPreferences.getString(Constants.DATA,""));
        b.radioGroup.check(sharedPreferences.getInt(Constants.RADIO_BUTTON_CHECK,0));
        b.sendData.setText(sharedPreferences.getString(Constants.EDT_INITIAL_COUNT_VALUE,""));

    }

    /**
     * Send data to MainActivity by using explicit intent
     */
    private void sendDataToMainActivity() {
        //click event
        b.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendData = b.sendData.getText().toString().trim();

                //Check data
                if (sendData.isEmpty()) {
                    b.sendData.setError("Please enter data");
                    return;
                }
                //Put data in bundle
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.INITIAL_DATA, Integer.parseInt(sendData));
                bundle.putInt(Constants.MIN_VALUE, 0);
                bundle.putInt(Constants.MAX_VALUE, 100);

                //Send data by using explicit intent
                Intent intent = new Intent(IntentPlaygroundActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_COUNT);

            }
        });
    }

    /**
     * Initialize Layout
     */
    private void setupLayout() {
        b = ActivityIntentPlaygroundBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setTitle("Intents Playground");
    }


    /**
     * Open MainActivity by using Explicit Intent
     */
    private void setupExplicitIntent() {
        //click event
        b.sendExplicitIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntentPlaygroundActivity.this, MainActivity.class));
            }
        });
    }


    /**
     * Implicit Intent
     */
    private void setupImplicitIntent() {
        b.sendImplicitIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = b.data.getText().toString().trim();

                //Check validation of data
                if (str.isEmpty()) {
                    b.data.setError("Please enter data");
                    return;
                }
                int id = b.radioGroup.getCheckedRadioButtonId();

                if (id == R.id.open_webPage) {
                    //To open web page
                    openWebPage(str);
                } else if (id == R.id.dial_no) {
                    //To open dialer
                    openDialer(str);

                } else if (id == R.id.share_text) {
                    //To share text
                    openShareMenu(str);
                } else {
                    Toast.makeText(IntentPlaygroundActivity.this, "Please Select a option", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * Open all sharing app
     *
     * @param text Share text to any sharing app
     */
    private void openShareMenu(String text) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text);

        startActivity(Intent.createChooser(intent, "Share text via"));
        //Hide the error
        hideError();

    }

    /**
     * Open the dialer
     *
     * @param number number to called
     */
    private void openDialer(String number) {
        //Check number
        if (!number.matches("^\\d{10}$")) {
            b.data.setError("Please enter valid number ");
            return;
        }
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
        hideError();

    }

    /**
     * @param url which is open on Browser
     */
    private void openWebPage(String url) {
        //check url
        if (!url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            b.data.setError("Please enter valid url");
            return;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        hideError();
    }


    /**
     * Used to hide the error showed when Text changes
     */
    private void hideError() {
        b.data.setError(null);
    }

    /**
     * @param requestCode code of the request made
     * @param resultCode  code of the result
     * @param data        data which is coming back in the form of result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COUNT && resultCode == RESULT_OK) {
            finalCountValue = data.getIntExtra(Constants.FINAL_DATA, 0);
            b.finalData.setText("The final count value is " + finalCountValue);
            b.finalData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //save data
        outState.putInt(FINAL_COUNT_VALUE, finalCountValue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //update sharedPreferences
        getPreferences(MODE_PRIVATE).edit()
                .putString(Constants.DATA, b.data.getText().toString().trim())
                .putInt(Constants.RADIO_BUTTON_CHECK, b.radioGroup.getCheckedRadioButtonId())
                .putString(Constants.EDT_INITIAL_COUNT_VALUE, b.sendData.getText().toString().trim()).apply();
    }
}