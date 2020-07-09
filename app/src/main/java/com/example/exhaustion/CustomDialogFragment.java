package com.example.exhaustion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

public class CustomDialogFragment extends DialogFragment {

    private NumberPicker numberPickerHours, numberPickerMinutes, numberPickerSeconds;
    private CustomFragmentDialogListener listener;
    private MediaPlayer clickSound;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.timer_dialog, null);

        builder.setView(view);
        builder.setTitle(" ");
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hours = numberPickerHours.getValue();
                int minutes = numberPickerMinutes.getValue();
                int seconds = numberPickerSeconds.getValue();
                listener.getTime(hours, minutes, seconds);
            }
        });

        clickSound = MediaPlayer.create(getContext(), R.raw.click);

        numberPickerHours = view.findViewById(R.id.numberPickerHours);
        numberPickerMinutes = view.findViewById(R.id.numberPickerMinutes);
        numberPickerSeconds = view.findViewById(R.id.numberPickerSeconds);
        numberPickerHours.setMinValue(0);
        numberPickerHours.setMaxValue(23);
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(59);
        numberPickerSeconds.setMinValue(0);
        numberPickerSeconds.setMaxValue(59);

        numberPickerHours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                clickSound.start();
            }
        });
        numberPickerMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                clickSound.start();
            }
        });
        numberPickerSeconds.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                clickSound.start();
            }
        });


        numberPickerHours.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        numberPickerMinutes.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        numberPickerSeconds.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try
        {
            listener = (CustomFragmentDialogListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement CustomFragmentDialogListener");
        }
    }

    public interface CustomFragmentDialogListener
    {
        void getTime(int hours, int minutes, int seconds);
    }
}
