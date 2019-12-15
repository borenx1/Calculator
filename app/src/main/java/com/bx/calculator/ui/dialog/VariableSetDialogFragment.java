package com.bx.calculator.ui.dialog;

import com.bx.calculator.R;
import com.bx.calculator.db.Variable;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ch.obermuhlner.math.big.BigComplex;

public class VariableSetDialogFragment extends DialogFragment {

    private static final String TAG = "VariableSetDialogFragment";
    private static final String ARG_VARIABLE = "variable_set_dialog_fragment_variable";

    private DialogListener dialogListener;
    private Variable variable;

    private EditText valueEditText;
    private EditText exponentEditText;

    /**
     * Get an instance with {@link #getInstance(Variable)}.
     */
    public VariableSetDialogFragment() {}

    public static VariableSetDialogFragment getInstance(@NonNull Variable variable) {
        final VariableSetDialogFragment fragment = new VariableSetDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_VARIABLE, variable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            variable = getArguments().getParcelable(ARG_VARIABLE);
            if (variable == null) {
                Log.wtf(TAG, "onCreate: variable is null");
            }
        } else {
            Log.wtf(TAG, "No arguments set");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_variable_set, null);
        valueEditText = view.findViewById(R.id.variable_value_edit_text);
        exponentEditText = view.findViewById(R.id.variable_exponent_edit_text);
        exponentEditText.setText("0");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_variable_set_title)
                .setMessage(variable.display)
                .setView(view)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialogListener.onDialogPositiveClick(VariableSetDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialogListener.onDialogNegativeClick(VariableSetDialogFragment.this));
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement DialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dialogListener = null;
    }

    @Nullable
    public Variable getVariable() {
        return variable;
    }

    @Nullable
    public BigComplex getValue() {
        // TODO complex
        final String reValueString = valueEditText.getText().toString().trim();
        String reExponentString = exponentEditText.getText().toString().trim();
        if (reExponentString.isEmpty()) {
            reExponentString = "0";
        }
        try {
            final BigDecimal re = new BigDecimal(reValueString);
            final int reEx = Integer.valueOf(reExponentString);
            return BigComplex.valueOf(re.movePointRight(reEx));
        } catch (NumberFormatException e) {
            Log.d(TAG, "Set variable invalid.", e);
            return null;
        }
    }
}
