package com.example.myminesweeper.ResultDatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myminesweeper.R;

import java.util.ArrayList;

public class AddResultDialog extends DialogFragment {
    private static final String ARG_TIME = "timeArg";
    private static final String ARG_LEVEL = "levelArg";
    private static final String ARG_TIME_SECONDS = "timeSecondsArg";

    private View title;

    private EditText etName;
    private boolean nameIsWrote = false;
    private String time, name, level;
    private int levelSeconds;

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    //Повертає готовий обєкт Діалогу
    public static AddResultDialog newInstance(String time, String level){
        AddResultDialog dialog = new AddResultDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TIME_SECONDS, getTimeSeconds(time));
        bundle.putString(ARG_TIME, time);
        bundle.putString(ARG_LEVEL, level);
        dialog.setArguments(bundle);
        return dialog;
    }
    //Допоміжний статичний метод для визначення часу з стрічки
    private static int getTimeSeconds(String time){
        String[] levelTime = time.split(":");
        return (Integer.parseInt(levelTime[0])*60)+(Integer.parseInt(levelTime[1]));
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        title = getLayoutInflater().inflate(R.layout.dialog_title_item, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            time = getArguments().getString(ARG_TIME);
            level = getArguments().getString(ARG_LEVEL);
            levelSeconds = getArguments().getInt(ARG_TIME_SECONDS);
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Підготовлюємо базу даних до роботи
        prepareDatabase();
        //Підготовлюємо список вже збережених імен
        prepareSavedNamesList();
        //Створюємо діалогове вікно запиту імені користувача
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCustomTitle(title);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = adapter.getItem(which);
                addDataIntoDatabase(name);
            }
        });
        builder.setView(etName);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("Commit", null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button posButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                posButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        name = etName.getText().toString();
                        if(name.equals("")){
                            nameIsWrote = false;
                            Toast.makeText(getContext(), "Please write your name!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            nameIsWrote = true;
                            addDataIntoDatabase(name);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        return dialog;
    }
    //Підготовлює базу даних ініціалізує всі необхідні обєкти
    private void prepareDatabase(){
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
    }
    //Підготовлює список збережених імен
    private void prepareSavedNamesList(){
        etName = new EditText(getContext());
        etName.setHint("Name");
        arrayList = new ArrayList<>();
        fillArrayNames();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);
    }
    //Допоміжний метод для підготовки списку імен
    private void fillArrayNames(){
        if(cursor.moveToFirst()) {
            do {
                if(!arrayList.contains(cursor.getString(cursor.getColumnIndex(DBHelper.TABLE_COLUMN_NAME)))) {
                    arrayList.add(cursor.getString(cursor.getColumnIndex(DBHelper.TABLE_COLUMN_NAME)));
                }
            } while (cursor.moveToNext());
        }
    }
    //Метод для заповнення даних в базу даних
    private void addDataIntoDatabase(String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TABLE_COLUMN_NAME, name);
        contentValues.put(DBHelper.TABLE_COLUMN_RESULT, time);
        contentValues.put(DBHelper.TABLE_COLUMN_LEVEL, level);
        contentValues.put(DBHelper.TABLE_COLUMN_RESULT_SECONDS, levelSeconds);
        database.insert(DBHelper.TABLE_NAME, null, contentValues);
        contentValues.clear();
        ResultFragment resultFragment = new ResultFragment();
        resultFragment.show(getFragmentManager(), "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        database.close();
    }
}
