package com.bx.calculator.calc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ch.obermuhlner.math.big.BigComplex;

/**
 * A class representing the result of a calculation. Contains the input, answer and the parameters. The accuracy of the answer
 * depends on the calculation method, so it is not guaranteed to be correct.
 */
public class CResult {

    private final CUnit[] input;
    private final BigComplex answer;
    private final CParams params;

    CResult(CUnit[] input, BigComplex answer, CParams params) throws NullPointerException {
        this.input = Arrays.copyOf(input, input.length);
        this.answer = Objects.requireNonNull(answer);
        this.params = params;
    }

    CResult(@NonNull CUnit[] input, @NonNull BigDecimal answerRe, @NonNull BigDecimal answerIm, @NonNull CParams params) {
        this(input, BigComplex.valueOf(answerRe, answerIm), params);
    }

    @Override
    public String toString() {
        return String.format("CResult[input=%s, answer=%s, params=%s]", Arrays.toString(input), answer, params);
    }

    /**
     * @return The input of the calculation. This instance is tied to the original input parameter
     * of {@link Calculate#calculate(CUnit[], CParams)}.
     */
    public CUnit[] getInput() {
        return input;
    }

    public BigComplex getAnswer() {
        return answer;
    }

    public CParams getParams() {
        return params;
    }
}
