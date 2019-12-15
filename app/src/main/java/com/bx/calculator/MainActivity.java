package com.bx.calculator;

import com.bx.calculator.calc.CFormat;
import com.bx.calculator.calc.CNum;
import com.bx.calculator.calc.CParams;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.calc.Calculate;
import com.bx.calculator.db.Variable;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import ch.obermuhlner.math.big.BigComplex;

import com.bx.calculator.ui.dialog.ConstantInsertDialogFragment;
import com.bx.calculator.ui.dialog.ConstantOptionsDialogFragment;
import com.bx.calculator.ui.dialog.DialogListener;
import com.bx.calculator.ui.dialog.VariableInsertDialogFragment;
import com.bx.calculator.ui.dialog.VariableSetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener,
        InputFragment.OnButtonPressedListener, DialogListener, InsertCUnitListener {

    private static final String TAG = "MainActivity";

    public static final int MODE_BASIC = 0;
    public static final int MODE_SCIENTIFIC = 1;
    public static final int MODE_GRAPHING = 2;

    private static final String PREF_CALCULATOR_MODE = "com.bx.calculator.calculator_mode";

    private MainViewModel model;
    private MediaPlayer buttonSoundPlayer;

    private CalculatorMode calculatorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("theme_dark", false)) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        setContentView(R.layout.activity_main);

        model = ViewModelProviders.of(this).get(MainViewModel.class);
        buttonSoundPlayer = MediaPlayer.create(this, R.raw.button_1);

        // setup views
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setup states
        final int calculatorMode = getPreferences(Context.MODE_PRIVATE).getInt(PREF_CALCULATOR_MODE, MODE_BASIC);
        switch (calculatorMode) {
            case MODE_BASIC:
                navigationView.setCheckedItem(R.id.nav_modes_basic);
                break;
            case MODE_SCIENTIFIC:
                navigationView.setCheckedItem(R.id.nav_modes_scientific);
                break;
            case MODE_GRAPHING:
                navigationView.setCheckedItem(R.id.nav_modes_graphing);
                break;
            default:
                Log.wtf(TAG, "Unrecognised mode: " + calculatorMode);
        }
        setCalculatorMode(calculatorMode);

        // viewModel observers, listeners
        CalculateManager.getInstance().setParamsAngleUnit(
                Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("calculator_angle_unit", "0")));
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        model.getAllVariables().observe(this, variables -> {
            final CalculateManager manager = CalculateManager.getInstance();
            final Map<CUnit, BigComplex> variableMap = new HashMap<>();
            for (final Variable var: variables) {
                variableMap.put(CUnit.get(var.display), var.value);
            }
            variableMap.put(CUnit.ANS, manager.getParams().getValue(CUnit.ANS));
            manager.setParamsVariables(variableMap);
        });
        model.getAnswer().observe(this, answer -> CalculateManager.getInstance().setParamsAnswer(answer));
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem angleItem = menu.findItem(R.id.menu_angle);
        final int angleUnit = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("calculation_angle_unit", "0"));
        switch (angleUnit) {
            case CParams.ANGLE_RAD:
                angleItem.setIcon(R.drawable.ic_angle_rad);
                angleItem.setTooltipText(getString(R.string.menu_angle) + " (" + getString(R.string.rad) + ")");
                break;
            case CParams.ANGLE_DEG:
                angleItem.setIcon(R.drawable.ic_angle_deg);
                angleItem.setTooltipText(getString(R.string.menu_angle) + " (" + getString(R.string.deg) + ")");
                break;
            default:
                Log.wtf(TAG, "Unknown angle unit: " + angleUnit);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_angle:
                final int angleUnit = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this)
                        .getString("calculation_angle_unit", "0"));
                switch (angleUnit) {
                    case CParams.ANGLE_RAD:
                        setAngleUnit(CParams.ANGLE_DEG);
                        break;
                    case CParams.ANGLE_DEG:
                        setAngleUnit(CParams.ANGLE_RAD);
                        break;
                    default:
                        Log.wtf(TAG, "Unrecognized angle unit: " + angleUnit);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_modes_basic:
                if (!item.isChecked()) {
                    setCalculatorMode(MODE_BASIC);
                }
                break;
            case R.id.nav_modes_scientific:
                if (!item.isChecked()) {
                    setCalculatorMode(MODE_SCIENTIFIC);
                }
                break;
            case R.id.nav_modes_graphing:
                if (!item.isChecked()) {
                    setCalculatorMode(MODE_GRAPHING);
                }
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                Log.wtf(TAG, "Unknown navigation item: " + item);
        }
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "calculation_angle_unit":
                CalculateManager.getInstance().setParamsAngleUnit(Integer.valueOf(sharedPreferences.getString(key, "0")));
                break;
        }
    }

    @Override
    public void onButtonPressed(int buttonCode) {
        if (calculatorFragment == null) {
            throw new IllegalStateException("No calculator mode fragment.");
        }
        switch (buttonCode) {
            case InputCodes.EXECUTE:
                calculatorFragment.execute();
                break;
            case InputCodes.CLEAR:
                calculatorFragment.clear();
                break;
            case InputCodes.DELETE:
                calculatorFragment.delete();
                break;
            case InputCodes.MORE:
                calculatorFragment.changeInputMode();
                break;
            case InputCodes.VARIABLE:
                new VariableInsertDialogFragment().show(getSupportFragmentManager(), null);
                break;
            case InputCodes.CONSTANT:
                new ConstantOptionsDialogFragment().show(getSupportFragmentManager(), null);
                break;
            case InputCodes.ANS:
                calculatorFragment.insert(CUnit.ANS);
                break;
            case InputCodes.PLUS:
                calculatorFragment.insert(CUnit.PLUS);
                break;
            case InputCodes.MINUS:
                calculatorFragment.insert(CUnit.MINUS);
                break;
            case InputCodes.TIMES:
                calculatorFragment.insert(CUnit.TIMES);
                break;
            case InputCodes.DIVIDE:
                calculatorFragment.insert(CUnit.DIVIDE);
                break;
            case InputCodes.POWER:
                calculatorFragment.insert(CUnit.POWER);
                break;
            case InputCodes.EXP:
                calculatorFragment.insert(CUnit.EXP);
                break;
            case InputCodes.NROOT:
                calculatorFragment.insert(CUnit.ROOT);
                break;
            case InputCodes.NPR:
                calculatorFragment.insert(CUnit.NPR);
                break;
            case InputCodes.NCR:
                calculatorFragment.insert(CUnit.NCR);
                break;
            case InputCodes.LEFT_BRACKET:
                calculatorFragment.insert(CUnit.LEFT_BRACKET);
                break;
            case InputCodes.RIGHT_BRACKET:
                calculatorFragment.insert(CUnit.RIGHT_BRACKET);
                break;
            case InputCodes.POINT:
                calculatorFragment.insert(CUnit.POINT);
                break;
            case InputCodes.ZERO:
                calculatorFragment.insert(CUnit.ZERO);
                break;
            case InputCodes.ONE:
                calculatorFragment.insert(CUnit.ONE);
                break;
            case InputCodes.TWO:
                calculatorFragment.insert(CUnit.TWO);
                break;
            case InputCodes.THREE:
                calculatorFragment.insert(CUnit.THREE);
                break;
            case InputCodes.FOUR:
                calculatorFragment.insert(CUnit.FOUR);
                break;
            case InputCodes.FIVE:
                calculatorFragment.insert(CUnit.FIVE);
                break;
            case InputCodes.SIX:
                calculatorFragment.insert(CUnit.SIX);
                break;
            case InputCodes.SEVEN:
                calculatorFragment.insert(CUnit.SEVEN);
                break;
            case InputCodes.EIGHT:
                calculatorFragment.insert(CUnit.EIGHT);
                break;
            case InputCodes.NINE:
                calculatorFragment.insert(CUnit.NINE);
                break;
            case InputCodes.PI:
                calculatorFragment.insert(CNum.PI);
                break;
            case InputCodes.TAU:
                calculatorFragment.insert(CNum.TAU);
                break;
            case InputCodes.E:
                calculatorFragment.insert(CNum.E);
                break;
            case InputCodes.I:
                calculatorFragment.insert(CNum.I);
                break;
            case InputCodes.SQUARED:
                calculatorFragment.insert(CUnit.SQUARED);
                break;
            case InputCodes.CUBED:
                calculatorFragment.insert(CUnit.CUBED);
                break;
            case InputCodes.INVERSE:
                calculatorFragment.insert(CUnit.INVERSE);
                break;
            case InputCodes.FACTORIAL:
                calculatorFragment.insert(CUnit.FACTORIAL);
                break;
            case InputCodes.SQRT:
                calculatorFragment.insert(CUnit.SQRT);
                break;
            case InputCodes.ABS:
                calculatorFragment.insert(CUnit.ABS);
                break;
            case InputCodes.SIN:
                calculatorFragment.insert(CUnit.SIN);
                break;
            case InputCodes.COS:
                calculatorFragment.insert(CUnit.COS);
                break;
            case InputCodes.TAN:
                calculatorFragment.insert(CUnit.TAN);
                break;
            case InputCodes.ASIN:
                calculatorFragment.insert(CUnit.ASIN);
                break;
            case InputCodes.ACOS:
                calculatorFragment.insert(CUnit.ACOS);
                break;
            case InputCodes.ATAN:
                calculatorFragment.insert(CUnit.ATAN);
                break;
            case InputCodes.SINH:
                calculatorFragment.insert(CUnit.SINH);
                break;
            case InputCodes.COSH:
                calculatorFragment.insert(CUnit.COSH);
                break;
            case InputCodes.TANH:
                calculatorFragment.insert(CUnit.TANH);
                break;
            case InputCodes.ASINH:
                calculatorFragment.insert(CUnit.ASINH);
                break;
            case InputCodes.ACOSH:
                calculatorFragment.insert(CUnit.ACOSH);
                break;
            case InputCodes.ATANH:
                calculatorFragment.insert(CUnit.ATANH);
                break;
            case InputCodes.CSC:
                calculatorFragment.insert(CUnit.CSC);
                break;
            case InputCodes.SEC:
                calculatorFragment.insert(CUnit.SEC);
                break;
            case InputCodes.COT:
                calculatorFragment.insert(CUnit.COT);
                break;
            case InputCodes.LOG10:
                calculatorFragment.insert(CUnit.LOG10);
                break;
            case InputCodes.LOG:
                calculatorFragment.insert(CUnit.LOG);
                break;
            case InputCodes.LOG2:
                calculatorFragment.insert(CUnit.LOG2);
                break;
            case InputCodes.ARG:
                calculatorFragment.insert(CUnit.ARG);
                break;
            case InputCodes.CONJ:
                calculatorFragment.insert(CUnit.CONJ);
                break;
            default:
                Log.wtf(TAG, "Unknown code: " + buttonCode);
                break;
        }
    }

    @Override
    public void onButtonDown() {
        // sound and vibrate
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("input_sound", false)) {
            buttonSoundPlayer.start();
        }
        if (preferences.getBoolean("input_vibrate", true)) {
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                final int duration = preferences.getInt("input_vibrate_duration", 50);
                vibrator.vibrate(VibrationEffect.createOneShot(duration, 50));
            }
        }
    }

    @Override
    public void insert(CUnit unit) {
        if (calculatorFragment != null) {
            calculatorFragment.insert(unit);
        } else {
            Log.wtf(TAG, "calculatorFragment is null in InsertCUnitListener: insert");
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof VariableSetDialogFragment) {
            final VariableSetDialogFragment d = (VariableSetDialogFragment) dialog;
            final BigComplex value = d.getValue();
            if (value == null) {
                Snackbar.make(findViewById(R.id.coordinator_layout), R.string.snackbar_set_variable_error, Snackbar.LENGTH_LONG).show();
            } else if (Calculate.isInRange(value)) {
                final Variable variable = d.getVariable();
                if (variable != null) {
                    model.updateVariable(new Variable(variable.display, value, variable.order));
                    Snackbar.make(findViewById(R.id.coordinator_layout),
                            getString(R.string.snackbar_set_variable, variable.display, CFormat.toConditionalString(value, this)),
                            Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(findViewById(R.id.coordinator_layout), R.string.snackbar_set_variable_out_of_range, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        if (dialog instanceof ConstantInsertDialogFragment) {
            // back
            new ConstantOptionsDialogFragment().show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    /**
     * Sets the calculator mode: replace fragment, then save the new mode.
     *
     * @param mode One of {@link #MODE_BASIC}, {@link #MODE_SCIENTIFIC},
     * {@link #MODE_GRAPHING}.
     */
    protected void setCalculatorMode(int mode) throws IllegalArgumentException {
        final Fragment fragment;
        switch (mode) {
            case MODE_BASIC:
                fragment = CalculatorModeFragment.newInstance(CalculatorModeFragment.Mode.BASIC);
                break;
            case MODE_SCIENTIFIC:
                fragment = CalculatorModeFragment.newInstance(CalculatorModeFragment.Mode.SCIENTIFIC);
                break;
            case MODE_GRAPHING:
                fragment = CalculatorModeFragment.newInstance(CalculatorModeFragment.Mode.SCIENTIFIC);
                break;
            default:
                Log.wtf(TAG, "Unknown mode: " + mode);
                throw new IllegalArgumentException(String.format("Unrecognized mode: %s", mode));
        }
        calculatorFragment = (CalculatorMode) fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        getPreferences(Context.MODE_PRIVATE).edit().putInt(PREF_CALCULATOR_MODE, mode).apply();
    }

    /**
     * Sets the angle unit: changes the icon in the action bar, then saves the value.
     * @param angleUnit one of {@link CParams#ANGLE_RAD}, {@link CParams#ANGLE_DEG}
     */
    protected void setAngleUnit(int angleUnit) {
        if (angleUnit == CParams.ANGLE_RAD || angleUnit == CParams.ANGLE_DEG) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("calculation_angle_unit", String.valueOf(angleUnit)).apply();
            invalidateOptionsMenu();
        } else {
            Log.wtf(TAG, "Unknown angle unit: " + angleUnit);
        }
    }
}
