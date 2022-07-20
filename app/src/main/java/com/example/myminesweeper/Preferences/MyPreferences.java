package com.example.myminesweeper.Preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.widget.Toast;

import com.example.myminesweeper.DefMinefieldSettings;
import com.example.myminesweeper.R;

public class MyPreferences extends PreferenceActivity {
    public static final String PREF_RESEIVER_ACTION = "com.example.myminesweeper/prefReceiverCallback";
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        intent = new Intent(PREF_RESEIVER_ACTION);
        findPrefAndSetListeners();
        checkListPref();
    }

    private void findPrefAndSetListeners() {
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

    private void checkListPref() {
        switch (listPreference.getValue()) {
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
        listPreference.setSummary("Weight: " + listPreference.getValue());
        width.setSummary("Width: " + width.getValue());
        height.setSummary("Height: " + height.getValue());
        minesPercent.setSummary("Mines percent: " + minesPercent.getValue());
        spacing.setSummary("Spacing: " + spacing.getValue());
    }

    private final Preference.OnPreferenceChangeListener listListener;

    {
        listListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                intent = new Intent(PREF_RESEIVER_ACTION);
                switch ((String) newValue) {
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
    }

    private final Preference.OnPreferenceChangeListener customCompliclistener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            intent = new Intent(PREF_RESEIVER_ACTION);
            switch (preference.getKey()) {
                case "mWidth":
                    width.setSummary("Width: " + (String) newValue);
                    intent.putExtra(PREF_WIDTH_KEY, Integer.parseInt((String) newValue));
                    break;
                case "mHeight":
                    height.setSummary("Height: " + (String) newValue);
                    intent.putExtra(PREF_HEIGHT_KEY, Integer.parseInt((String) newValue));
                    break;
                case "mMinesCount":
                    minesPercent.setSummary("Percents of mines: " + (String) newValue);
                    intent.putExtra(PREF_MINES_PERCENT_KEY, Integer.parseInt((String) newValue));
                    break;
                case "spacing":
                    spacing.setSummary("Spacing: " + (String) newValue);
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
    }

    private void setEasySettings() {
        Toast.makeText(getBaseContext(), DefMinefieldSettings.LEVEL_EASY, Toast.LENGTH_SHORT).show();
        listPreference.setSummary("Weight: " + DefMinefieldSettings.LEVEL_EASY);
        category.setEnabled(false);
    }

    private void setMediumSettings() {
        Toast.makeText(getBaseContext(), DefMinefieldSettings.LEVEL_MEDIUM, Toast.LENGTH_SHORT).show();
        listPreference.setSummary("Weight: " + DefMinefieldSettings.LEVEL_MEDIUM);
        category.setEnabled(false);
    }

    private void setHardSettings() {
        Toast.makeText(getBaseContext(), DefMinefieldSettings.LEVEL_HARD, Toast.LENGTH_SHORT).show();
        listPreference.setSummary("Weight: " + DefMinefieldSettings.LEVEL_HARD);
        category.setEnabled(false);
    }

    private void setCustomSettings() {
        Toast.makeText(getBaseContext(), DefMinefieldSettings.LEVEL_CUSTOM, Toast.LENGTH_SHORT).show();
        listPreference.setSummary("Weight: " + DefMinefieldSettings.LEVEL_CUSTOM);
        category.setEnabled(true);
    }
}
