package com.bx.calculator.calc.exception;

/**
 * Thrown when there is a syntax error in the {@link com.bx.calculator.calc.CSequence}.
 */
public class SyntaxException extends ArithmeticException {

    public SyntaxException() {
        super();
    }

    public SyntaxException(String s) {
        super(s);
    }
}
