package com.bx.calculator.ui.dialog;

import com.bx.calculator.InsertCUnitListener;
import com.bx.calculator.R;
import com.bx.calculator.VariableViewModel;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.db.Variable;
import com.bx.calculator.ui.VariableAdapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VariableInsertDialogFragment extends DialogFragment implements VariableAdapter.OnVariableClickListener {

    private static final String TAG = "VariableInsertDialogFragment";

    private DialogListener dialogListener;
    private InsertCUnitListener insertListener;
    private VariableViewModel model;
    private RecyclerView recyclerView;
    private VariableAdapter adapter;

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
        builder.setTitle(R.string.dialog_variable_insert_title)
                .setView(recyclerView)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialogListener.onDialogNegativeClick(VariableInsertDialogFragment.this));
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
        try {
            insertListener = (InsertCUnitListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement InsertCUnitListener");
        }
        model = ViewModelProviders.of(this).get(VariableViewModel.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dialogListener = null;
        insertListener = null;
        model = null;
    }

    @Override
    public void onVariableClick(int position, Variable variable) {
        if (variable != null) {
            insertListener.insert(CUnit.get(variable.display));
        } else {
            Log.wtf(TAG, "onVariableClick: variable is null.");
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
