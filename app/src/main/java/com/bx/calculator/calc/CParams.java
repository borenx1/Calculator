package com.bx.calculator.calc;

import androidx.annotation.NonNull;

import com.bx.calculator.calc.math.AngleUnit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.obermuhlner.math.big.BigComplex;

/**
 * Value (immutable) class representing parameters for a calculation.
 */
public class CParams {

    private final AngleUnit angleUnit;
    /**
     * Map to replace a unit (variable) with a value.
     */
    private final Map<CUnit, BigComplex> variableMap = new HashMap<>();
    private Map<CUnit, BigComplex> unmodifiableVariableMap;

    /**
     * Constructor for a {@link CParams} instance. Represents parameters for a calculation.
     *
     * @param angleUnit Angle unit, {@link AngleUnit#RAD} or {@link AngleUnit#DEG}.
     * @param variables Variable and value pairs. Pairs with {@code null} keys or values are ignored.
     * @throws IllegalArgumentException A {@link CUnit} is not a variable.
     * @see CUnit#isVariable()
     * @see Calculate#calculate(CExpression, CParams)
     */
    public CParams(AngleUnit angleUnit, Map<CUnit, BigComplex> variables) throws IllegalArgumentException {
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

    /**
     * Constructor for a {@link CParams} instance with no variable mapping.
     *
     * @param angleUnit Angle unit, {@link AngleUnit#RAD} or {@link AngleUnit#DEG}.
     */
    public CParams(AngleUnit angleUnit) {
        this(angleUnit, null);
    }

    /**
     * Constructor for a default {@link CParams}. The angle unit is radians are there is no
     * variable mapping.
     */
    public CParams() {
        this(AngleUnit.RAD);
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

    public AngleUnit getAngleUnit() {
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
