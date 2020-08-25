package com.example.exhaustion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CounterViewAdapter extends RecyclerView.Adapter<CounterViewAdapter.CounterViewHolder> {

    private Context context;
    private Cursor cursor;
    private onItemListener onItemListener;

    public CounterViewAdapter(Context context, Cursor cursor, onItemListener onItemListener) {
        this.context = context;
        this.cursor = cursor;
        this.onItemListener = onItemListener;
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

    public static boolean isYesterday(Date d) {
        return DateUtils.isToday(d.getTime() + DateUtils.DAY_IN_MILLIS);
    }

    public static boolean isThreeHoursAgo(Date d) {
        return DateUtils.isToday(d.getTime() + DateUtils.HOUR_IN_MILLIS * 3);
    }

    @Override
    public void onBindViewHolder(@NonNull CounterViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        String name = cursor.getString(cursor.getColumnIndex(DataBaseHelper.NAME));
        String time = cursor.getString(cursor.getColumnIndex(DataBaseHelper.TIME_OF_LAST_EDIT));
        int number = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.NUMBER));
        long id = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.KEY_ID));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        String formattedTime = time;
        try {
            Date res = df.parse(time);
            formattedTime = output.format(res);

            if (DateUtils.isToday(res.getTime())) {
                formattedTime = "Сегодня в" + formattedTime.substring(10);
                //formattedTime = DateUtils.getRelativeTimeSpanString(res.getTime());
            } else if (isYesterday(res)) {
                formattedTime = "Вчера в" + formattedTime.substring(10);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.nameField.setText(name);
        holder.editTimeField.setText(formattedTime);
        holder.numberValueField.setText(String.valueOf(number));
        holder.itemView.setTag(name);
    }

    public class CounterViewHolder extends RecyclerView.ViewHolder {

        TextView numberValueField, nameField, editTimeField;

        public CounterViewHolder(@NonNull View itemView) {
            super(itemView);

            nameField = itemView.findViewById(R.id.rv_nameField);
            numberValueField = itemView.findViewById(R.id.rv_numberValue);
            editTimeField = itemView.findViewById(R.id.rv_editTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onClickListener(getAdapterPosition());
                }
            });
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

    public void swapCursorWithoutNotify(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
    }

    public interface onItemListener {
        void onClickListener(int position);
    }
}
