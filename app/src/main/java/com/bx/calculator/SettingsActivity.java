package com.bx.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.bx.calculator.ui.dialog.DialogListener;
import com.bx.calculator.ui.dialog.SettingsDeleteHistoryDialogFragment;
import com.bx.calculator.ui.dialog.SettingsResetVariablesDialogFragment;

public class SettingsActivity extends AppCompatActivity implements DialogListener {

    private SettingsViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("theme_dark", false)) {
            setTheme(R.style.AppTheme_Dark);
        }
        setContentView(R.layout.activity_settings);

        model = ViewModelProviders.of(this).get(SettingsViewModel.class);
    }

    /**
     * Provides same action as up button to recreate parent activity after pop back stack.
     */
    @Override
    public void onBackPressed() {
        // main activity must have default launch mode to recreate when this is popped from back stack
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof SettingsDeleteHistoryDialogFragment) {
            model.deleteHistory();
        } else if (dialog instanceof SettingsResetVariablesDialogFragment) {
            model.resetVariables();
        }
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    protected void vibrate(int milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, 50));
        }
    }
}
