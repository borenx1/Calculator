package com.bx.calculator.math;

/**
 * Thrown when the result of a calculation is undefined, eg. dividing by 0.
 */
public class UndefinedException extends ArithmeticException {

    public UndefinedException() {
        super();
    }

    public UndefinedException(String s) {
        super(s);
    }
}
