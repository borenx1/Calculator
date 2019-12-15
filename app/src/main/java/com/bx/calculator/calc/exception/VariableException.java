package com.bx.calculator.calc.exception;

import com.bx.calculator.calc.CParams;
import com.bx.calculator.calc.CUnit;

/**
 * Thrown when a sequence with {@link CUnit#isVariable()} is calculated but
 * the {@link CParams} does not contain a value for it.
 */
public class VariableException extends IllegalStateException {

    public VariableException() {
        super();
    }

    public VariableException(String s) {
        super(s);
    }

    public VariableException(String message, Throwable cause) {
        super(message, cause);
    }

    public VariableException(Throwable cause) {
        super(cause);
    }
}
