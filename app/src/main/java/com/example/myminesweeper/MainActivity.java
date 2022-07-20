package com.example.myminesweeper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myminesweeper.Preferences.MyPreferences;
import com.example.myminesweeper.ResultDatabase.AddResultDialog;
import com.example.myminesweeper.ResultDatabase.DBHelper;
import com.example.myminesweeper.ResultDatabase.ResultFragment;

import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private Minefield minefield;
    private DBHelper dbHelper;
    private TextView minesC, timerT;
    private Button startStop, settings;
    private MyTimerTask myTimerTask;
    private MyAdapter adapter;
    private GridView gvMinesfield;
    private SharedPreferences sp;
    private MyReceiver receiver;
    private String gameLevel;
    private int prefWidth, prefHeight, gvSpacing;
    private double prefMinesPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gvMinesfield = (GridView) findViewById(R.id.gvMineField);
        minesC = (TextView) findViewById(R.id.tvMineCount);
        timerT = (TextView) findViewById(R.id.tvTimer);
        startStop = (Button) findViewById(R.id.btnStart);
        settings = (Button) findViewById(R.id.btnSettings);
        dbHelper = new DBHelper(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        gvSpacing = Integer.parseInt(sp.getString("spacing", "" + DefMinefieldSettings.SPACING));
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyPreferences.PREF_RESEIVER_ACTION);
        registerReceiver(receiver, intentFilter);
        checkSavedInstance();
        adapter = new MyAdapter(this, R.layout.my_point, minefield, gvSpacing);
        gvMinesfield.setAdapter(adapter);
        adjustGridView();
    }

    private void checkSavedInstance() {
        if (getLastCustomNonConfigurationInstance() == null) {
            switch (sp.getString("listSetting", DefMinefieldSettings.LEVEL_EASY)) {
                case DefMinefieldSettings.LEVEL_EASY:
                    prefWidth = DefMinefieldSettings.Easy.WIDTH;
                    prefHeight = DefMinefieldSettings.Easy.HEIGHT;
                    prefMinesPercent = DefMinefieldSettings.Easy.MINES_PERCENT;
                    gameLevel = DefMinefieldSettings.LEVEL_EASY;
                    break;
                case DefMinefieldSettings.LEVEL_MEDIUM:
                    prefWidth = DefMinefieldSettings.Medium.WIDTH;
                    prefHeight = DefMinefieldSettings.Medium.HEIGHT;
                    prefMinesPercent = DefMinefieldSettings.Medium.MINES_PERCENT;
                    gameLevel = DefMinefieldSettings.LEVEL_MEDIUM;
                    break;
                case DefMinefieldSettings.LEVEL_HARD:
                    prefWidth = DefMinefieldSettings.Hard.WIDTH;
                    prefHeight = DefMinefieldSettings.Hard.HEIGHT;
                    prefMinesPercent = DefMinefieldSettings.Hard.MINES_PERCENT;
                    gameLevel = DefMinefieldSettings.LEVEL_HARD;
                    break;
                case DefMinefieldSettings.LEVEL_CUSTOM:
                    prefWidth = Integer.parseInt(sp.getString("mWidth", String.valueOf(DefMinefieldSettings.Default.WIDTH)));
                    prefHeight = Integer.parseInt(sp.getString("mHeight", String.valueOf(DefMinefieldSettings.Default.HEIGHT)));
                    prefMinesPercent = Integer.parseInt(sp.getString("mMinesCount", String.valueOf(DefMinefieldSettings.Default.MINES_PERCENT)));
                    gameLevel = DefMinefieldSettings.LEVEL_CUSTOM + "(" + "w: " + prefWidth + ", h: " + prefHeight + ", per: " + prefMinesPercent + ")";
                    break;
            }
            minefield = new Minefield(MainActivity.this, prefWidth, prefHeight, prefMinesPercent * 0.01);
            minefield.fillEmptyData();
            minefield.fillMinefield();
        } else {
            minefield = (Minefield) getLastCustomNonConfigurationInstance();
            minesC.setText("Mines: " + minefield.getMinesOnScreen());
        }
    }

    public void adjustGridView() {
        gvMinesfield.setNumColumns(minefield.getWidth());
        gvMinesfield.setHorizontalSpacing(gvSpacing);
        gvMinesfield.setVerticalSpacing(gvSpacing);
        gvMinesfield.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!view.isEnabled()) return;
                if (minefield.getPoints()[position].getData().equals("*")) {
                    for (int i = 0; i < minefield.getPoints().length; i++) {
                        if (minefield.getPoints()[i].getData().equals("*") &&
                                !minefield.getPoints()[i].isEnabledPoint()) {
                            minefield.getPoints()[i].setPointGoodCheckedMines();
                            continue;
                        }
                        if (minefield.getPoints()[i].getData().equals("*") &&
                                minefield.getPoints()[i].getCurrentState() != getDrawable(R.drawable.rect_keepclicked)) {
                            minefield.getPoints()[i].setPointWrongCheckedMines();
                            continue;
                        }
                        minefield.getPoints()[i].setPointClicked();
                    }
                    minefield.getPoints()[position].setPointLost();
                    adapter.notifyDataSetChanged();
                    minefield.setMinesOnScreen(0);
                    minesC.setText("Mines: " + minefield.getMinesOnScreen());
                    Toast.makeText(MainActivity.this, "Unfortunately you lost, try again!", Toast.LENGTH_LONG).show();
                    startStop.setText("Start");
                    settings.setEnabled(true);
                    myTimerTask.cancel(true);
                    return;
                }

                Stack<Integer> inList = new Stack<>();
                HashMap<Integer, Integer> outList = new HashMap<>();
                inList.push(position);
                if (minefield.getPoints()[position].getData().equals("")) {
                    int mainCursor;
                    do {
                        mainCursor = inList.pop();
                        if (!outList.containsValue(mainCursor)) {
                            minefield.getPoints()[mainCursor].setPointClicked();
                            outList.put(mainCursor, mainCursor);
                        }
                        int cursor = mainCursor - 1;
                        if (cursor >= 0 && (cursor + 1) % minefield.getWidth() != 0) {
                            defineEmptyPoints(cursor, inList, outList);
                        }
                        cursor = mainCursor - minefield.getWidth() - 1;
                        if (cursor >= 0 && (cursor + 1) % minefield.getWidth() != 0) {
                            defineEmptyPoints(cursor, inList, outList);
                        }
                        cursor = mainCursor - minefield.getWidth();
                        if (cursor >= 0) {
                            defineEmptyPoints(cursor, inList, outList);
                        }
                        cursor = mainCursor - minefield.getWidth() + 1;
                        if (cursor >= 0 && cursor % minefield.getWidth() != 0) {
                            defineEmptyPoints(cursor, inList, outList);
                        }
                        cursor = mainCursor + 1;
                        if (cursor < minefield.getPointCount() && cursor % minefield.getWidth() != 0) {
                            defineEmptyPoints(cursor, inList, outList);
                        }
                        cursor = mainCursor + minefield.getWidth() + 1;
                        if (cursor < minefield.getPointCount() && cursor % minefield.getWidth() != 0) {
                            defineEmptyPoints(cursor, inList, outList);
                        }
                        cursor = mainCursor + minefield.getWidth();
                        if (cursor < minefield.getPointCount()) {
                            defineEmptyPoints(cursor, inList, outList);
                        }
                        cursor = mainCursor + minefield.getWidth() - 1;
                        if (cursor < minefield.getPointCount() && (cursor + 1) % minefield.getWidth() != 0) {
                            defineEmptyPoints(cursor, inList, outList);
                        }
                        adapter.notifyDataSetChanged();
                    } while (!inList.isEmpty());
                    minefield.setPointsFinishedCount(minefield.getPointsFinishedCount() - 1);
                    adapter.notifyDataSetChanged();
                }
                if (minefield.getPointsFinishedCount() == (minefield.getPointCount() - minefield.getMinesCount() - 1)) {
                    for (int i = 0; i < minefield.getPoints().length; i++) {
                        minefield.getPoints()[position].setPointClicked();
                        if (minefield.getPoints()[i].getData().equals("*")) {
                            minefield.getPoints()[i].setPointGoodCheckedMines();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    minefield.setMinesOnScreen(0);
                    minesC.setText("Mines: " + minefield.getMinesOnScreen());
                    Toast.makeText(MainActivity.this, "Congratulations! You deactivate all the mines.", Toast.LENGTH_LONG).show();
                    startStop.setText("Start");
                    settings.setEnabled(true);
                    AddResultDialog resultDialog = AddResultDialog.newInstance(minefield.getPlayTime(), gameLevel);
                    myTimerTask.cancel(true);
                    resultDialog.show(getSupportFragmentManager(), "");
                    return;
                }
                minefield.getPoints()[position].setPointClicked();
                adapter.notifyDataSetChanged();
            }
        });

        gvMinesfield.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (minefield.getPoints()[position].isFinish()) {
                    if (minefield.getPoints()[position].isEnabledPoint()) {
                        minefield.getPoints()[position].setPointLongClicked();
                        minefield.setMinesOnScreen(minefield.getMinesOnScreen() - 1);
                        minesC.setText("Mines: " + minefield.getMinesOnScreen());
                        adapter.notifyDataSetChanged();

                    } else if (!minefield.getPoints()[position].isEnabledPoint()) {
                        minefield.getPoints()[position].setCancelLongClicked();
                        minefield.setMinesOnScreen(minefield.getMinesOnScreen() + 1);
                        minesC.setText("Mines: " + minefield.getMinesOnScreen());
                        adapter.notifyDataSetChanged();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void defineEmptyPoints(int cursor, Stack<Integer> inList, HashMap<Integer, Integer> outList) {
        if (minefield.getData()[cursor].equals("")) {
            if (!outList.containsValue(cursor) && !inList.contains(cursor)) {
                if (minefield.getPoints()[cursor].isFinish()) {
                    inList.push(cursor);
                }
            }
        }
        if (!minefield.getData()[cursor].equals("")) {
            if (!outList.containsValue(cursor)) {
                outList.put(cursor, cursor);
                if (minefield.getPoints()[cursor].isFinish()) {
                    minefield.getPoints()[cursor].setPointClicked();
                }
            }
        }
    }

    public void onStartStop(View view) {
        switch (startStop.getText().toString()) {
            case "Start":
                minefield.fillEmptyData();
                minefield.fillMinefield();
                setPointsStart();
                minesC.setText("Mines: " + minefield.getMinesOnScreen());
                startStop.setText("Stop");
                settings.setEnabled(false);
                minefield.setMinesOnScreen(minefield.getMinesCount());
                adapter.notifyDataSetChanged();
                myTimerTask = new MyTimerTask();
                myTimerTask.execute();
                break;
            case "Stop":
                myTimerTask.cancel(true);
                minesC.setText("Mines: 0");
                timerT.setText("Time: 0:00");
                startStop.setText("Start");
                settings.setEnabled(true);
                setPointsStop();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    public void onShowRecords(View view) {
        ResultFragment dialog = new ResultFragment();
        dialog.show(getSupportFragmentManager(), "");
    }

    public void onShowSettings(View view) {
        Intent intent = new Intent(MainActivity.this, MyPreferences.class);
        startActivity(intent);
    }

    private void setPointsStart() {
        for (int i = 0; i < minefield.getPoints().length; i++) {
            minefield.getPoints()[i].setPointStart();
        }
    }

    private void setPointsStop() {
        for (int i = 0; i < minefield.getPoints().length; i++) {
            minefield.getPoints()[i].setPointStop();
        }
    }

    @Nullable
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return minefield;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        unregisterReceiver(receiver);
    }

    private class MyTimerTask extends AsyncTask<Void, String, Void> {
        private int min = 0;
        private int sec = 0;
        private String res = "";

        @Override
        protected Void doInBackground(Void... voids) {
            do {
                res = min + ":" + sec;
                if (res.length() < 4) res = min + ":0" + sec;
                minefield.setPlayTime(res);
                publishProgress(res);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (sec == 59) {
                    sec = -1;
                    min++;
                }
                if (min == 59 && sec == 59) {
                    min = 0;
                    sec = 0;
                    cancel(true);
                }
                sec++;

            } while (!isCancelled());
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            timerT.setText("Time: " + values[0]);
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        private boolean changed = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(MyPreferences.PREF_SPACING_KEY)) {
                changed = true;
                gvSpacing = Integer.parseInt(sp.getString("spacing", "1"));
                gvMinesfield.setHorizontalSpacing(gvSpacing);
                gvMinesfield.setVerticalSpacing(gvSpacing);
            }
            if (intent.hasExtra(MyPreferences.PREF_WEIGHT_KEY)) {
                changed = true;
                switch (sp.getString("listSetting", "Easy")) {
                    case "Easy":
                        minefield.setWidth(DefMinefieldSettings.Easy.WIDTH);
                        minefield.setHeight(DefMinefieldSettings.Easy.HEIGHT);
                        minefield.setKoefMines(DefMinefieldSettings.Easy.MINES_PERCENT * 0.01);
                        gvMinesfield.setNumColumns(DefMinefieldSettings.Easy.WIDTH);
                        break;
                    case "Medium":
                        minefield.setWidth(DefMinefieldSettings.Medium.WIDTH);
                        minefield.setHeight(DefMinefieldSettings.Medium.HEIGHT);
                        minefield.setKoefMines(DefMinefieldSettings.Medium.MINES_PERCENT * 0.01);
                        gvMinesfield.setNumColumns(DefMinefieldSettings.Medium.WIDTH);
                        break;
                    case "Hard":
                        minefield.setWidth(DefMinefieldSettings.Hard.WIDTH);
                        minefield.setHeight(DefMinefieldSettings.Hard.HEIGHT);
                        minefield.setKoefMines(DefMinefieldSettings.Hard.MINES_PERCENT * 0.01);
                        gvMinesfield.setNumColumns(DefMinefieldSettings.Hard.WIDTH);
                        break;
                    case "Custom":
                        minefield.setWidth(Integer.parseInt(sp.getString("mWidth", "8")));
                        minefield.setHeight(Integer.parseInt(sp.getString("mHeight", "13")));
                        minefield.setKoefMines(Integer.parseInt(sp.getString("mMinesCount", "17")) * 0.01);
                        gvMinesfield.setNumColumns(Integer.parseInt(sp.getString("mWidth", "8")));
                        break;
                }
            }
            if (intent.hasExtra(MyPreferences.PREF_WIDTH_KEY)) {
                changed = true;
                minefield.setWidth(Integer.parseInt(sp.getString("mWidth", "8")));
                gvMinesfield.setNumColumns(Integer.parseInt(sp.getString("mWidth", "8")));
            }
            if (intent.hasExtra(MyPreferences.PREF_HEIGHT_KEY)) {
                changed = true;
                minefield.setHeight(Integer.parseInt(sp.getString("mHeight", "13")));
            }
            if (intent.hasExtra(MyPreferences.PREF_MINES_PERCENT_KEY)) {
                changed = true;
                minefield.setKoefMines(Integer.parseInt(sp.getString("mMinesCount", "17")) * 0.01);
            }

            if (intent.hasExtra(MyPreferences.PREF_END_SETTINGS_KEY)) {
                if (changed) {
                    minefield.generateNewMinefield();
                    minesC.setText("Mines: 0");
                    timerT.setText("Time: 0:00");
                    adapter.notifyDataSetChanged();
                    changed = false;
                }
            }
        }
    }
}

