package com.bx.calculator.ui.dialog;

import com.bx.calculator.R;
import com.bx.calculator.VariableViewModel;
import com.bx.calculator.calc.CFormat;
import com.bx.calculator.db.Variable;
import com.bx.calculator.ui.VariableAdapter;
import com.google.android.material.snackbar.Snackbar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.obermuhlner.math.big.BigComplex;

public class VariableMapDialogFragment extends DialogFragment implements VariableAdapter.OnVariableClickListener {

    private static final String TAG = "VariableMapDialogFragment";
    private static final String ARG_VALUE_RE = "map_variable_dialog_fragment_value_re";
    private static final String ARG_VALUE_IM = "map_variable_dialog_fragment_value_im";

    private DialogListener dialogListener;
    private BigComplex value;
    private VariableViewModel model;
    private RecyclerView recyclerView;
    private VariableAdapter adapter;

    /**
     * Get an instance with {@link #getInstance(BigComplex)}.
     */
    public VariableMapDialogFragment() {}

    public static VariableMapDialogFragment getInstance(@NonNull BigComplex value) {
        final VariableMapDialogFragment fragment = new VariableMapDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_VALUE_RE, value.re.toString());
        args.putString(ARG_VALUE_IM, value.im.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            final String reString = getArguments().getString(ARG_VALUE_RE);
            final String imString = getArguments().getString(ARG_VALUE_IM);
            try {
                value = BigComplex.valueOf(new BigDecimal(reString), new BigDecimal(imString));
            } catch (NumberFormatException | NullPointerException e) {
                Log.wtf(TAG, "Value format error: re = " + reString + ", im = " + imString);
                value = BigComplex.ZERO;
            }
        } else {
            Log.wtf(TAG, "No arguments set");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        recyclerView = new RecyclerView(getContext());
        adapter = new VariableAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        model.getAllVariables().observe(this, variables -> adapter.submitList(variables));

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_variable_map_title)
                .setMessage(CFormat.toConditionalString(value, getContext()))
                .setView(recyclerView)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialogListener.onDialogNegativeClick(VariableMapDialogFragment.this));
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
        model = ViewModelProviders.of(this).get(VariableViewModel.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dialogListener = null;
        model = null;
    }

    @Override
    public void onVariableClick(int position, Variable variable) {
        if (value != null && variable != null) {
            model.updateVariable(new Variable(variable.display, value, variable.order));
            final String snackbarMessage = getString(R.string.snackbar_set_variable,variable.display, CFormat.toConditionalString(value, getContext()));
            Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), snackbarMessage, Snackbar.LENGTH_LONG).show();
        } else {
            Log.wtf(TAG, "onVariableClick: value or variable is null.");
        }
        dismiss();
    }

    @Override
    public void onVariableSet(int position, Variable variable) {
        if (variable != null) {
            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.detach(this).addToBackStack(null);
            VariableSetDialogFragment.getInstance(variable).show(transaction, null);
        } else {
            Log.wtf(TAG, "onVariableSet: variable is null.");
            dismiss();
        }
    }

    @Override
    public void onVariableReset(int position, Variable variable) {
        if (variable != null) {
            model.updateVariable(Variable.getDefault(variable.display));
        } else {
            Log.wtf(TAG, "onVariableReset: variable is null.");
        }
    }
}
