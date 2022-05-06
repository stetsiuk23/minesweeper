package com.example.myminesweeper.ResultDatabase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myminesweeper.R;

import java.security.acl.LastOwnerException;
import java.util.List;


public class ResultFragment extends DialogFragment {
    private View title;

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    private ListView listView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        title = getLayoutInflater().inflate(R.layout.dialog_title_item_results, null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Підготовлює базу даних
        prepareDatabase();
        //Створює список і заповняє його днами результатів
        prepareListResults();
        //Створює діалог з результатами
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
    //Підготовка бази даних
    private void prepareDatabase(){
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.TABLE_NAME, null, null, null, null, null, DBHelper.TABLE_COLUMN_RESULT_SECONDS);
    }
    //Підготовка списку
    private void prepareListResults(){
        String[] from = new String[]{DBHelper.TABLE_COLUMN_NAME, DBHelper.TABLE_COLUMN_RESULT, DBHelper.TABLE_COLUMN_LEVEL};
        int[] to = new int[]{R.id.itemName, R.id.itemResult, R.id.itemLevel};
        final MySimpleCursorAdapter adapter = new MySimpleCursorAdapter(getContext(), R.layout.cursor_item, cursor, from, to);
        listView = new ListView(getContext());
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.itemName);
                long vid = adapter.getItemId(position);
                database.delete(DBHelper.TABLE_NAME, DBHelper.TABLE_COLUMN_ID+" = "+vid, null);
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
