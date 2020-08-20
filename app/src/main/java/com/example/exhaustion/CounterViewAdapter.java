package com.example.exhaustion;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CounterViewAdapter extends RecyclerView.Adapter<CounterViewAdapter.CounterViewHolder> {

    private Context context;
    private Cursor cursor;

    public CounterViewAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public CounterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_item, parent, false);
        return new CounterViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public void onBindViewHolder(@NonNull CounterViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        String name = cursor.getString(cursor.getColumnIndex(DataBaseHelper.NAME));
        String time = cursor.getString(cursor.getColumnIndex(DataBaseHelper.TIME_OF_LAST_EDIT));
        int number = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.NUMBER));

        long id = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.KEY_ID));

        holder.nameField.setText(name);
        holder.editTimeField.setText(time);
        holder.numberValueField.setText(String.valueOf(number));
        holder.itemView.setTag(id);
    }

    public class CounterViewHolder extends RecyclerView.ViewHolder {

        TextView numberValueField, nameField, editTimeField;

        public CounterViewHolder(@NonNull View itemView) {
            super(itemView);

            nameField = itemView.findViewById(R.id.rv_nameField);
            numberValueField = itemView.findViewById(R.id.rv_numberValue);
            editTimeField = itemView.findViewById(R.id.rv_editTime);
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }

        cursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
