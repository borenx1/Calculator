package com.bx.calculator.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.bx.calculator.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConstantOptionsDialogFragment extends DialogFragment {

    private static final String TAG = "ConstantOptionsDialogFragment";

    private DialogListener dialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_constant_options_title)
                .setItems(R.array.constants, (dialog, which) -> ConstantInsertDialogFragment.getInstance(which).show(getFragmentManager(), null))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialogListener.onDialogNegativeClick(ConstantOptionsDialogFragment.this));
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement DialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dialogListener = null;
    }
}
