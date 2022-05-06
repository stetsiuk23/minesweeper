package com.example.myminesweeper.Preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.widget.Toast;

import com.example.myminesweeper.DefMinefieldSettings;
import com.example.myminesweeper.R;

public class MyPreferences extends PreferenceActivity {
    public static final String PREF_RESEIVER_ACTION = "com.example.myminesweeper/prefReceiverCallback";

    //Константи Keys для надсилання і отримання даних Intent
    public static final String PREF_WIDTH_KEY = "pref_width";
    public static final String PREF_HEIGHT_KEY = "pref_height";
    public static final String PREF_MINES_PERCENT_KEY = "pref_mines_percent";
    public static final String PREF_SPACING_KEY = "pref_spacing";
    public static final String PREF_WEIGHT_KEY = "pref_weight";
    public static final String PREF_END_SETTINGS_KEY = "pref_end_settings";

    private ListPreference listPreference;
    private PreferenceCategory category;

    private ListPreference width, height, minesPercent, spacing;

    private Intent intent;
    private int currentWidth, currentHeight, currentMinesCount;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        //Створюємо Intent ддля повернення даних в Receiver
        intent = new Intent(PREF_RESEIVER_ACTION);
        //Знаходимо всі наші Preferences елементи і встановлюємо для них Listeners
        findPrefAndSetListeners();
        //Провіряємо і отримуємо теперішнє значення ListPreference
        checkListPref();
    }

    //Знаходження елементів і встановлення для них Callbacks
    private void findPrefAndSetListeners(){
        width = (ListPreference) findPreference("mWidth");
        height = (ListPreference) findPreference("mHeight");
        minesPercent = (ListPreference) findPreference("mMinesCount");
        spacing = (ListPreference) findPreference("spacing");

        listPreference = (ListPreference) findPreference("listSetting");
        category = (PreferenceCategory) findPreference("customCompl");

        width.setOnPreferenceChangeListener(customCompliclistener);
        height.setOnPreferenceChangeListener(customCompliclistener);
        minesPercent.setOnPreferenceChangeListener(customCompliclistener);
        spacing.setOnPreferenceChangeListener(customCompliclistener);
        listPreference.setOnPreferenceChangeListener(listListener);
    }

    //Провіряє теперішнє значення ListPreference
    private void checkListPref(){
        switch (listPreference.getValue()){
            case "Easy":
                setEasySettings();
                break;
            case "Medium":
                setMediumSettings();
                break;
            case "Hard":
                setHardSettings();
                break;
            case "Custom":
                setCustomSettings();
                break;
        }
        listPreference.setSummary("Weight: "+listPreference.getValue());
        width.setSummary("Width: "+width.getValue());
        height.setSummary("Height: "+height.getValue());
        minesPercent.setSummary("Mines percent: "+minesPercent.getValue());
        spacing.setSummary("Spacing: "+spacing.getValue());
    }

    //Callback виконується при знімі значення ListPreference
    private Preference.OnPreferenceChangeListener listListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            intent = new Intent(PREF_RESEIVER_ACTION);
            switch ((String) newValue){
                case "Easy":
                    intent.putExtra(PREF_WEIGHT_KEY, "Easy");
                    setEasySettings();
                    break;
                case "Medium":
                    intent.putExtra(PREF_WEIGHT_KEY, "Medium");
                    setMediumSettings();
                    break;
                case "Hard":
                    intent.putExtra(PREF_WEIGHT_KEY, "Hard");
                    setHardSettings();
                    break;
                case "Custom":
                    intent.putExtra(PREF_WEIGHT_KEY, "Custom");
                    setCustomSettings();
                    break;
            }
            sendBroadcast(intent);
            return true;
        }
    };

    //Callback виконується при зніні значень Custom
    private Preference.OnPreferenceChangeListener customCompliclistener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            intent = new Intent(PREF_RESEIVER_ACTION);
            switch (preference.getKey()){
                case "mWidth":
                    width.setSummary("Width: "+(String)newValue);
                    intent.putExtra(PREF_WIDTH_KEY, Integer.parseInt((String) newValue));
                    break;
                case "mHeight":
                    height.setSummary("Height: "+(String)newValue);
                    intent.putExtra(PREF_HEIGHT_KEY, Integer.parseInt((String) newValue));
                    break;
                case "mMinesCount":
                    minesPercent.setSummary("Percents of mines: "+(String)newValue);
                    intent.putExtra(PREF_MINES_PERCENT_KEY, Integer.parseInt((String) newValue));
                    break;
                case "spacing":
                    spacing.setSummary("Spacing: "+(String)newValue);
                    intent.putExtra(PREF_SPACING_KEY, Integer.parseInt((String) newValue));
                    break;
            }
            sendBroadcast(intent);
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        intent = new Intent(PREF_RESEIVER_ACTION);
        intent.putExtra(PREF_END_SETTINGS_KEY, "");
        sendBroadcast(intent);
        //Провіряємо чи встановлене значення list. Якщо так то Встановлюємо ці значення інакше значення залишаються незмінні
        /*notifyCustomData();
        //Надсилаємо готові значення в Reciever
        putDataSendResult();*/
    }

    //Провіряє якщо встановлені настройки Custom то вкстановлює всі вибрані Custom значення
    /*private void notifyCustomData(){
        if(listPreference.getValue().equals("Custom")){
            currentWidth = Integer.parseInt(width.getValue());
            currentHeight = Integer.parseInt(height.getValue());
            currentMinesCount = Integer.parseInt(minesPercent.getValue());
        }
    }
    //Вставляє готові дані і надсила їх в Receiver
    private void putDataSendResult(){
        intent.putExtra(PREF_WIDTH_KEY, currentWidth);
        intent.putExtra(PREF_HEIGHT_KEY, currentHeight);
        intent.putExtra(PREF_MINES_PERCENT_KEY, currentMinesCount);
        intent.putExtra(PREF_SPACING_KEY, Integer.parseInt(spacing.getValue()));
        sendBroadcast(intent);
    }*/

    //Встановлює Easy настройки
    private void setEasySettings(){
        Toast.makeText(getBaseContext(), DefMinefieldSettings.LEVEL_EASY, Toast.LENGTH_SHORT).show();
        listPreference.setSummary("Weight: "+DefMinefieldSettings.LEVEL_EASY);
        category.setEnabled(false);
        /*currentWidth = DefMinefieldSettings.Easy.WIDTH;
        currentHeight = DefMinefieldSettings.Easy.HEIGHT;
        currentMinesCount = DefMinefieldSettings.Easy.MINES_PERCENT;*/
    }
    //Встановлює Medium настройки
    private void setMediumSettings(){
        Toast.makeText(getBaseContext(), DefMinefieldSettings.LEVEL_MEDIUM, Toast.LENGTH_SHORT).show();
        listPreference.setSummary("Weight: "+DefMinefieldSettings.LEVEL_MEDIUM);
        category.setEnabled(false);
        /*currentWidth = DefMinefieldSettings.Medium.WIDTH;
        currentHeight = DefMinefieldSettings.Medium.HEIGHT;
        currentMinesCount = DefMinefieldSettings.Medium.MINES_PERCENT;*/
    }
    //Встановлює Hard настройки
    private void setHardSettings(){
        Toast.makeText(getBaseContext(), DefMinefieldSettings.LEVEL_HARD, Toast.LENGTH_SHORT).show();
        listPreference.setSummary("Weight: "+DefMinefieldSettings.LEVEL_HARD);
        category.setEnabled(false);
        /*currentWidth = DefMinefieldSettings.Hard.WIDTH;
        currentHeight = DefMinefieldSettings.Hard.HEIGHT;
        currentMinesCount = DefMinefieldSettings.Hard.MINES_PERCENT;*/
    }
    //Встановлює Custom настройки
    private void setCustomSettings(){
        Toast.makeText(getBaseContext(), DefMinefieldSettings.LEVEL_CUSTOM, Toast.LENGTH_SHORT).show();
        listPreference.setSummary("Weight: "+DefMinefieldSettings.LEVEL_CUSTOM);
        category.setEnabled(true);
    }
}
