package com.example.lista1_de_compras;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * SettingsActivity: permite cambiar el tema de la app (oscuro/noche).
 * El estado se guarda en SharedPreferences y se aplica a toda la app.
 */
public class SettingsActivity extends AppCompatActivity {
    private Switch switchTheme;
    private static final String PREFS_NAME = "settings_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchTheme = findViewById(R.id.switchTheme);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, true);
        switchTheme.setChecked(isDarkMode);
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Tema actualizado", Toast.LENGTH_SHORT).show();
        });
    }
}

