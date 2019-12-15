package com.bx.calculator.calc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ch.obermuhlner.math.big.BigComplex;

/**
 * Value class of parameters for calculations.
 */
public class CParams {

    public static final int ANGLE_RAD = 0;
    public static final int ANGLE_DEG = 1;

    private int angleUnit;
    /**
     * Map to replace a unit (variable) with a value.
     */
    private final Map<CUnit, BigComplex> variableMap = new HashMap<>();
    private Map<CUnit, BigComplex> unmodifiableVariableMap;

    /**
     * Constructor for a {@link CParams} instance. Represents parameters for a calculation.
     *
     * @param angleUnit Angle unit, {@link #ANGLE_RAD} or {@link #ANGLE_DEG}.
     * @param variables Variable and value pairs. Pairs with {@code null} keys or values are ignored.
     * @throws IllegalArgumentException A {@link CUnit} is not a variable.
     * @see CUnit#isVariable()
     * @see Calculate#calculate(CUnit[], CParams)
     */
    public CParams(int angleUnit, Map<CUnit, BigComplex> variables) throws IllegalArgumentException {
		// update equals method for adapter diff callback
        this.angleUnit = angleUnit;
        if (variables != null) {
            for (Map.Entry<CUnit, BigComplex> entry: variables.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    if (entry.getKey().isVariable()) {
                        variableMap.put(entry.getKey(), entry.getValue());
                    } else {
                        throw new IllegalArgumentException(entry.getKey() + " is not a variable.");
                    }
                }
            }
        }
    }

    public CParams(int angleUnit) {
        this(angleUnit, null);
    }

    public CParams() {
        this(ANGLE_RAD);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("CParams[angleUnit=%s, variableMap=%s]", angleUnit, variableMap);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CParams) {
            final CParams that = (CParams) obj;
            return angleUnit == that.angleUnit && variableMap.equals(that.variableMap);
        }
        return false;
    }

    public int getAngleUnit() {
        return angleUnit;
    }

    public BigComplex getValue(CUnit variable) {
        return variableMap.get(variable);
    }

    /**
     * @return Unmodifiable map of variables-value pairs.
     */
    public Map<CUnit, BigComplex> getVariableMap() {
        if (unmodifiableVariableMap == null) {
            unmodifiableVariableMap = Collections.unmodifiableMap(variableMap);
        }
        return unmodifiableVariableMap;
    }

    /**
     * @return Unmodifiable set of variables representing the key of variable-value map.
     */
    public Set<CUnit> getVariables() {
        return getVariableMap().keySet();
    }
}
