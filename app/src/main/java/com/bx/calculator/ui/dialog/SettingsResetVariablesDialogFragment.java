package com.bx.calculator.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.bx.calculator.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Called on reset variables setting in {@link com.bx.calculator.SettingsFragment}.
 */
public class SettingsResetVariablesDialogFragment extends DialogFragment {

    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_settings_memory_variables_title)
                .setMessage(R.string.dialog_settings_memory_variables_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> listener.onDialogPositiveClick(SettingsResetVariablesDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(SettingsResetVariablesDialogFragment.this));
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement DialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
