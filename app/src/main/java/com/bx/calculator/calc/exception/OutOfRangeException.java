package com.bx.calculator.calc.exception;

import com.bx.calculator.calc.Calculate;

import ch.obermuhlner.math.big.BigComplex;

/**
 * Thrown when a value that is out of range and cannot be handled occurs during a calculation.
 *
 * @see Calculate#isInRange(BigComplex)
 */
public class OutOfRangeException extends ArithmeticException {

    public OutOfRangeException() {
        super();
    }

    public OutOfRangeException(String s) {
        super(s);
    }
}
