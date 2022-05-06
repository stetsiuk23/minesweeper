package com.example.myminesweeper.ResultDatabase;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.myminesweeper.R;

public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.itemPos);
        textView.setText((position+1)+"");
        return view;
    }
}
