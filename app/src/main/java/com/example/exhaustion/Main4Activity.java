package com.example.exhaustion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;


public class Main4Activity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main4);

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

    }
}
