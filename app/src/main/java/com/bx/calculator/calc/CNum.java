package com.bx.calculator.calc;

import com.bx.calculator.calc.math.Maffs;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

import ch.obermuhlner.math.big.BigComplex;

/**
 * A {@link CUnit} with a number value. Not inputted by user, but instead created during
 * calculations for calculations.
 */
public class CNum extends CUnit {

    public static final String DEFAULT_DISPLAY = "NUM";

    // I does not count as a constant, add manually to CUnit.unitMap
    public static final CNum I = new CNum("i", BigComplex.I, false);
    public static final CNum PI = new CNum("π", BigComplex.valueOf(Maffs.PI), false);
    public static final CNum TAU = new CNum("τ", BigComplex.valueOf(Maffs.TAU), false);
    public static final CNum E = new CNum("e", BigComplex.valueOf(Maffs.E), false);
    public static final CNum GOLDEN_RATIO = new CNum("ϕ", BigComplex.valueOf(new BigDecimal("1.618033988749894848204586834")), false);
    public static final CNum FEIGENBAUM = new CNum("δ", BigComplex.valueOf(new BigDecimal("4.669201609102990671853")), false);
    public static final CNum G = new CNum("<i>G</i>", BigComplex.valueOf(new BigDecimal("6.67408e-11")), false);
    public static final CNum SPEED_OF_LIGHT = new CNum("<i>c</i>", BigComplex.valueOf(299792458), false);
    public static final CNum PLANCK_CONSTANT = new CNum("<i>h</i>", BigComplex.valueOf(new BigDecimal("6.62607015e-34")), false);
    public static final CNum PLANCK_CONSTANT_REDUCED = new CNum("ℏ",
            Maffs.divide(PLANCK_CONSTANT.getNum(), Maffs.multiply(Maffs.TWO, BigComplex.valueOf(Maffs.PI))), false);
    public static final CNum ELEMENTARY_CHARGE = new CNum("<i>e</i>", BigComplex.valueOf(new BigDecimal("1.602176634e-19")), false);
    public static final CNum VACUUM_PERMEABILITY = new CNum("<i>μ<sub><small>0</small></sub></i>", BigComplex.valueOf(new BigDecimal("1.2566370614e-6")), false);
    public static final CNum VACUUM_PERMITTIVITY = new CNum("<i>ε<sub><small>0</small></sub></i>", BigComplex.valueOf(new BigDecimal("8.854187817e-12")), false);
    public static final CNum BOLTZMANN_CONSTANT = new CNum("<i>k<sub><small>B</small></sub></i>", BigComplex.valueOf(new BigDecimal("1.380649e-23")), false);
    public static final CNum STEFAN_BOLTZMANN_CONSTANT = new CNum("<i>σ</i>", BigComplex.valueOf(new BigDecimal("5.670367e-8")), false);
    public static final CNum GAS_CONSTANT = new CNum("<i>R</i>", BigComplex.valueOf(new BigDecimal("8.3144598")), false);
    public static final CNum AVOGADRO_CONSTANT = new CNum("<i>N<sub><small>A</small></sub></i>", BigComplex.valueOf(new BigDecimal("6.02214076e23")), false);
    public static final CNum ELECTRON_MASS = new CNum("<i>m<sub><small>e</small></sub></i>", BigComplex.valueOf(new BigDecimal("9.10938356e-31")), false);
    public static final CNum PROTON_MASS = new CNum("<i>m<sub><small>p</small></sub></i>", BigComplex.valueOf(new BigDecimal("1.672621898e-27")), false);
    public static final CNum NEUTRON_MASS = new CNum("<i>m<sub><small>n</small></sub></i>", BigComplex.valueOf(new BigDecimal("1.674927471e-27")), false);
    public static final CNum BOHR_RADIUS = new CNum("<i>a<sub><small>0</small></sub></i>", BigComplex.valueOf(new BigDecimal("5.2917721067e-11")), false);
    public static final CNum RYDBERG_CONSTANT = new CNum("<i>R<sub><small>∞</small></sub></i>", BigComplex.valueOf(new BigDecimal("10973731.568508")), false);
    public static final CNum FINE_STRUCTURE_CONSTANT = new CNum("<i>α</i>", BigComplex.valueOf(new BigDecimal("0.0072973525664")), false);
    public static final CNum ASTRONOMICAL_UNIT = new CNum("au", BigComplex.valueOf(new BigDecimal("149597870700")), false);
    public static final CNum LIGHT_YEAR = new CNum("ly", BigComplex.valueOf(new BigDecimal("9460730472580800")), false);
    public static final CNum PARSEC = new CNum("pc", BigComplex.valueOf(new BigDecimal("30856775814913673")), false);
    public static final CNum SOLAR_MASS = new CNum("<i>M</i><sub><small>☉</small></sub>", BigComplex.valueOf(new BigDecimal("1.98847e30")), false);
    public static final CNum SOLAR_RADIUS = new CNum("<i>R</i><sub><small>☉</small></sub>", BigComplex.valueOf(new BigDecimal("6.95700e8")), false);
    public static final CNum SOLAR_LUMINOSITY = new CNum("<i>L</i><sub><small>☉</small></sub>", BigComplex.valueOf(new BigDecimal("3.828e26")), false);
    public static final CNum SUN_TEMPERATURE = new CNum("<i>T</i><sub><small>☉</small></sub>", BigComplex.valueOf(new BigDecimal("5772")), false);
    public static final CNum EARTH_MASS = new CNum("<i>M</i><sub><small>⊕</small></sub>", BigComplex.valueOf(new BigDecimal("5.9722e24")), false);
    public static final CNum EARTH_RADIUS = new CNum("<i>R</i><sub><small>⊕</small></sub>", BigComplex.valueOf(new BigDecimal("6.3781e6")), false);
    public static final CNum PROTON_MASS_SUBATOMIC = new CNum("<i>m<sub><small>p<sup><small>±</small></sup></small></sub></i>", BigComplex.valueOf(new BigDecimal("938.2720813")), false);
    public static final CNum NEUTRON_MASS_SUBATOMIC = new CNum("<i>m<sub><small>n<sup><small>0</small></sup></small></sub></i>", BigComplex.valueOf(new BigDecimal("939.5654133")), false);
    public static final CNum ELECTRON_MASS_SUBATOMIC = new CNum("<i>m<sub><small>e<sup><small>±</small></sup></small></sub></i>", BigComplex.valueOf(new BigDecimal("0.5109989461")), false);
    public static final CNum MUON_MASS = new CNum("<i>m<sub><small>μ<sup><small>±</small></sup></small></sub></i>", BigComplex.valueOf(new BigDecimal("105.6583745")), false);
    public static final CNum TAU_MASS = new CNum("<i>m<sub><small>τ<sup><small>±</small></sup></small></sub></i>", BigComplex.valueOf(new BigDecimal("1776.82")), false);
    public static final CNum UP_MASS = new CNum("<i>m<sub><small>u</small></sub></i>", BigComplex.valueOf(new BigDecimal("2.01")), false);
    public static final CNum DOWN_MASS = new CNum("<i>m<sub><small>d</small></sub></i>", BigComplex.valueOf(new BigDecimal("4.79")), false);
    public static final CNum CHARM_MASS = new CNum("<i>m<sub><small>c</small></sub></i>", BigComplex.valueOf(new BigDecimal("1280")), false);
    public static final CNum STRANGE_MASS = new CNum("<i>m<sub><small>s</small></sub></i>", BigComplex.valueOf(new BigDecimal("93.8")), false);
    public static final CNum TOP_MASS = new CNum("<i>m<sub><small>t</small></sub></i>", BigComplex.valueOf(new BigDecimal("172440")), false);
    public static final CNum BOTTOM_MASS = new CNum("<i>m<sub><small>b</small></sub></i>", BigComplex.valueOf(new BigDecimal("4180")), false);
    public static final CNum W_MASS = new CNum("<i>m<sub><small>W<sup><small>±</small></sup></small></sub></i>", BigComplex.valueOf(new BigDecimal("80385")), false);
    public static final CNum Z_MASS = new CNum("<i>m<sub><small>Z<sup><small>0</small></sup></small></sub></i>", BigComplex.valueOf(new BigDecimal("91187.6")), false);

    public static final List<CNum> constants;
    static {
        constants = Collections.unmodifiableList(Arrays.asList(PI, TAU, E, GOLDEN_RATIO, FEIGENBAUM,
                G, SPEED_OF_LIGHT, PLANCK_CONSTANT, PLANCK_CONSTANT_REDUCED,
                ELEMENTARY_CHARGE, VACUUM_PERMEABILITY, VACUUM_PERMITTIVITY,
                BOLTZMANN_CONSTANT, STEFAN_BOLTZMANN_CONSTANT, GAS_CONSTANT, AVOGADRO_CONSTANT,
                ELECTRON_MASS, PROTON_MASS, NEUTRON_MASS, BOHR_RADIUS, RYDBERG_CONSTANT, FINE_STRUCTURE_CONSTANT,
                ASTRONOMICAL_UNIT, LIGHT_YEAR, PARSEC, SOLAR_MASS, SOLAR_RADIUS, SOLAR_LUMINOSITY, SUN_TEMPERATURE, EARTH_MASS, EARTH_RADIUS,
                PROTON_MASS_SUBATOMIC, NEUTRON_MASS_SUBATOMIC, ELECTRON_MASS_SUBATOMIC, MUON_MASS, TAU_MASS,
                UP_MASS, DOWN_MASS, CHARM_MASS, STRANGE_MASS, TOP_MASS, BOTTOM_MASS, W_MASS, Z_MASS));
    }

    private final BigComplex num;
    private final boolean isCombinedFromDigits;
    
    protected CNum(@NonNull String display, @NonNull BigComplex num, boolean isCombinedFromDigits) throws NullPointerException {
        super(display);
        this.num = Objects.requireNonNull(num);
        this.isCombinedFromDigits = isCombinedFromDigits;
    }

    CNum(@NonNull BigComplex num, boolean isCombinedFromDigits) {
        this(DEFAULT_DISPLAY, num, isCombinedFromDigits);
    }

    CNum(@NonNull BigDecimal num, boolean isCombinedFromDigits) {
        this(BigComplex.valueOf(num), isCombinedFromDigits);
    }

    CNum(@NonNull BigComplex num) {
        this(num, false);
    }

    CNum(@NonNull BigDecimal num) {
        this(num, false);
    }

    /**
     * Constructor for a {@link CNum} with a real part of {@code num} and {@link #isCombinedFromDigits()} set to {@code true}.
     *
     * @param num String representation of the real part.
     * @throws NumberFormatException {@code num} is not a valid string representation of a big decimal.
     */
    CNum(@NonNull String num) throws NumberFormatException {
        this(new BigDecimal(num), true);
    }

    @NonNull
    public BigComplex getNum() {
        return num;
    }

    boolean isCombinedFromDigits() {
        return isCombinedFromDigits;
    }

    @NonNull
    BigComplex getSignedNum(@NonNull CUnit pm) throws IllegalArgumentException {
        if (pm == CUnit.PLUS) {
            return num;
        } else if (pm == CUnit.MINUS) {
            return num.negate();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
