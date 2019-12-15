package com.bx.calculator.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bx.calculator.InsertCUnitListener;
import com.bx.calculator.R;
import com.bx.calculator.calc.CNum;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.ui.Constant;
import com.bx.calculator.ui.ConstantAdapter;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ConstantInsertDialogFragment extends DialogFragment implements ConstantAdapter.OnConstantClickListener {

    private static final String TAG = "ConstantInsertDialogFragment";
    private static final String ARG_OPTION = "option";

    private DialogListener dialogListener;
    private InsertCUnitListener insertListener;
    /**
     * Index of constant option, from constant array, eg. maths, physics.
     */
    private int option;

    private RecyclerView recyclerView;
    private ConstantAdapter adapter;

    public static ConstantInsertDialogFragment getInstance(int option) {
        final ConstantInsertDialogFragment fragment = new ConstantInsertDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_OPTION, option);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Get an instance with {@link #getInstance(int)}.
     */
    public ConstantInsertDialogFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            option = getArguments().getInt(ARG_OPTION);
        } else {
            Log.wtf(TAG, "No arguments set.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        recyclerView = new RecyclerView(getContext());
        adapter = new ConstantAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        switch (option) {
            case 0:
                // maths
                adapter.submitList(Constant.getConstants(new CNum[] {
                        CNum.PI, CNum.TAU, CNum.E, CNum.GOLDEN_RATIO, CNum.FEIGENBAUM}, getContext()));
                break;
            case 1:
                // physics
                adapter.submitList(Constant.getConstants(new CNum[] {
                        CNum.SPEED_OF_LIGHT, CNum.G, CNum.PLANCK_CONSTANT, CNum.PLANCK_CONSTANT_REDUCED}, getContext()));
                break;
            case 2:
                // electromagnetic
                adapter.submitList(Constant.getConstants(new CNum[] {
                        CNum.ELEMENTARY_CHARGE, CNum.VACUUM_PERMEABILITY, CNum.VACUUM_PERMITTIVITY}, getContext()));
                break;
            case 3:
                // thermodynamic
                adapter.submitList(Constant.getConstants(new CNum[] {
                        CNum.BOLTZMANN_CONSTANT, CNum.STEFAN_BOLTZMANN_CONSTANT, CNum.GAS_CONSTANT, CNum.AVOGADRO_CONSTANT}, getContext()));
                break;
            case 4:
                // atomic
                adapter.submitList(Constant.getConstants(new CNum[] {
                        CNum.ELECTRON_MASS, CNum.PROTON_MASS, CNum.NEUTRON_MASS, CNum.BOHR_RADIUS, CNum.RYDBERG_CONSTANT, CNum.FINE_STRUCTURE_CONSTANT}, getContext()));
                break;
            case 5:
                // astronomical
                adapter.submitList(Constant.getConstants(new CNum[] {
                        CNum.ASTRONOMICAL_UNIT, CNum.LIGHT_YEAR, CNum.PARSEC, CNum.SOLAR_MASS, CNum.SOLAR_RADIUS, CNum.SOLAR_LUMINOSITY, CNum.SUN_TEMPERATURE,
                        CNum.EARTH_MASS, CNum.EARTH_RADIUS}, getContext()));
                break;
            case 6:
                // subatomic
                adapter.submitList(Constant.getConstants(new CNum[] {
                        CNum.PROTON_MASS_SUBATOMIC, CNum.NEUTRON_MASS_SUBATOMIC, CNum.ELECTRON_MASS_SUBATOMIC, CNum.MUON_MASS, CNum.TAU_MASS,
                        CNum.UP_MASS, CNum.DOWN_MASS, CNum.CHARM_MASS, CNum.STRANGE_MASS, CNum.TOP_MASS, CNum.BOTTOM_MASS, CNum.W_MASS, CNum.Z_MASS}, getContext()));
                break;
            default:
                Log.wtf(TAG, "Invalid constant option: " + option);
                break;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getStringArray(R.array.constants)[option])
                .setView(recyclerView)
                .setNeutralButton(R.string.back, (dialog, which) -> dialogListener.onDialogNeutralClick(ConstantInsertDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialogListener.onDialogNegativeClick(ConstantInsertDialogFragment.this));
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dialogListener = null;
        insertListener = null;
    }

    @Override
    public void onConstantClick(int position, Constant constant) {
        if (constant != null) {
            insertListener.insert(CUnit.get(constant.getSymbol()));
        } else {
            Log.wtf(TAG, "onConstantClick: constant is null.");
        }
        dismiss();
    }
}
