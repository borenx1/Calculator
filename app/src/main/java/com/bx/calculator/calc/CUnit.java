package com.bx.calculator.calc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Value class for a unit in a calculator input sequence.
 */
public class CUnit implements Serializable {

    public static final CUnit ZERO = new CUnit("0");
    public static final CUnit ONE = new CUnit("1");
    public static final CUnit TWO = new CUnit("2");
    public static final CUnit THREE = new CUnit("3");
    public static final CUnit FOUR = new CUnit("4");
    public static final CUnit FIVE = new CUnit("5");
    public static final CUnit SIX = new CUnit("6");
    public static final CUnit SEVEN = new CUnit("7");
    public static final CUnit EIGHT = new CUnit("8");
    public static final CUnit NINE = new CUnit("9");
    public static final CUnit POINT = new CUnit(".");
    public static final CUnit LEFT_BRACKET = new CUnit("(");
    public static final CUnit RIGHT_BRACKET = new CUnit(")");

    public static final CUnit PLUS = new CUnit("+");
    public static final CUnit MINUS = new CUnit("−");
    public static final CUnit TIMES = new CUnit("×");
    public static final CUnit DIVIDE = new CUnit("÷");
    public static final CUnit POWER = new CUnit("^");
    public static final CUnit EXP = new CUnit("ᴇ");
    public static final CUnit ROOT = new CUnit("<sup><small>n</sup></small>√");
    public static final CUnit NPR = new CUnit("<b>P</b>");
    public static final CUnit NCR = new CUnit("<b>C</b>");

    public static final CUnit ABS = new CUnit("abs");
    public static final CUnit ARG = new CUnit("arg");
    public static final CUnit CONJ = new CUnit("conj");
    public static final CUnit LOG = new CUnit("ln");
    public static final CUnit LOG10 = new CUnit("log");
    public static final CUnit LOG2 = new CUnit("log<sub><small>2</small></sub>");
    public static final CUnit SQRT = new CUnit("√");
    public static final CUnit SIN = new CUnit("sin");
    public static final CUnit COS = new CUnit("cos");
    public static final CUnit TAN = new CUnit("tan");
    public static final CUnit CSC = new CUnit("csc");
    public static final CUnit SEC = new CUnit("sec");
    public static final CUnit COT = new CUnit("cot");
    public static final CUnit ASIN = new CUnit("sin<sup><small>-1</small></sup>");
    public static final CUnit ACOS = new CUnit("cos<sup><small>-1</small></sup>");
    public static final CUnit ATAN = new CUnit("tan<sup><small>-1</small></sup>");
    public static final CUnit SINH = new CUnit("sinh");
    public static final CUnit COSH = new CUnit("cosh");
    public static final CUnit TANH = new CUnit("tanh");
    public static final CUnit ASINH = new CUnit("sinh<sup><small>-1</small></sup>");
    public static final CUnit ACOSH = new CUnit("cosh<sup><small>-1</small></sup>");
    public static final CUnit ATANH = new CUnit("tanh<sup><small>-1</small></sup>");

    public static final CUnit FACTORIAL = new CUnit("!");
    public static final CUnit PERCENT = new CUnit("%");
    public static final CUnit SQUARED = new CUnit("<sup><small>2</small></sup>");
    public static final CUnit CUBED = new CUnit("<sup><small>3</small></sup>");
    public static final CUnit INVERSE = new CUnit("<sup><small>-1</small></sup>");

    public static final CUnit ANS = new CUnit("Ans");
    public static final CUnit X = new CUnit("X");
    public static final CUnit Y = new CUnit("Y");
    public static final CUnit Z = new CUnit("Z");
    public static final CUnit A = new CUnit("A");
    public static final CUnit B = new CUnit("B");
    public static final CUnit C = new CUnit("C");
    public static final CUnit D = new CUnit("D");
    public static final CUnit ALPHA = new CUnit("α");
    public static final CUnit BETA = new CUnit("β");
    public static final CUnit GAMMA = new CUnit("γ");

    public static final List<CUnit> variables;
    static {
        variables = Collections.unmodifiableList(Arrays.asList(
                X, Y, Z, A, B, C, D, ALPHA, BETA, GAMMA
        ));
    }

    public static final List<CUnit> preFunctions;
    static {
        preFunctions = Collections.unmodifiableList(Arrays.asList(
                ABS, ARG, CONJ, SQRT, LOG, LOG10, LOG2, SIN, COS, TAN,
                ASIN, ACOS, ATAN, SINH, COSH, TANH, ASINH, ACOSH, ATANH, CSC, SEC, COT
        ));
    }

    public static final List<CUnit> postFunctions;
    static {
        postFunctions = Collections.unmodifiableList(Arrays.asList(
                FACTORIAL, PERCENT, SQUARED, CUBED, INVERSE
        ));
    }

    private static Map<String, CUnit> unitMap;

    /**
     * @param display display string
     * @return unique {@link CUnit} with the given {@code display}, or {@code null} if it does not exist
     */
    @Nullable
    public static CUnit get(@Nullable String display) {
        if (unitMap == null) {
            final Map<String, CUnit> tempMap = new HashMap<>();
            Arrays.stream(new CUnit[] {
                    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE,
                    POINT, LEFT_BRACKET, RIGHT_BRACKET,
                    PLUS, MINUS, TIMES, DIVIDE, POWER, EXP, ROOT, NPR, NCR,
                    ANS, CNum.I,
            }).forEach(unit -> tempMap.put(unit.toString(), unit));
            variables.forEach(unit -> tempMap.put(unit.toString(), unit));
            preFunctions.forEach(unit -> tempMap.put(unit.toString(), unit));
            postFunctions.forEach(unit -> tempMap.put(unit.toString(), unit));
            CNum.constants.forEach(unit -> tempMap.put(unit.toString(), unit));
            unitMap = Collections.unmodifiableMap(tempMap);
        }
        return unitMap.get(display);
    }

    private final String rawDisplay;
    private final Spanned display;

    /**
     * Constructor for {@link CUnit}.
     *
     * @param display string that is displayed on the screen (output)
     * @throws IllegalArgumentException {@code display} is an empty string
     */
    protected CUnit(@NonNull String display) throws IllegalArgumentException {
        this.rawDisplay = display;
        this.display = Html.fromHtml(display, Html.FROM_HTML_MODE_LEGACY);
        if (this.display.toString().isEmpty()) {
            throw new IllegalArgumentException("Display is empty.");
        }
    }

    /**
     * @return Non stylized display string with html markup.
     */
    @NonNull
    @Override
    public String toString() {
        return rawDisplay;
}

    /**
     * @return String displayed on the screen (output).
     */
    @NonNull
    public Spanned toDisplayString() {
        return display;
    }

    /**
     * @return length of the string on the display
     */
    public int length() {
        return display.length();
    }

    public boolean isDigit() {
        return this == ZERO || this == ONE || this == TWO || this == THREE || this == FOUR
                || this == FIVE || this == SIX || this == SEVEN || this == EIGHT || this == NINE;
    }

    public boolean isDigitOrPoint() {
        return isDigit() || this == POINT;
    }

    public boolean isPlusOrMinus() {
        return this == CUnit.PLUS || this == CUnit.MINUS;
    }

    public boolean isDigitOrPlusOrMinus() {
        return isDigit() || isPlusOrMinus();
    }

    public boolean isTimesOrDivide() {
        return this == CUnit.TIMES || this == CUnit.DIVIDE;
    }

    public boolean isPermutationOrCombination() {
        return this == NPR || this == NCR;
    }

    public boolean isOperator() {
        return this == PLUS || this == MINUS || this == TIMES || this == DIVIDE ||
                this == POWER || this == EXP || this == ROOT || this == NPR || this == NCR;
    }

    public boolean isPreFunction() {
        return preFunctions.contains(this);
    }

    public boolean isPostFunction() {
        return postFunctions.contains(this);
    }

    public boolean isVariable() {
        return this == ANS || variables.contains(this);
    }
}
