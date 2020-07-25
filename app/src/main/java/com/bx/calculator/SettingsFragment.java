package com.bx.calculator;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bx.calculator.ui.dialog.SettingsDeleteHistoryDialogFragment;
import com.bx.calculator.ui.dialog.SettingsResetVariablesDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

/**
 * Create order:
 * <ol>
 *     <li>{@link #onCreate(Bundle)}.</li>
 *     <li>{@link #onCreatePreferences(Bundle, String)}.</li>
 *     <li>{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.</li>
 *     <li>{@link #onViewCreated(View, Bundle)}.</li>
 *     <li>{@link #onActivityCreated(Bundle)}.</li>
 *     <li>{@link #onStart()}.</li>
 *     <li>{@link #onResume()}.</li>
 * </ol>
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        // manually add preferences
        // MUST USE THIS CONTEXT for themes and styles
        final Context context = getPreferenceManager().getContext();
        PreferenceCategory inputCategory = findPreference("input_category");
        PreferenceCategory outputCategory = findPreference("output_category");

        SeekBarPreference maxCharacters = new SeekBarPreference(context);
        maxCharacters.setKey("input_max_char");
        maxCharacters.setTitle(R.string.settings_input_max_char);
        maxCharacters.setShowSeekBarValue(true);
        maxCharacters.setMin(20);
        maxCharacters.setMax(99);
        maxCharacters.setSeekBarIncrement(1);
        maxCharacters.setDefaultValue(60);
        maxCharacters.setOrder(0);

        SeekBarPreference vibrateDuration = new SeekBarPreference(context);
        vibrateDuration.setKey("input_vibrate_duration");
        vibrateDuration.setTitle(R.string.settings_input_vibrate_duration);
        vibrateDuration.setShowSeekBarValue(true);
        vibrateDuration.setMin(20);
        vibrateDuration.setMax(100);
        vibrateDuration.setSeekBarIncrement(10);
        vibrateDuration.setDefaultValue(50);

        SeekBarPreference sigfig = new SeekBarPreference(context);
        sigfig.setKey("output_sigfig");
        sigfig.setTitle(R.string.settings_output_sigfig);
        sigfig.setShowSeekBarValue(true);
        sigfig.setMin(6);
        sigfig.setMax(20);
        sigfig.setSeekBarIncrement(1);
        sigfig.setDefaultValue(10);

        SeekBarPreference scientificLower = new SeekBarPreference(context);
        scientificLower.setKey("output_scientific_lower");
        scientificLower.setTitle(R.string.settings_output_scientific_lower);
        scientificLower.setSummary(R.string.settings_output_scientific_lower_summary);
        scientificLower.setShowSeekBarValue(true);
        scientificLower.setMin(3);
        scientificLower.setMax(10);
        scientificLower.setSeekBarIncrement(1);
        scientificLower.setDefaultValue(3);

        SeekBarPreference scientificUpper = new SeekBarPreference(context);
        scientificUpper.setKey("output_scientific_upper");
        scientificUpper.setTitle(R.string.settings_output_scientific_upper);
        scientificUpper.setSummary(R.string.settings_output_scientific_upper_summary);
        scientificUpper.setShowSeekBarValue(true);
        scientificUpper.setMin(3);
        scientificUpper.setMax(10);
        scientificUpper.setSeekBarIncrement(1);
        scientificUpper.setDefaultValue(6);

        inputCategory.addPreference(maxCharacters);
        inputCategory.addPreference(vibrateDuration);
        outputCategory.addPreference(sigfig);
        outputCategory.addPreference(scientificLower);
        outputCategory.addPreference(scientificUpper);

        vibrateDuration.setOnPreferenceChangeListener(this);
        findPreference("theme_dark").setOnPreferenceChangeListener(this);
        sigfig.setOnPreferenceChangeListener(this);
        scientificLower.setOnPreferenceChangeListener(this);
        scientificUpper.setOnPreferenceChangeListener(this);

        findPreference("memory_history").setOnPreferenceClickListener(this);
        findPreference("memory_variables").setOnPreferenceClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findPreference("input_vibrate_duration").setDependency("input_vibrate");
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "input_vibrate_duration":
                ((SettingsActivity) getActivity()).vibrate((int) newValue);
                break;
            case "theme_dark":
                getActivity().recreate();
                break;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "memory_history":
                new SettingsDeleteHistoryDialogFragment().show(getFragmentManager(), null);
                return true;
            case "memory_variables":
                new SettingsResetVariablesDialogFragment().show(getFragmentManager(), null);
                return true;
            default:
                return false;
        }
    }
}
