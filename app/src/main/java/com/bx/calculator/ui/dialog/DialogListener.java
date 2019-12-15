package com.bx.calculator.ui.dialog;

import androidx.fragment.app.DialogFragment;

/**
 * The activity that creates an instance of a dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it.
 */
public interface DialogListener {

    void onDialogPositiveClick(DialogFragment dialog);
    void onDialogNeutralClick(DialogFragment dialog);
    void onDialogNegativeClick(DialogFragment dialog);
}
