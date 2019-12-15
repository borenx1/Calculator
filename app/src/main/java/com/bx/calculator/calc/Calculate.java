package com.bx.calculator.calc;

import com.bx.calculator.calc.exception.OutOfRangeException;
import com.bx.calculator.calc.exception.VariableException;
import com.bx.calculator.calc.exception.SyntaxException;
import com.bx.calculator.math.AngleUnit;
import com.bx.calculator.math.Maffs;
import com.bx.calculator.math.UndefinedException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

import ch.obermuhlner.math.big.BigComplex;
import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * Contains static methods for calculations.
 */
public final class Calculate {

    public static final int MAX_EXPONENT = 9999;

    /**
     * <p>Calculates the result of a sequence.</p>
     * Method:
     * <ol>
     *     <li>Check if input sequence is empty.</li>
     *     <li>Check if input sequence contains a {@link CUnit#isVariable()} and if {@code params} has a value for it.</li>
     *     <li>Catch syntax errors early:<ol type="a">
     *         <li>{@link CUnit#RIGHT_BRACKET} at beginning or {@link CUnit#LEFT_BRACKET} at end.</li>
     *         <li>{@link CUnit#isOperator()} at end.</li>
     *         <li>{@link CUnit#isOperator()} at beginning that is not {@link CUnit#PLUS} or {@link CUnit#MINUS}.</li>
     *         <li>{@link CUnit#isPostFunction()} at beginning or {@link CUnit#isPreFunction()} at end.</li>
     *         <li>Consecutive {@link CUnit#POINT}s.</li>
     *         <li>Contains empty brackets '()'.</li>
     *         <li>Non-digit or non-point before {@link CUnit#PERCENT}.</li>
     *         <li>Non-digit or non-point before {@link CUnit#EXP}.</li>
     *         <li>Non-digit or non plus or minus following {@link CUnit#EXP}.</li>
     *         <li>If plus or minus following {@link CUnit#EXP}, check if the following is an integer made up of digits.</li>
     *     </ol></li>
     *     <li>Prepare the input using {@link #prepareSequence(CUnit[], CParams)}.</li>
     *     <li>Calculate result using {@link #calculate2(CUnit[], CParams)}.</li>
     *     <li>Round the result with {@link Maffs#round(BigComplex)}.</li>
     * </ol>
     *
     * @param input Sequence of {@link CUnit}s to calculate.
     * @param params Parameters of this calculation.
     * @return Result of calculation.
     * @throws NullPointerException {@code input} or a {@link CUnit} is {@code null}.
     * @throws VariableException Sequence contains {@link CUnit#isVariable()} and {@code params} does not contain a value for it.
     * @throws UndefinedException result is undefined
     * @throws SyntaxException syntax is wrong
     * @throws OutOfRangeException result is out of range
     * @see #prepareSequence(CUnit[], CParams)
     * @see #calculate2(CUnit[], CParams)
     * @see #isInRange(BigComplex)
     */
    @NonNull
    public static CResult calculate(@NonNull final CUnit[] input, @NonNull final CParams params)
            throws NullPointerException, VariableException, UndefinedException, SyntaxException, OutOfRangeException {
        // 1. Empty sequence
        if (input.length == 0) {
            throw new SyntaxException();
        }
        // 2. Variable null
        for (CUnit u: input) {
            if (u.isVariable() && params.getValue(u) == null) {
                throw new VariableException();
            }
        }
        // 3. Syntax errors
        if (input[0] == CUnit.RIGHT_BRACKET || input[input.length - 1] == CUnit.LEFT_BRACKET) {
            throw new SyntaxException();
        }
        if (input[input.length - 1].isOperator()) {
            throw new SyntaxException();
        }
        if (input[0].isOperator() && !input[0].isPlusOrMinus()) {
            throw new SyntaxException();
        }
        if (input[0].isPostFunction() || input[input.length - 1].isPreFunction()) {
            throw new SyntaxException();
        }
        for (int i = 0; i < input.length - 1; i++) {
            final CUnit u1 = input[i];
            final CUnit u2 = input[i + 1];
            if (u1 == CUnit.POINT && u2 == CUnit.POINT) {
                throw new SyntaxException();
            }
            if (u1 == CUnit.LEFT_BRACKET && u2 == CUnit.RIGHT_BRACKET) {
                throw new SyntaxException();
            }
            if (u2 == CUnit.PERCENT && !u1.isDigitOrPoint()) {
                throw new SyntaxException();
            }
            if (u2 == CUnit.EXP && !u1.isDigitOrPoint()) {
                throw new SyntaxException();
            }
            if (u1 == CUnit.EXP && !u2.isDigitOrPlusOrMinus()) {
                throw new SyntaxException();
            }
            if (u1 == CUnit.EXP && u2.isPlusOrMinus()) {
                for (int j = 2; i + j < input.length; j++) {
                    if (input[i + j].isDigit()) {
                        break;
                    } else if (!input[i + j].isPlusOrMinus()) {
                        throw new SyntaxException();
                    }
                }
            }
        }
        // 4. Prepare
        final CUnit[] prepared = prepareSequence(input, params);
        // 4. Calculate
        final BigComplex resultNumber = requireInRange(calculate2(prepared, params));
        // 5. Round
        final BigComplex rounded = Maffs.round(resultNumber);
        return new CResult(input, rounded, params);
    }

    @NonNull
    public static BigComplex calculateOperator(@NonNull final CUnit op, @NonNull final BigComplex n1, @NonNull final BigComplex n2)
            throws NullPointerException, OutOfRangeException, UndefinedException {
        final BigComplex result;
        if (op == CUnit.PLUS) {
            result = Maffs.add(n1, n2);
        } else if (op == CUnit.MINUS) {
            result = Maffs.subtract(n1, n2);
        } else if (op == CUnit.TIMES) {
            result = Maffs.multiply(n1, n2);
        } else if (op == CUnit.DIVIDE) {
            result = Maffs.divide(n1, n2);
        } else if (op == CUnit.POWER) {
            result = Maffs.pow(n1, n2);
        } else if (op == CUnit.EXP) {
            if (!n2.isReal()) {
                throw new UndefinedException();
            } else if (!isInteger(n2.re)) {
                throw new UndefinedException();
            } else if (!BigDecimalMath.isIntValue(n2.re)) { // is integer but not int
                throw new OutOfRangeException();
            }
            final int exponent = n2.re.intValueExact();
            if (!isExponentInRange(exponent)) {         // exponent > 9999
                throw new OutOfRangeException();
            }
            result = Maffs.scaleByPowerOfTen(n1, exponent);
        } else if (op == CUnit.ROOT) {
            result = Maffs.root(n2, n1);
        } else if (op == CUnit.NPR) {
            if (n1.isReal() && n2.isReal()) {
                result = BigComplex.valueOf(Maffs.permutation(n1.re, n2.re));
            } else {
                throw new UndefinedException();
            }
        } else if (op == CUnit.NCR) {
            if (n1.isReal() && n2.isReal()) {
                result = BigComplex.valueOf(Maffs.combination(n1.re, n2.re));
            } else {
                throw new UndefinedException();
            }
        } else {
            throw new IllegalArgumentException("Unrecognized operator: " + op);
        }
        return requireInRange(Maffs.round(result));
    }

    @NonNull
    public static BigComplex calculateFunction(@NonNull final CUnit func, @NonNull final BigComplex n, final int angleUnit)
            throws NullPointerException, UndefinedException, OutOfRangeException {
        final AngleUnit mappedAngleUnit = angleUnit == CParams.ANGLE_RAD ? AngleUnit.RAD : AngleUnit.DEG;
        // TODO set boundaries for factorial, sinh etc.
        final BigComplex result;
        if (func == CUnit.PLUS) {
            result = n;
        } else if (func == CUnit.MINUS) {
            result = n.negate();
        } else if (func == CUnit.ABS) {
            result = BigComplex.valueOf(Maffs.abs(n));
        } else if (func == CUnit.LOG) {
            result = Maffs.log(n);
        } else if (func == CUnit.LOG10) {
            result = Maffs.log10(n);
        } else if (func == CUnit.LOG2) {
            result = Maffs.log2(n);
        } else if (func == CUnit.SQRT) {
            result = Maffs.sqrt(n);
        } else if (func == CUnit.SIN) {
            result = Maffs.sin(n, mappedAngleUnit);
        } else if (func == CUnit.COS) {
            result = Maffs.cos(n, mappedAngleUnit);
        } else if (func == CUnit.TAN) {
            result = Maffs.tan(n, mappedAngleUnit);
        } else if (func == CUnit.ASIN) {
            result = Maffs.asin(n, mappedAngleUnit);
        } else if (func == CUnit.ACOS) {
            result = Maffs.acos(n, mappedAngleUnit);
        } else if (func == CUnit.ATAN) {
            result = Maffs.atan(n, mappedAngleUnit);
        } else if (func == CUnit.SINH) {
            result = Maffs.sinh(n);
        } else if (func == CUnit.COSH) {
            result = Maffs.cosh(n);
        } else if (func == CUnit.TANH) {
            result = Maffs.tanh(n);
        } else if (func == CUnit.ASINH) {
            result = Maffs.asinh(n);
        } else if (func == CUnit.ACOSH) {
            result = Maffs.acosh(n);
        } else if (func == CUnit.ATANH) {
            result = Maffs.atanh(n);
        } else if (func == CUnit.CSC) {
            result = Maffs.csc(n, mappedAngleUnit);
        } else if (func == CUnit.SEC) {
            result = Maffs.sec(n, mappedAngleUnit);
        } else if (func == CUnit.COT) {
            result = Maffs.cot(n, mappedAngleUnit);
        } else if (func == CUnit.FACTORIAL) {
            result = Maffs.factorial(n);
        } else if (func == CUnit.SQUARED) {
            result = Maffs.squared(n);
        } else if (func == CUnit.CUBED) {
            result = Maffs.multiply(Maffs.squared(n), n);
        } else if (func == CUnit.INVERSE) {
            result = Maffs.inverse(n);
        } else if (func == CUnit.PERCENT) {
            result = Maffs.scaleByPowerOfTen(n, -2);
        } else if (func == CUnit.ARG) {
            final BigDecimal resultReal = angleUnit == CParams.ANGLE_DEG ? Maffs.arg(n, AngleUnit.DEG) : Maffs.arg(n, AngleUnit.RAD);
            result = BigComplex.valueOf(resultReal);
        } else if (func == CUnit.CONJ) {
            result = Maffs.conjugate(n);
        } else {
            throw new IllegalArgumentException("Unrecognized function: " + func);
        }
        return requireInRange(Maffs.round(result));
    }

    public static boolean isInteger(@NonNull BigDecimal n) {
        try {
            n.toBigIntegerExact();
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    /**
     * Value is within range if the exponents of both real and imaginary parts are in range. Values
     * out of range should throw a {@link OutOfRangeException}.
     *
     * @param n value to check
     * @return true if the value is within range
     * @see #isExponentInRange(int)
     * @see OutOfRangeException
     */
    public static boolean isInRange(@NonNull BigComplex n) {
        return isInRange(n.re) && isInRange(n.im);
    }

    /**
     * Value is within range if the exponent is in range. Values
     * out of range should throw a {@link OutOfRangeException}.
     *
     * @param n value to check
     * @return true if the value is within range
     * @see #isExponentInRange(int)
     * @see OutOfRangeException
     */
    public static boolean isInRange(@NonNull BigDecimal n) {
        if (n.signum() == 0) {
            return true;
        }
        return isExponentInRange(BigDecimalMath.exponent(n));
    }

    /**
     * Takes the exponent of a value to check if the value is in range. Value is
     * in range if its exponent has a absolute value <= {@link #MAX_EXPONENT}.
     *
     * @param exponent exponent of value to check
     * @return true if value is in range
     */
    public static boolean isExponentInRange(int exponent) {
        return Math.abs(exponent) <= MAX_EXPONENT;
    }

    /**
     * @param n exponent of value to check
     * @return true if value is in range
     * @see #isExponentInRange(int)
     */
    public static boolean isExponentInRange(@NonNull BigInteger n) {
        return n.abs().compareTo(BigInteger.valueOf(MAX_EXPONENT)) < 0;
    }

    /**
     * @param n number to check if in range
     * @return {@code n} if it is in range
     * @throws OutOfRangeException if {@code n} is out of range
     * @see #isInRange(BigComplex)
     */
    @NonNull
    public static BigComplex requireInRange(@NonNull BigComplex n) throws OutOfRangeException {
        if (isInRange(n)) {
            return n;
        } else {
            throw new OutOfRangeException();
        }
    }

    // -------- CALCULATE HELPER FUNCTIONS -----------------------------------------------------------------------------------

    /**
     * Called from {@link #calculate(CUnit[], CParams)}, prepares a sequence for calculation.
     * The original sequence is not modified. The preparation process is:
     * <ol>
     *     <li>Check if input sequence is empty.</li>
     *     <li>Balance brackets.</li>
     *     <li>Combine {@link CUnit#PLUS} and {@link CUnit#MINUS} units.</li>
     *     <li>Combine {@link CUnit#isDigit()} and {@link CUnit#POINT} and into number values.</li>
     *     <li>Evaluate {@link CUnit#EXP} and {@link CUnit#PERCENT} operations/functions.</li>
     *     <li>Replace {@link CUnit#isVariable()} with the value given in {@code params}.</li>
     * </ol>
     * This method makes sure the output sequence will:
     * <ul>
     *     <li>Not be empty.</li>
     *     <li>Have balanced brackets.</li>
     *     <li>Not have consecutive {@link CUnit#PLUS} and {@link CUnit#MINUS} units.</li>
     *     <li>Not contain {@link CUnit#isDigit()} or {@link CUnit#POINT} units.</li>
     *     <li>Not contain {@link CUnit#EXP} or {@link CUnit#PERCENT} units.</li>
     *     <li>Not contain any constants.</li>
     *     <li>Not contain any {@link CUnit#isVariable()}.</li>
     * </ul>
     *
     * @param input input sequence to prepare
     * @param params Parameters of the calculation.
     * @return prepared sequence
     * @throws VariableException Sequence contains {@link CUnit#isVariable()} and {@code params} does not contain a value for it.
     * @throws SyntaxException Syntax error.
     * @throws OutOfRangeException A number that is out of range was encountered.
     * @see #calculate(CUnit[], CParams)
     * @see #isInRange(BigComplex)
     */
    @NonNull
    private static CUnit[] prepareSequence(@NonNull CUnit[] input, @NonNull CParams params)
            throws VariableException, SyntaxException, OutOfRangeException {
        // 1. Empty sequence
        if (input.length == 0) {
            throw new SyntaxException();
        }
        final List<CUnit> prepared = new ArrayList<>(Arrays.asList(input));
        // 2. balance brackets
        int bracketCount = 0;
        for (CUnit u: prepared) {
            if (u == CUnit.LEFT_BRACKET) {
                bracketCount++;
            } else if (u == CUnit.RIGHT_BRACKET) {
                bracketCount--;
            }
            if (bracketCount < 0) {
                throw new SyntaxException();
            }
        }
        for (int i = 0; i < bracketCount; i++) {
            prepared.add(CUnit.RIGHT_BRACKET);
        }
        // 3. combine plus/minus
        for (int i = 0; i < prepared.size() - 1; i++) {
            final CUnit u1 = prepared.get(i);
            final CUnit u2 = prepared.get(i + 1);
            if (u1.isPlusOrMinus() && u2.isPlusOrMinus()) {
                if (u1 == u2) {    // ++ or --
                    prepared.set(i, CUnit.PLUS);
                } else {                // +- or -+
                    prepared.set(i, CUnit.MINUS);
                }
                prepared.remove(i + 1);
                i--;
            }
        }
        // 4. combine digits into number
        for (int i = 0; i < prepared.size(); i++) {
            if (prepared.get(i).isDigitOrPoint()) {
                final StringBuilder combinedString = new StringBuilder();
                do {
                    combinedString.append(prepared.remove(i).toString());
                } while (i < prepared.size() && prepared.get(i).isDigitOrPoint());
                try {
                    prepared.add(i, new CNum(combinedString.toString()));
                } catch (NumberFormatException e) {
                    throw new SyntaxException(e.getMessage());
                }
            }
        }
        // 5. EXP and PERCENT
        for (int i = 0; i < prepared.size() - 2; i++) {
            final CUnit u1 = prepared.get(i);
            final CUnit u2 = prepared.get(i + 1);
            final CUnit u3 = prepared.get(i + 2);
            if (u2 == CUnit.EXP) {
                if (u1 instanceof CNum && ((CNum) u1).isCombinedFromDigits()) {
                    final BigComplex expResult;
                    if (u3 instanceof CNum && ((CNum) u3).isCombinedFromDigits()) {     // n ^ m
                        expResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) u3).getNum());
                    } else if (i < prepared.size() - 3 && u3.isPlusOrMinus()
                            && prepared.get(i + 3) instanceof CNum && ((CNum) prepared.get(i + 3)).isCombinedFromDigits()) { // n ^ +- m
                        expResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) prepared.get(i + 3)).getSignedNum(u3));
                        prepared.remove(i + 3);
                    } else {
                        throw new SyntaxException();
                    }
                    prepared.set(i, new CNum(expResult));
                    prepared.remove(i + 2);
                    prepared.remove(i + 1);
                    i--;
                } else {
                    throw new SyntaxException();
                }
            }
        }
        for (int i = 0; i < prepared.size() - 1; i++) {
            final CUnit u1 = prepared.get(i);
            final CUnit u2 = prepared.get(i + 1);
            if (u2 == CUnit.PERCENT) {
                if (u1 instanceof CNum && ((CNum) u1).isCombinedFromDigits()) {
                    final BigComplex funcResult = calculateFunction(u2, ((CNum) u1).getNum(), 0);
                    prepared.set(i, new CNum(funcResult));
                    prepared.remove(i + 1);
                    i--;
                } else {
                    throw new SyntaxException();
                }
            }
        }
        // 7. Variables
        for (int i = 0; i < prepared.size(); i++) {
            final CUnit u = prepared.get(i);
            if (u.isVariable()) {
                if (params.getValue(u) == null) {
                    throw new VariableException();
                }
                prepared.set(i, new CNum(params.getValue(u), false));
            }
        }
        return prepared.toArray(new CUnit[] {});
    }

    /**
     * <p>Called from {@link #calculate(CUnit[], CParams)}. Calculates the input sequence.
     * Assumes that the input is prepared using {@link #prepareSequence(CUnit[], CParams)}.</p>
     * <p>Evaluation order:</p>
     * <ol>
     *     <li>Check for empty sequence (empty if called from empty brackets).</li>
     *     <li>Evaluate brackets (recursion)</li>
     *     <li>{@link CUnit#isPostFunction()}</li>
     *     <li>{@link CUnit#POWER}</li>
     *     <li>{@link CUnit#ROOT}</li>
     *     <li>Evaluate {@link CUnit#isPreFunction()} recursively like brackets. PreFunc, sign and consecutive {@link CNum} is within scope, scope
     *     must end with value.</li>
     *     <li>Consecutive {@link CNum}s (multiply).</li>
     *     <li>Permutation, combination.</li>
     *     <li>{@link CUnit#TIMES}, {@link CUnit#DIVIDE}</li>
     *     <li>{@link CUnit#PLUS}, {@link CUnit#MINUS}</li>
     *     <li>If last remaining {@link CUnit} is a {@link CNum}, return result.</li>
     * </ol>
     *
     * @param input prepared sequence
     * @param params Parameters of the calculation.
     * @return result of calculation
     * @throws SyntaxException syntax error
     * @throws UndefinedException result is undefined
     * @throws OutOfRangeException result is out of range
     * @see #calculate(CUnit[], CParams)
     * @see #prepareSequence(CUnit[], CParams)
     */
    private static BigComplex calculate2(@NonNull final CUnit[] input, final CParams params)
            throws SyntaxException, UndefinedException, OutOfRangeException {
        if (input.length == 0) {
            throw new SyntaxException();
        }
        final List<CUnit> temp = new ArrayList<>(Arrays.asList(input));
        // 2. brackets
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i) == CUnit.LEFT_BRACKET) {
                // left - right brackets
                int bracketCount = 0;
                final List<CUnit> bracketSequence = new ArrayList<>();
                do {
                    if (temp.get(i) == CUnit.LEFT_BRACKET) {
                        bracketCount++;
                    }
                    if (temp.get(i) == CUnit.RIGHT_BRACKET) {
                        bracketCount--;
                    }
                    bracketSequence.add(temp.remove(i));
                } while (bracketCount > 0);
                // remove brackets at ends
                bracketSequence.remove(0);
                bracketSequence.remove(bracketSequence.size() - 1);
                final BigComplex bracketResult = calculate2(bracketSequence.toArray(new CUnit[] {}), params);
                temp.add(i, new CNum(bracketResult));
            }
        }
        // 3. post func
        if (temp.get(0).isPostFunction()) {
            throw new SyntaxException();
        }
        for (int i = 0; i < temp.size() - 1; i++) {
            final CUnit u1 = temp.get(i);
            final CUnit u2 = temp.get(i + 1);
            if (u2.isPostFunction()) {
                if (u1 instanceof CNum) {
                    final BigComplex funcResult = calculateFunction(u2, ((CNum) u1).getNum(), params.getAngleUnit());
                    temp.set(i, new CNum(funcResult));
                    temp.remove(i + 1);
                    i--;
                } else {
                    throw new SyntaxException();
                }
            }
        }
        // 4. power
        for (int i = 0; i < temp.size() - 2; i++) {
            final CUnit u1 = temp.get(i);
            final CUnit u2 = temp.get(i + 1);
            final CUnit u3 = temp.get(i + 2);
            if (u2 == CUnit.POWER) {
                if (u1 instanceof CNum) {
                    final BigComplex powResult;
                    if (u3 instanceof CNum) {     // n ^ m
                        powResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) u3).getNum());
                    } else if (i < temp.size() - 3 && u3.isPlusOrMinus() && temp.get(i + 3) instanceof CNum) { // n ^ +- m
                        powResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) temp.get(i + 3)).getSignedNum(u3));
                        temp.remove(i + 3);
                    } else {
                        throw new SyntaxException();
                    }
                    temp.set(i, new CNum(powResult));
                    temp.remove(i + 2);
                    temp.remove(i + 1);
                    i--;
                } else {
                    throw new SyntaxException();
                }
            }
        }
        // 5. nroot
        for (int i = 0; i < temp.size() - 2; i++) {
            final CUnit u1 = temp.get(i);
            final CUnit u2 = temp.get(i + 1);
            final CUnit u3 = temp.get(i + 2);
            if (u2 == CUnit.ROOT) {
                if (u1 instanceof CNum) {
                    final BigComplex rootResult;
                    if (u3 instanceof CNum) {     // n ^ m
                        rootResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) u3).getNum());
                    } else if (i < temp.size() - 3 && u3.isPlusOrMinus() && temp.get(i + 3) instanceof CNum) { // n ^ +- m
                        rootResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) temp.get(i + 3)).getSignedNum(u3));
                        temp.remove(i + 3);
                    } else {
                        throw new SyntaxException();
                    }
                    temp.set(i, new CNum(rootResult));
                    temp.remove(i + 2);
                    temp.remove(i + 1);
                    i--;
                } else {
                    throw new SyntaxException();
                }
            }
        }
        // 6. pre func
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).isPreFunction()) {
                final CUnit func = temp.remove(i);
                final List<CUnit> funcSequence = new ArrayList<>();
                // if a CNum is added to the sequence
                boolean hasNum = false;
                while (i < temp.size() && (temp.get(i).isPreFunction() || temp.get(i).isPlusOrMinus() || temp.get(i) instanceof CNum)) {
                    if (hasNum && !(temp.get(i) instanceof CNum)) {
                        break;
                    } else {
                        if (temp.get(i) instanceof CNum) {
                            hasNum = true;
                        }
                        funcSequence.add(temp.remove(i));
                    }
                }
                if (funcSequence.isEmpty() || !hasNum) {
                    throw new SyntaxException();
                }
                final BigComplex funcResult = calculateFunction(func, calculate2(funcSequence.toArray(new CUnit[] {}), params), params.getAngleUnit());
                temp.add(i, new CNum(funcResult));
            }
        }
        // 7. consecutive values
        for (int i = 0; i < temp.size() - 1; i++) {
            final CUnit u1 = temp.get(i);
            final CUnit u2 = temp.get(i + 1);
            if (u1 instanceof CNum && u2 instanceof CNum) {
                final BigComplex multiplyResult = calculateOperator(CNum.TIMES, ((CNum) u1).getNum(), ((CNum) u2).getNum());
                temp.set(i, new CNum(multiplyResult));
                temp.remove(i + 1);
                i--;
            }
        }
        // 8. Permutation, combination
        for (int i = 0; i < temp.size() - 2; i++) {
            final CUnit u1 = temp.get(i);
            final CUnit u2 = temp.get(i + 1);
            final CUnit u3 = temp.get(i + 2);
            if (u2.isPermutationOrCombination()) {
                if (u1 instanceof CNum) {
                    final BigComplex calcResult;
                    if (u3 instanceof CNum) {
                        calcResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) u3).getNum());
                    } else if (i < temp.size() - 3 && u3.isPlusOrMinus() && temp.get(i + 3) instanceof CNum) {
                        calcResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) temp.get(i + 3)).getSignedNum(u3));
                        temp.remove(i + 3);
                    } else {
                        throw new SyntaxException();
                    }
                    temp.set(i, new CNum(calcResult));
                    temp.remove(i + 2);
                    temp.remove(i + 1);
                    i--;
                } else {
                    throw new SyntaxException();
                }
            }
        }
        // 9. multiply, divide
        for (int i = 0; i < temp.size() - 2; i++) {
            final CUnit u1 = temp.get(i);
            final CUnit u2 = temp.get(i + 1);
            final CUnit u3 = temp.get(i + 2);
            if (u2.isTimesOrDivide()) {
                if (u1 instanceof CNum) {
                    final BigComplex calcResult;
                    if (u3 instanceof CNum) {     // n x m
                        calcResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) u3).getNum());
                    } else if (i < temp.size() - 3 && u3.isPlusOrMinus() && temp.get(i + 3) instanceof CNum) { // n x +- m
                        calcResult = calculateOperator(u2, ((CNum) u1).getNum(), ((CNum) temp.get(i + 3)).getSignedNum(u3));
                        temp.remove(i + 3);
                    } else {
                        throw new SyntaxException();
                    }
                    temp.set(i, new CNum(calcResult));
                    temp.remove(i + 2);
                    temp.remove(i + 1);
                    i--;
                } else {
                    throw new SyntaxException();
                }
            }
        }
        // 10. add, subtract
        for (int i = 0; i < temp.size() - 1; i++) {
            final CUnit u1 = temp.get(i);
            final CUnit u2 = temp.get(i + 1);
            if (u1.isPlusOrMinus()) {
                if (u2 instanceof CNum) {
                    if (i > 0 && temp.get(i - 1) instanceof CNum) {   // 1 + 1
                        final BigComplex calcResult = calculateOperator(u1, ((CNum) temp.get(i - 1)).getNum(), ((CNum) u2).getNum());
                        temp.set(i - 1, new CNum(calcResult));
                        temp.remove(i + 1);
                        temp.remove(i);
                        i--;
                    } else {                                            // + 1
                        final BigComplex calcResult = ((CNum) u2).getSignedNum(u1);
                        temp.set(i, new CNum(calcResult));
                        temp.remove(i + 1);
                    }
                } else {
                    throw new SyntaxException();
                }
            }
        }
        if (temp.size() == 1 && temp.get(0) instanceof CNum) {
            final BigComplex result = ((CNum) temp.get(0)).getNum();
            return requireInRange(Maffs.round(result));
        } else {
            throw new SyntaxException();
        }
    }

//    /**
//     * Called from {@link #calculate(CSequence, BigComplex, int)} to calculate the input sequence. Assumes the input is
//     * prepared using {@link #prepareSequence(CUnit[])}.
//     * <p>Evaluation method:</p>
//     * <ol>
//     *     <li>Check for empty sequence.</li>
//     *     <li>Create stacks for calculation order.</li>
//     *     <li>Iterate through sequence, changing the stacks depending on the unit:<ol type="a">
//     *         <li></li>
//     *     </ol></li>
//     *     <li>Remove units from {@code unitStack} until it is empty, changing the stacks depending on the unit:<ol type="a">
//     *         <li></li>
//     *     </ol></li>
//     *     <li>Return result if {@code numStack} only has 1 number, else raise {@link SyntaxException}.</li>
//     * </ol>
//     *
//     * @param input sequence to calculate
//     * @param answer number to replace {@link CUnit#ANS}
//     * @param angleUnit {@link com.bx.calculator.math.AngleUnit#ANGLE_RAD} or {@link com.bx.calculator.math.AngleUnit#ANGLE_DEG}
//     * @return result
//     * @throws NullPointerException {@code input} is null or a {@link CUnit} is in {@code input} is null
//     * @throws SyntaxException syntax error
//     * @throws VariableException {@code input} contains {@link CUnit#ANS} and {@code answer} is null
//     * @throws OutOfRangeException result is out of range
//     * @throws UndefinedException result is undefined
//     * @see #calculate(CSequence, BigComplex, int)
//     * @see #prepareSequence(CUnit[])
//     */
//    private static BigComplex calculate2(@NonNull final CUnit[] input, @Nullable final BigComplex answer, final int angleUnit)
//            throws NullPointerException, SyntaxException, VariableException, OutOfRangeException, UndefinedException {
//        // 1. Empty sequence
//        if (input.length == 0) {
//            throw new SyntaxException();
//        }
//        // 2. Create stacks
//        final Deque<BigComplex> numStack = new ArrayDeque<>();
//        final Deque<CUnit> unitStack = new ArrayDeque<>(60);
//        // 3. Build stacks
//        for (CUnit u: input) {
//            if (!u.isDigitOrPoint()) {
//                performPopNumberOperation(unitStack, numStack);
//                if (u.isPlusOrMinus()) {
//                    if (performPlusMinusOperation(unitStack, numStack)) {
//                        // next
//                    } else if (performTimesDivideOperation(unitStack, numStack)) {
//                        // next
//                    }
//                } else if (u.isTimesOrDivide()) {
//                    performTimesDivideOperation(unitStack, numStack);
//                }
//            }
//            unitStack.push(u);
//        }
//        performPopNumberOperation(unitStack, numStack);
//        // 4. Pop stacks
//        while (!unitStack.isEmpty()) {
//            final CUnit u = unitStack.getFirst();
//            if (u.isDigitOrPoint()) {
//                throw new AssertionError("Digit or point found while popping unit stack");
//            } else if (performPlusMinusOperation(unitStack, numStack)) {
//                // next
//            } else if (performTimesDivideOperation(unitStack, numStack)) {
//
//            } else {
//                throw new SyntaxException();
//            }
//        }
//        // 5. Return result
//        if (numStack.size() != 1) {
//            throw new SyntaxException();
//        }
//        return requireInRange(numStack.pop());
//    }
//
//    /**
//     * If the top of the {@code fromStack} is a digit or point, then pop the number from the stack and
//     * push it to {@code toStack}.
//     *
//     * @param fromStack Stack to pop from.
//     * @param toStack Stack to push to.
//     * @return true If the operation was executed.
//     * @throws SyntaxException Number popped from the stack is invalid.
//     * @see #popNumberFromStack(Deque)
//     * @see #isStackTopDigitOrPoint(Deque)
//     */
//    private static boolean performPopNumberOperation(@NonNull Deque<CUnit> fromStack, @NonNull Deque<BigComplex> toStack) throws SyntaxException {
//        if (isStackTopDigitOrPoint(fromStack)) {
//            toStack.push(popNumberFromStack(fromStack));
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * Perform a plus or minus operation to modify the stacks if possible.
//     *
//     * @param fromStack Unit stack.
//     * @param toStack Number stack.
//     * @return true if the operation was executed.
//     * @throws SyntaxException Syntax error.
//     */
//    private static boolean performPlusMinusOperation(@NonNull Deque<CUnit> fromStack, @NonNull Deque<BigComplex> toStack) throws SyntaxException {
//        boolean performed = false;
//        while (!fromStack.isEmpty() && fromStack.getFirst().isPlusOrMinus()) {
//            final CUnit pm = fromStack.pop();
//            final BigComplex n2 = requirePopNumber(toStack);
//            if (toStack.isEmpty()) {   // signed number
//                toStack.push(calculateFunction(pm, n2, 0));
//            } else {
//                toStack.push(calculateOperator(pm, toStack.pop(), n2));
//            }
//            performed = true;
//        }
//        return performed;
//    }
//
//    /**
//     * Perform a times or divide operation to modify the stacks if possible.
//     *
//     * @param fromStack Unit stack.
//     * @param toStack Number stack.
//     * @return true if the operation was executed.
//     * @throws SyntaxException Syntax error.
//     */
//    private static boolean performTimesDivideOperation(@NonNull Deque<CUnit> fromStack, @NonNull Deque<BigComplex> toStack) throws SyntaxException {
//        if (!fromStack.isEmpty() && fromStack.getFirst().isTimesOrDivide()) {
//            final CUnit op = fromStack.pop();
//            final BigComplex n2 = requirePopNumber(toStack);
//            final BigComplex n1 = requirePopNumber(toStack);
//            toStack.push(calculateOperator(op, n1, n2));
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * Pops the digits and points from the stack, then returns the number that is formed by them.
//     *
//     * @param stack stack to pop from
//     * @return number from stack
//     * @throws SyntaxException number formed from the stack is invalid ({@link NumberFormatException}), or the top
//     * {@link CUnit} is not a digit or point, so tries to format an empty string
//     * @see #performPopNumberOperation(Deque, Deque)
//     */
//    @NonNull
//    private static BigComplex popNumberFromStack(@NonNull Deque<CUnit> stack) throws SyntaxException {
//        final StringBuilder builder = new StringBuilder();
//        while (isStackTopDigitOrPoint(stack)) {
//            builder.insert(0, stack.pop());
//        }
//        try {
//            return BigComplex.valueOf(new BigDecimal(builder.toString()));
//        } catch (NumberFormatException e) {
//            throw new SyntaxException(e.getMessage());
//        }
//    }
//
//    /**
//     * @return true if the top of the stack is a digit or point
//     */
//    private static boolean isStackTopDigitOrPoint(@NonNull Deque<CUnit> stack) {
//        return !stack.isEmpty() && stack.getFirst().isDigitOrPoint();
//    }
//
//    private static BigComplex requirePopNumber(@NonNull Deque<BigComplex> stack) throws SyntaxException {
//        if (stack.isEmpty()) {
//            throw new SyntaxException();
//        } else {
//            return stack.pop();
//        }
//    }

    private Calculate() {}
}
