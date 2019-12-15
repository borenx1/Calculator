package com.bx.calculator;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InputFragment extends Fragment {

    public enum Mode {
        BASIC, SCIENTIFIC, SCIENTIFIC2
    }

    private static final String TAG = "InputFragment";
    private static final String ARG_MODE = "input_fragment_mode";

    private OnButtonPressedListener listener;
    private Mode mode;

    /**
     * Create a new instance with {@link #newInstance(Mode)}.
     */
    public InputFragment() {}

    public static InputFragment newInstance(@NonNull Mode mode) {
        final InputFragment fragment = new InputFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnButtonPressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnButtonPressedListener");
        }
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
        switch (mode) {
            case BASIC:
                return inflater.inflate(R.layout.fragment_basic_input, container, false);
            case SCIENTIFIC:
                return inflater.inflate(R.layout.fragment_scientific_input, container, false);
            case SCIENTIFIC2:
                return inflater.inflate(R.layout.fragment_scientific_input_2, container, false);
            default:
                Log.wtf(TAG, "Unknown mode: " + mode);
                return inflater.inflate(R.layout.fragment_basic_input, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindButtonOnClick(view, R.id.button_clear, InputCodes.CLEAR);
        bindButtonOnClick(view, R.id.button_delete, InputCodes.DELETE);
        bindButtonOnClick(view, R.id.button_execute, InputCodes.EXECUTE);
        bindButtonOnClick(view, R.id.button_variable, InputCodes.VARIABLE);
        bindButtonOnClick(view, R.id.button_constant, InputCodes.CONSTANT);
        switch (mode) {
            case SCIENTIFIC2:
                bindButtonOnClick(view, R.id.button_pi, InputCodes.PI);
                bindButtonOnClick(view, R.id.button_e, InputCodes.E);
                bindButtonOnClick(view, R.id.button_i, InputCodes.I);
                bindButtonOnClick(view, R.id.button_tau, InputCodes.TAU);
                bindButtonOnClick(view, R.id.button_asin, InputCodes.ASIN);
                bindButtonOnClick(view, R.id.button_acos, InputCodes.ACOS);
                bindButtonOnClick(view, R.id.button_atan, InputCodes.ATAN);
                bindButtonOnClick(view, R.id.button_sinh, InputCodes.SINH);
                bindButtonOnClick(view, R.id.button_cosh, InputCodes.COSH);
                bindButtonOnClick(view, R.id.button_tanh, InputCodes.TANH);
                bindButtonOnClick(view, R.id.button_asinh, InputCodes.ASINH);
                bindButtonOnClick(view, R.id.button_acosh, InputCodes.ACOSH);
                bindButtonOnClick(view, R.id.button_atanh, InputCodes.ATANH);
                bindButtonOnClick(view, R.id.button_csc, InputCodes.CSC);
                bindButtonOnClick(view, R.id.button_sec, InputCodes.SEC);
                bindButtonOnClick(view, R.id.button_cot, InputCodes.COT);
                bindButtonOnClick(view, R.id.button_abs, InputCodes.ABS);
                bindButtonOnClick(view, R.id.button_arg, InputCodes.ARG);
                bindButtonOnClick(view, R.id.button_conj, InputCodes.CONJ);
                bindButtonOnClick(view, R.id.button_cubed, InputCodes.CUBED);
                bindButtonOnClick(view, R.id.button_inverse, InputCodes.INVERSE);
                bindButtonOnClick(view, R.id.button_factorial, InputCodes.FACTORIAL);
                bindButtonOnClick(view, R.id.button_xroot, InputCodes.NROOT);
                bindButtonOnClick(view, R.id.button_log2, InputCodes.LOG2);
                bindButtonOnClick(view, R.id.button_npr, InputCodes.NPR);
                bindButtonOnClick(view, R.id.button_ncr, InputCodes.NCR);
                bindButtonOnClick(view, R.id.button_more, InputCodes.MORE);
                break;
            case SCIENTIFIC:
                bindButtonOnClick(view, R.id.button_exp, InputCodes.EXP);
                bindButtonOnClick(view, R.id.button_ans, InputCodes.ANS);
                bindButtonOnClick(view, R.id.button_pi, InputCodes.PI);
                bindButtonOnClick(view, R.id.button_e, InputCodes.E);
                bindButtonOnClick(view, R.id.button_squared, InputCodes.SQUARED);
                bindButtonOnClick(view, R.id.button_sqrt, InputCodes.SQRT);
                bindButtonOnClick(view, R.id.button_abs, InputCodes.ABS);
                bindButtonOnClick(view, R.id.button_sin, InputCodes.SIN);
                bindButtonOnClick(view, R.id.button_cos, InputCodes.COS);
                bindButtonOnClick(view, R.id.button_tan, InputCodes.TAN);
                bindButtonOnClick(view, R.id.button_log, InputCodes.LOG10);
                bindButtonOnClick(view, R.id.button_ln, InputCodes.LOG);
                bindButtonOnClick(view, R.id.button_more, InputCodes.MORE);
                // fall through
            case BASIC:
                bindButtonOnClick(view, R.id.button_0, InputCodes.ZERO);
                bindButtonOnClick(view, R.id.button_1, InputCodes.ONE);
                bindButtonOnClick(view, R.id.button_2, InputCodes.TWO);
                bindButtonOnClick(view, R.id.button_3, InputCodes.THREE);
                bindButtonOnClick(view, R.id.button_4, InputCodes.FOUR);
                bindButtonOnClick(view, R.id.button_5, InputCodes.FIVE);
                bindButtonOnClick(view, R.id.button_6, InputCodes.SIX);
                bindButtonOnClick(view, R.id.button_7, InputCodes.SEVEN);
                bindButtonOnClick(view, R.id.button_8, InputCodes.EIGHT);
                bindButtonOnClick(view, R.id.button_9, InputCodes.NINE);
                bindButtonOnClick(view, R.id.button_point, InputCodes.POINT);
                bindButtonOnClick(view, R.id.button_plus, InputCodes.PLUS);
                bindButtonOnClick(view, R.id.button_minus, InputCodes.MINUS);
                bindButtonOnClick(view, R.id.button_times, InputCodes.TIMES);
                bindButtonOnClick(view, R.id.button_divide, InputCodes.DIVIDE);
                bindButtonOnClick(view, R.id.button_power, InputCodes.POWER);
                bindButtonOnClick(view, R.id.button_left_bracket, InputCodes.LEFT_BRACKET);
                bindButtonOnClick(view, R.id.button_right_bracket, InputCodes.RIGHT_BRACKET);
                break;
            default:
                Log.wtf(TAG, "Unknown mode: " + mode);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    public Mode getMode() {
        return mode;
    }

    /**
     * Helper method to sets the {@link android.view.View.OnClickListener} of the button with id to call
     * {@link OnButtonPressedListener#onButtonPressed(int)} with the given button code.
     */
    private void bindButtonOnClick(@NonNull final View view, @IdRes int id, final int buttonCode) throws NullPointerException {
        final Button button = view.findViewById(id);
        button.setOnClickListener(v -> listener.onButtonPressed(buttonCode));
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                listener.onButtonDown();
            }
            return false;
        });
    }

    public interface OnButtonPressedListener {
        /**
         * Called when a calculator button is pressed.
         *
         * @param buttonCode A code in {@link InputCodes}.
         */
        void onButtonPressed(int buttonCode);
        void onButtonDown();
    }
}
