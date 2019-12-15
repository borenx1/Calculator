package com.bx.calculator;

import com.bx.calculator.calc.CFormat;
import com.bx.calculator.calc.CParams;
import com.bx.calculator.calc.CResult;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.calc.exception.OutOfRangeException;
import com.bx.calculator.calc.exception.SyntaxException;
import com.bx.calculator.calc.exception.VariableException;
import com.bx.calculator.math.UndefinedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

/**
 * The base for a standard calculator, provides behaviour for {@link CalculatorMode}. Subclasses must use
 * a layout by overriding {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
 */
public class CalculatorModeFragment extends Fragment implements CalculatorMode {

    public enum Mode {
        BASIC, SCIENTIFIC
    }

    private static final String TAG = "CalculatorModeFragment";
    private static final String ARG_MODE = "calculator_mode_fragment_mode";

    private Mode mode;

    private MainViewModel model;
    private CalculatorDisplayFragment output;
    private InputFragment input;

    /**
     * Create a new instance with {@link #newInstance(Mode)}.
     */
    public CalculatorModeFragment() {}

    public static CalculatorModeFragment newInstance(@NonNull Mode mode) {
        final CalculatorModeFragment fragment = new CalculatorModeFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        model = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = Mode.values()[getArguments().getInt(ARG_MODE)];
        } else {
            Log.wtf(TAG, "No arguments");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calculator_mode, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        output = (CalculatorDisplayFragment) getChildFragmentManager().findFragmentById(R.id.output);
        output.getInputView().requestFocus();
        switch (mode) {
            case BASIC:
                setInputFragment(InputFragment.Mode.BASIC);
                break;
            case SCIENTIFIC:
                setInputFragment(InputFragment.Mode.SCIENTIFIC);
                break;
            default:
                Log.wtf(TAG, "Unknown mode: " + mode);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        model = null;
    }

    @Override
    public void execute() {
        final CUnit[] input = model.getInput().getValue();
        if (input == null || input.length == 0) {
            return;
        }

        final Runnable onStarted = () -> model.setCalculating(true);
        final Consumer<CResult> onComplete = result -> {
            model.setCalculating(false);
            model.addResultToHistory(result);
            clear();
        };
        final Consumer<Exception> onException = exception -> {
            model.setCalculating(false);
            if (exception instanceof VariableException) {
                Snackbar.make(getView(), R.string.snackbar_error_null_answer, Snackbar.LENGTH_LONG).show();
            } else if (exception instanceof SyntaxException) {
                Snackbar.make(getView(), R.string.snackbar_error_syntax, Snackbar.LENGTH_LONG).show();
            } else if (exception instanceof UndefinedException) {
                Snackbar.make(getView(), R.string.snackbar_error_undefined, Snackbar.LENGTH_LONG).show();
            } else if (exception instanceof OutOfRangeException) {
                Snackbar.make(getView(), R.string.snackbar_error_out_of_range, Snackbar.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Unknown calculation exception", exception);
                Snackbar.make(getView(), R.string.snackbar_error_unknown, Snackbar.LENGTH_LONG).show();
            }
        };
        CalculateManager.getInstance().calculate(input, onStarted, onComplete, onException);
    }

    @Override
    public void clear() {
        model.setInput(null);
    }

    @Override
    public void delete() {
        final CUnit[] input = model.getInput().getValue();
        if (input == null || input.length == 0) {
            return;
        }
        final int maxChars = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("input_max_char", 60);
        if (input.length <= maxChars) {
            // position of cursor in inputView
            final int charIndex = getInputSelection();
            // equivalent position in the sequence
            final int unitIndex = CFormat.display2UnitIndex(input, charIndex);
            if (unitIndex > 0) {
                final int cursor = CFormat.unit2DisplayIndex(input, unitIndex - 1);
                final List<CUnit> tempInput = new ArrayList<>(Arrays.asList(input));
                tempInput.remove(unitIndex - 1);
                final CUnit[] newInput = tempInput.toArray(new CUnit[] {});
                model.setInput(newInput);
                setInputSelection(cursor);
            }
        } else {
            Snackbar.make(getView(), getString(R.string.snackbar_max_char, maxChars), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void insert(CUnit unit) {
        CUnit[] input = model.getInput().getValue();
        if (input == null) {
            input = new CUnit[] {};
        }
        // position of cursor in inputView
        final int charIndex = getInputSelection();
        // equivalent position in the sequence
        final int unitIndex = CFormat.display2UnitIndex(input, charIndex);

        final List<CUnit> tempInput = new ArrayList<>(Arrays.asList(input));
        tempInput.add(unitIndex, unit);
        final CUnit[] newInput = tempInput.toArray(new CUnit[] {});
        model.setInput(newInput);
        setInputSelection(CFormat.unit2DisplayIndex(newInput, unitIndex + 1));
    }

    @Override
    public void changeInputMode() {
        if (mode == Mode.SCIENTIFIC && input != null) {
            if (input.getMode() == InputFragment.Mode.SCIENTIFIC) {
                setInputFragment(InputFragment.Mode.SCIENTIFIC2);
            } else if (input.getMode() == InputFragment.Mode.SCIENTIFIC2) {
                setInputFragment(InputFragment.Mode.SCIENTIFIC);
            }
        }
    }

    private void setInputFragment(@NonNull InputFragment.Mode inputMode) {
        input = InputFragment.newInstance(inputMode);
        getChildFragmentManager().beginTransaction().replace(R.id.input_container, input).commit();
    }

    /**
     * @return Selection position of the input {@link EditText}.
     */
    private int getInputSelection() {
        return output.getInputSelection();
    }

    /**
     * Sets the selection position of the input {@link EditText}.
     *
     * @param position Selection position.
     */
    private void setInputSelection(int position) {
        output.setInputSelection(position);
    }
}
