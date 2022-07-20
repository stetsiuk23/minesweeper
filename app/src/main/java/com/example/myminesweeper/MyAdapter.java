package com.example.myminesweeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
    private final int spacing;
    private final Minefield minefield;
    private final int item;
    private final LayoutInflater layoutInflater;

    public MyAdapter(Context context, int item, Minefield minefield, int spacing) {
        this.spacing = spacing;
        this.minefield = minefield;
        this.item = item;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return minefield.getPoints().length;
    }

    @Override
    public Object getItem(int position) {
        return minefield.getPoints()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Minefield.Point point = minefield.getPoints()[position];
        viewHolder.myLin.getLayoutParams().height = parent.getHeight() / (minefield.getHeight()) - spacing;
        viewHolder.tvData.setVisibility(point.getDataVisibility() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.tvData.setText(point.getData());
        viewHolder.tvData.setTextColor(point.getPointColor());
        viewHolder.tvData.setTextSize((float) (parent.getHeight() / (minefield.getHeight()) * 0.25));
        viewHolder.myLin.setBackground(point.getCurrentState());
        viewHolder.myLin.setEnabled(point.isEnabledPoint());
        parent.setEnabled(point.isEnabled());
        return convertView;
    }

    private static class ViewHolder {
        final LinearLayout myLin;
        final TextView tvData;

        ViewHolder(View view) {
            myLin = (LinearLayout) view.findViewById(R.id.myLin);
            tvData = (TextView) view.findViewById(R.id.tvData);
        }
    }
}
