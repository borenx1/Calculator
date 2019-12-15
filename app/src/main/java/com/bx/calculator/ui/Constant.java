package com.bx.calculator.ui;

import android.content.Context;

import com.bx.calculator.R;
import com.bx.calculator.calc.CNum;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import ch.obermuhlner.math.big.BigComplex;

public class Constant {

    final private String name;
    final private CNum value;
    final private String unit;

    public Constant(@NonNull String name, @NonNull CNum value, @NonNull String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public static Constant getConstant(@NonNull CNum constant, @NonNull Context context) {
        if (constant == CNum.PI) {
            return new Constant(context.getString(R.string.dialog_constant_pi),
                    constant, "");
        } else if (constant == CNum.TAU) {
            return new Constant(context.getString(R.string.dialog_constant_tau),
                    constant, "");
        } else if (constant == CNum.E) {
            return new Constant(context.getString(R.string.dialog_constant_e),
                    constant, "");
        }else if (constant == CNum.GOLDEN_RATIO) {
            return new Constant(context.getString(R.string.dialog_constant_golden_ratio),
                    constant, "");
        } else if (constant == CNum.FEIGENBAUM) {
            return new Constant(context.getString(R.string.dialog_constant_feigenbaum),
                    constant, "");
        }  else if (constant == CNum.SPEED_OF_LIGHT) {
            return new Constant(context.getString(R.string.dialog_constant_speed_of_light),
                    constant, "m·s<sup><small>-1</small></sup>");
        } else if (constant == CNum.G) {
            return new Constant(context.getString(R.string.dialog_constant_gravitational),
                    constant, "m<sup><small>3</small></sup>·kg<sup><small>-1</small></sup>·s<sup><small>-2</small></sup>");
        } else if (constant == CNum.PLANCK_CONSTANT) {
            return new Constant(context.getString(R.string.dialog_constant_planck_constant),
                    constant, "J·s");
        } else if (constant == CNum.PLANCK_CONSTANT_REDUCED) {
            return new Constant(context.getString(R.string.dialog_constant_planck_constant_reduced),
                    constant, "J·s·rad<sup><small>-1</small></sup>");
        } else if (constant == CNum.ELEMENTARY_CHARGE) {
            return new Constant(context.getString(R.string.dialog_constant_elementary_charge),
                    constant, "C");
        } else if (constant == CNum.VACUUM_PERMEABILITY) {
            return new Constant(context.getString(R.string.dialog_constant_vacuum_permeability),
                    constant, "H·m<sup><small>-1</small></sup>");
        } else if (constant == CNum.VACUUM_PERMITTIVITY) {
            return new Constant(context.getString(R.string.dialog_constant_vacuum_permittivity),
                    constant, "F·m<sup><small>-1</small></sup>");
        } else if (constant == CNum.BOLTZMANN_CONSTANT) {
            return new Constant(context.getString(R.string.dialog_constant_boltzmann_constant),
                    constant, "J·K<sup><small>-1</small></sup>");
        } else if (constant == CNum.STEFAN_BOLTZMANN_CONSTANT) {
            return new Constant(context.getString(R.string.dialog_constant_stefan_boltzmann_constant),
                    constant, "W·m<sup><small>-2</small></sup>·K<sup><small>-4</small></sup>");
        } else if (constant == CNum.GAS_CONSTANT) {
            return new Constant(context.getString(R.string.dialog_constant_gas_constant),
                    constant, "J·mol<sup><small>-1</small></sup>·K<sup><small>-1</small></sup>");
        } else if (constant == CNum.AVOGADRO_CONSTANT) {
            return new Constant(context.getString(R.string.dialog_constant_avogadro_constant),
                    constant, "mol<sup><small>-1</small></sup>");
        } else if (constant == CNum.ELECTRON_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_electron_mass),
                    constant, "kg");
        } else if (constant == CNum.PROTON_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_proton_mass),
                    constant, "kg");
        } else if (constant == CNum.NEUTRON_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_neutron_mass),
                    constant, "kg");
        } else if (constant == CNum.BOHR_RADIUS) {
            return new Constant(context.getString(R.string.dialog_constant_bohr_radius),
                    constant, "m");
        } else if (constant == CNum.RYDBERG_CONSTANT) {
            return new Constant(context.getString(R.string.dialog_constant_rydberg_constant),
                    constant, "m<sup><small>-1</small></sup>");
        } else if (constant == CNum.FINE_STRUCTURE_CONSTANT) {
            return new Constant(context.getString(R.string.dialog_constant_fine_structure_constant),
                    constant, "");
        } else if (constant == CNum.ASTRONOMICAL_UNIT) {
            return new Constant(context.getString(R.string.dialog_constant_astronomical_unit),
                    constant, "m");
        } else if (constant == CNum.LIGHT_YEAR) {
            return new Constant(context.getString(R.string.dialog_constant_light_year),
                    constant, "m");
        } else if (constant == CNum.PARSEC) {
            return new Constant(context.getString(R.string.dialog_constant_parsec),
                    constant, "m");
        } else if (constant == CNum.SOLAR_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_solar_mass),
                    constant, "kg");
        } else if (constant == CNum.SOLAR_RADIUS) {
            return new Constant(context.getString(R.string.dialog_constant_solar_radius),
                    constant, "m");
        } else if (constant == CNum.SOLAR_LUMINOSITY) {
            return new Constant(context.getString(R.string.dialog_constant_solar_luminosity),
                    constant, "W");
        } else if (constant == CNum.SUN_TEMPERATURE) {
            return new Constant(context.getString(R.string.dialog_constant_sun_temperature),
                    constant, "K");
        } else if (constant == CNum.EARTH_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_earth_mass),
                    constant, "kg");
        } else if (constant == CNum.EARTH_RADIUS) {
            return new Constant(context.getString(R.string.dialog_constant_earth_radius),
                    constant, "m");
        } else if (constant == CNum.PROTON_MASS_SUBATOMIC) {
            return new Constant(context.getString(R.string.dialog_constant_proton_mass_subatomic),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.NEUTRON_MASS_SUBATOMIC) {
            return new Constant(context.getString(R.string.dialog_constant_neutron_mass_subatomic),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.ELECTRON_MASS_SUBATOMIC) {
            return new Constant(context.getString(R.string.dialog_constant_electron_mass_subatomic),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.MUON_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_muon_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.TAU_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_tau_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.UP_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_up_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.DOWN_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_down_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.CHARM_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_charm_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.STRANGE_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_strange_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.TOP_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_top_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.BOTTOM_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_bottom_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.W_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_w_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else if (constant == CNum.Z_MASS) {
            return new Constant(context.getString(R.string.dialog_constant_z_mass),
                    constant, "Mev·c<sup><small>-2</small></sup>");
        } else {
            throw new IllegalArgumentException("Unknown constant: " + constant);
        }
    }

    public static List<Constant> getConstants(@NonNull CNum[] constants, @NonNull Context context) {
        final List<Constant> constantList = new ArrayList<>();
        for (final CNum c: constants){
            constantList.add(getConstant(c, context));
        }
        return constantList;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getSymbol() {
        return value.toString();
    }

    @NonNull
    public BigComplex getValue() {
        return value.getNum();
    }

    @NonNull
    public String getUnit() {
        return unit;
    }

    @NonNull
    public CNum getCUnit() {
        return value;
    }
}