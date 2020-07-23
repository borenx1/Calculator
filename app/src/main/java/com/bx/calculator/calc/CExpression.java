package com.bx.calculator.calc;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A value (immutable) class representing a mathematical expression. It includes an array of {@link CUnit}s and
 * calculating parameters such as angle units.
 */
public class CExpression implements Iterable<CUnit> {

    private final List<CUnit> expression;

    /**
     * Constructor for {@link CExpression}. This object and the argument are not linked.
     *
     * @param units Units in the expression.
     * @throws IllegalArgumentException The expression contains {@code null}.
     * @throws NullPointerException The argument is {@code null}.
     */
    public CExpression(CUnit... units) throws IllegalArgumentException, NullPointerException {
        if (units == null) {
            throw new NullPointerException();
        }
        this.expression = Arrays.asList(units);
        // Exception if expression contains a null unit.
        if (this.expression.contains(null)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Constructor for {@link CExpression}.
     *
     * @param units Units in the expression. This object and the argument are not linked.
     * @throws IllegalArgumentException The expression contains {@code null}.
     * @throws NullPointerException The argument is {@code null}.
     */
    public CExpression(Collection<CUnit> units) throws IllegalArgumentException, NullPointerException {
        if (units == null) {
            throw new NullPointerException();
        }
        // Exception if expression contains a null unit.
        if (units.contains(null)) {
            throw new IllegalArgumentException();
        }
        this.expression = new ArrayList<>(units);
    }

    /**
     * Creates a copy of the an expression that does not change when the original expression changes.
     * @param expression Expression to copy.
     */
    public CExpression(CExpression expression) {
        this(expression.expression);
    }

    @NonNull
    @Override
    public Iterator<CUnit> iterator() {
        return expression.iterator();
    }

    @NonNull
    @Override
    public String toString() {
        return "CExpression" + expression.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CExpression) {
            final CExpression that = (CExpression) obj;
            return expression.equals(that.expression);
        }
        return false;
    }

    public CUnit get(int index) {
        return expression.get(index);
    }

    public int size() {
        return expression.size();
    }

    public List<CUnit> toList() {
        return new ArrayList<>(expression);
    }

    public CUnit[] toArray() {
        return expression.toArray(new CUnit[] {});
    }
}
