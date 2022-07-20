package com.example.myminesweeper.ResultDatabase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myminesweeper.R;

public class ResultFragment extends DialogFragment {
    private View title;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;
    private ListView listView;

    @SuppressLint("InflateParams")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        title = getLayoutInflater().inflate(R.layout.dialog_title_item_results, null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        prepareDatabase();
        prepareListResults();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCustomTitle(title);
        builder.setView(listView);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    private void prepareDatabase() {
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.TABLE_NAME, null, null, null, null, null, DBHelper.TABLE_COLUMN_RESULT_SECONDS);
    }

    private void prepareListResults() {
        String[] from = new String[]{DBHelper.TABLE_COLUMN_NAME, DBHelper.TABLE_COLUMN_RESULT, DBHelper.TABLE_COLUMN_LEVEL};
        int[] to = new int[]{R.id.itemName, R.id.itemResult, R.id.itemLevel};
        final MySimpleCursorAdapter adapter = new MySimpleCursorAdapter(getContext(), R.layout.cursor_item, cursor, from, to);
        listView = new ListView(getContext());
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long vid = adapter.getItemId(position);
                database.delete(DBHelper.TABLE_NAME, DBHelper.TABLE_COLUMN_ID + " = " + vid, null);
                cursor.requery();
                Toast.makeText(getActivity(), "Result was successfully deleted!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        dbHelper.close();
        database.close();
    }
}
