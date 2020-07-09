package com.example.exhaustion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myLogs";

    Button counterButton;
    Toolbar toolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.reset:
                counterButton.setText("0");
                break;

            case R.id.info:
                // получить информацию о счетчике (наверно в новом лэйауте)
                break;
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.activity_menu, null);
                EditText nameField = view.findViewById(R.id.nameField);
                nameField.setText("");
                finish();
            }
        });

        counterButton = (Button) findViewById(R.id.counterButton);

        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dec = Integer.parseInt(counterButton.getText().toString()) + 1;
                counterButton.setText(String.valueOf(dec));
            }
        });

        counterButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int dec = Integer.parseInt(counterButton.getText().toString()) - 1;
                counterButton.setText(String.valueOf(dec));
                return true;
            }
        });

        String name = getIntent().getStringExtra("NAME");
        String startValue = getIntent().getStringExtra("START_VALUE");
        String finishValue = getIntent().getStringExtra("FINISH_VALUE");
        String stepValue = getIntent().getStringExtra("STEP_VALUE");
        String isTimer = getIntent().getStringExtra("TIMER");
        String isStopwatch = getIntent().getStringExtra("STOPWATCH");
        String timerHours = getIntent().getStringExtra("TIMER_VALUE_HOURS");
        String timerMinutes = getIntent().getStringExtra("TIMER_VALUE_MINUTES");
        String timerSeconds = getIntent().getStringExtra("TIMER_VALUE_SECONDS");

//        Log.d(TAG, "NAME = " + name);
//        Log.d(TAG, "START = " + startValue);
//        Log.d(TAG, "FINISH = " + finishValue);
//        Log.d(TAG, "STEP = " + stepValue);
//        Log.d(TAG, "TIMER = " + isTimer);
//        Log.d(TAG, "STOPWATCH = " + isStopwatch);
//        Log.d(TAG, "HOURS = " + timerHours);
//        Log.d(TAG, "MINUTES = " + timerMinutes);
//        Log.d(TAG, "SECONDS = " + timerSeconds);
    }
}
