package com.example.exhaustion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;


public class MenuActivity extends AppCompatActivity {

    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    Switch timerSwitch;
    EditText finishValueField, stepValueField, nameField, startValueField;

    CompoundButton previousRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
        timerSwitch = (Switch) findViewById(R.id.timerSwitch);
        finishValueField = (EditText) findViewById(R.id.finishValueField);
        startValueField = (EditText) findViewById(R.id.startValueField);
        stepValueField = (EditText) findViewById(R.id.stepValueField);
        nameField = (EditText) findViewById(R.id.nameField);

        startValueField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
        finishValueField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
        stepValueField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});

        previousRB = radioButton1;

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView)
                {
                    previousRB.setChecked(false);
                    previousRB = buttonView;
                }
            }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView)
                {
                    previousRB.setChecked(false);
                    previousRB = (RadioButton) buttonView;
                }
            }
        });
        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView)
                {
                    previousRB.setChecked(false);
                    previousRB = (RadioButton) buttonView;
                }
            }
        });
        radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView)
                {
                    previousRB.setChecked(false);
                    previousRB = (RadioButton) buttonView;
                }
            }
        });

        // TIMEPICKERDIALOG
    }
}
