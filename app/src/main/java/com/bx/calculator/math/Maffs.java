package com.bx.calculator.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import androidx.annotation.NonNull;
import ch.obermuhlner.math.big.BigComplex;
import ch.obermuhlner.math.big.BigComplexMath;
import ch.obermuhlner.math.big.BigDecimalMath;

public final class Maffs {

    /**
     * Default {@link MathContext} used for calculations.
     */
    public static final MathContext MC = new MathContext(64, RoundingMode.HALF_EVEN);
    public static final BigComplex TWO = BigComplex.valueOf(2);
    public static final BigDecimal PI = BigDecimalMath.pi(MC);
    public static final BigDecimal TAU = multiply(PI, new BigDecimal(2));
    public static final BigDecimal E = BigDecimalMath.e(MC);
    public static final BigDecimal HALF = new BigDecimal("0.5");

    @NonNull
    public static BigComplex add(@NonNull BigComplex n1, @NonNull BigComplex n2) {
        return n1.add(n2);
    }

    @NonNull
    public static BigComplex subtract(@NonNull BigComplex n1, @NonNull BigComplex n2) {
        return n1.subtract(n2);
    }

    @NonNull
    public static BigComplex multiply(@NonNull BigComplex n1, @NonNull BigComplex n2) {
        return n1.multiply(n2, MC);
    }

    @NonNull
    public static BigDecimal multiply(@NonNull BigDecimal n1, @NonNull BigDecimal n2) {
        return n1.multiply(n2, MC);
    }

    @NonNull
    public static BigComplex divide(@NonNull BigComplex n1, @NonNull BigComplex n2) throws UndefinedException {
        if (isZero(n2)) {
            throw new UndefinedException();
        }
        return n1.divide(n2, MC);
    }

    @NonNull
    public static BigDecimal divide(@NonNull BigDecimal n1, @NonNull BigDecimal n2) throws UndefinedException {
        if (n2.signum() == 0) {
            throw new UndefinedException();
        }
        return n1.divide(n2, MC);
    }

    @NonNull
    public static BigComplex pow(@NonNull BigComplex n1, @NonNull BigComplex n2) throws UndefinedException {
        if (isZero(n1)) {
            if (n2.re.signum() > 0) {           // 0^n = 0
                return BigComplex.ZERO;
            } else {                            // 0^0, 0^-1 = UNDEFINED
                throw new UndefinedException();
            }
        } else if (isZero(n2)) {                // n^0 = 1
            return BigComplex.ONE;
        } else if (n1.isReal() && n1.re.compareTo(E) == 0) {
            return exp(n2);
        } else if (n2.isReal()) {
            return pow(n1, n2.re);
        } else {
            return BigComplexMath.pow(n1, n2, MC);
        }
    }

    @NonNull
    public static BigComplex pow(@NonNull BigComplex n1, @NonNull BigDecimal n2) throws UndefinedException {
        if (isZero(n1)) {
            if (n2.signum() > 0) {
                return BigComplex.ZERO;
            } else {
                throw new UndefinedException();
            }
        } else if (n1.isReal() && n1.re.compareTo(E) == 0) {
            return exp(BigComplex.valueOf(n2));
        } else if (n2.compareTo(HALF) == 0) {
            return sqrt(n1);
        } else if (n2.compareTo(HALF.negate()) == 0) {
            return inverse(sqrt(n1));
        } else if (n1.isReal()) {
            try {
                return BigComplex.valueOf(BigDecimalMath.pow(n1.re, n2, MC));
            } catch (ArithmeticException e) {
                // if re ^ re fails, try complex ^ re
            }
        }
        return BigComplexMath.pow(n1, n2, MC);
    }

    @NonNull
    public static BigComplex root(@NonNull BigComplex x, @NonNull BigComplex n) throws UndefinedException {
        return pow(x, divide(BigComplex.ONE, n));
    }

    @NonNull
    public static BigComplex inverse(@NonNull BigComplex n) throws UndefinedException {
        if (isZero(n)) {
            throw new UndefinedException();
        }
        return n.reciprocal(MC);
    }

    @NonNull
    public static BigComplex exp(@NonNull BigComplex n) {
        return BigComplexMath.exp(n, MC);
    }

    @NonNull
    public static BigDecimal abs(@NonNull BigComplex n) {
        return n.abs(MC);
    }

    @NonNull
    public static BigDecimal arg(@NonNull BigComplex n) throws UndefinedException {
        if (isZero(n)) {
            throw new UndefinedException();
        }
        return n.angle(MC);
    }

    public static BigDecimal arg(@NonNull BigComplex n, AngleUnit angleUnit) {
        if (angleUnit == AngleUnit.DEG) {
            return toDeg(arg(n));
        } else {
            return arg(n);
        }
    }

    @NonNull
    public static BigComplex conjugate(@NonNull BigComplex n) {
        return n.conjugate();
    }

    @NonNull
    public static BigComplex log(@NonNull BigComplex n) throws UndefinedException {
        if (isZero(n)) {
            throw new UndefinedException();
        }
        if (n.isReal() && n.re.signum() > 0) {
            return BigComplex.valueOf(BigDecimalMath.log(n.re, MC));
        } else {
            return BigComplexMath.log(n, MC);
        }
    }

    @NonNull
    public static BigComplex log2(@NonNull BigComplex n) throws UndefinedException {
        if (isZero(n)) {
            throw new UndefinedException();
        }
        final MathContext mc = new MathContext(MC.getPrecision() + 2, MC.getRoundingMode());
        if (n.isReal() && n.re.signum() > 0) {
            return BigComplex.valueOf(BigDecimalMath.log2(n.re, MC));
        } else {
            return BigComplexMath.log(n, mc).divide(BigDecimalMath.log(new BigDecimal(2), mc), mc).round(MC);
        }
    }

    @NonNull
    public static BigComplex log10(@NonNull BigComplex n) throws UndefinedException {
        if (isZero(n)) {
            throw new UndefinedException();
        }
        final MathContext mc = new MathContext(MC.getPrecision() + 2, MC.getRoundingMode());
        if (n.isReal() && n.re.signum() > 0) {
            return BigComplex.valueOf(BigDecimalMath.log10(n.re, MC));
        } else {
            return BigComplexMath.log(n, mc).divide(BigDecimalMath.log(BigDecimal.TEN, mc), mc).round(MC);
        }
    }

    @NonNull
    public static BigComplex sqrt(@NonNull BigComplex n) {
        if (n.isReal()) {
            if (n.re.signum() >= 0) {
                return BigComplex.valueOf(BigDecimalMath.sqrt(n.re, MC));
            } else {
                // problem with sqrt of negative values
                return BigComplex.valueOf(BigDecimal.ZERO, BigDecimalMath.sqrt(n.re.negate(), MC));
            }
        } else {
            return BigComplexMath.sqrt(n, MC);
        }
    }

    @NonNull
    public static BigComplex squared(@NonNull BigComplex n) {
        return multiply(n, n);
    }

    @NonNull
    public static BigComplex sin(@NonNull BigComplex n) {
        // TODO get exact trig functions (0, undefined), round = 0 or undefined when pass threshold
        if (BigDecimalMath.exponent(n.im.abs()) < 3) {
            return BigComplexMath.sin(n, MC);
        } else {
            // more efficient
            return divide(subtract(exp(multiply(n, BigComplex.I)), exp(multiply(n, BigComplex.I).negate())), multiply(TWO, BigComplex.I));
        }
    }

    @NonNull
    public static BigComplex sin(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) {
        if (angleUnit == AngleUnit.DEG) {
            return sin(BigComplex.valueOf(toRad(n.re), n.im));
        } else {
            return sin(n);
        }
    }

    @NonNull
    public static BigComplex cos(@NonNull BigComplex n) {
        if (BigDecimalMath.exponent(n.im.abs()) < 3) {
            return BigComplexMath.cos(n, MC);
        } else {
            // more efficient
            return divide(add(exp(multiply(n, BigComplex.I)), exp(multiply(n, BigComplex.I).negate())), TWO);
        }
    }

    @NonNull
    public static BigComplex cos(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) {
        if (angleUnit == AngleUnit.DEG) {
            return cos(BigComplex.valueOf(toRad(n.re), n.im));
        } else {
            return cos(n);
        }
    }

    @NonNull
    public static BigComplex tan(@NonNull BigComplex n) {
        if (BigDecimalMath.exponent(n.im.abs()) < 3) {
            return BigComplexMath.tan(n, MC);
        } else {
            return divide(sin(n), cos(n));
        }
    }

    @NonNull
    public static BigComplex tan(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) throws UndefinedException {
        if (angleUnit == AngleUnit.DEG) {
            return tan(BigComplex.valueOf(toRad(n.re), n.im));
        } else {
            return tan(n);
        }
    }

    @NonNull
    public static BigComplex csc(@NonNull BigComplex n) throws UndefinedException {
        return inverse(sin(n));
    }

    @NonNull
    public static BigComplex csc(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) throws UndefinedException {
        return inverse(sin(n, angleUnit));
    }

    @NonNull
    public static BigComplex sec(@NonNull BigComplex n) throws UndefinedException {
        return inverse(cos(n));
    }

    @NonNull
    public static BigComplex sec(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) throws UndefinedException {
        return inverse(cos(n, angleUnit));
    }

    @NonNull
    public static BigComplex cot(@NonNull BigComplex n) throws UndefinedException {
        return inverse(tan(n));
    }

    @NonNull
    public static BigComplex cot(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) throws UndefinedException {
        return inverse(tan(n, angleUnit));
    }

    @NonNull
    public static BigComplex asin(@NonNull BigComplex n) {
        return BigComplexMath.asin(n, MC);
    }

    @NonNull
    public static BigComplex asin(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) {
        final BigComplex radAnswer = asin(n);
        if (angleUnit == AngleUnit.DEG) {
             return BigComplex.valueOf(toDeg(radAnswer.re), radAnswer.im);
        } else {
            return radAnswer;
        }
    }

    @NonNull
    public static BigComplex acos(@NonNull BigComplex n) {
        return BigComplexMath.acos(n, MC);
    }

    @NonNull
    public static BigComplex acos(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) {
        final BigComplex radAnswer = acos(n);
        if (angleUnit == AngleUnit.DEG) {
            return BigComplex.valueOf(toDeg(radAnswer.re), radAnswer.im);
        } else {
            return radAnswer;
        }
    }

    @NonNull
    public static BigComplex atan(@NonNull BigComplex n) {
        return BigComplexMath.atan(n, MC);
    }

    @NonNull
    public static BigComplex atan(@NonNull BigComplex n, @NonNull AngleUnit angleUnit) {
        final BigComplex radAnswer = atan(n);
        if (angleUnit == AngleUnit.DEG) {
            return BigComplex.valueOf(toDeg(radAnswer.re), radAnswer.im);
        } else {
            return radAnswer;
        }
    }

    @NonNull
    public static BigComplex sinh(@NonNull BigComplex n) {
        return divide(subtract(exp(n), exp(n.negate())), TWO);
//        return multiply(sin(multiply(n, BigComplex.I)), BigComplex.I).negate();
    }

    @NonNull
    public static BigComplex cosh(@NonNull BigComplex n) {
        return divide(add(exp(n), exp(n.negate())), TWO);
//        return cos(multiply(n, BigComplex.I));
    }

    @NonNull
    public static BigComplex tanh(@NonNull BigComplex n) throws UndefinedException {
        return divide(subtract(exp(multiply(n, TWO)), BigComplex.ONE), add(exp(multiply(n, TWO)), BigComplex.ONE));
//        return divide(sinh(n), cosh(n));
    }

    @NonNull
    public static BigComplex asinh(@NonNull BigComplex n) {
        return log(add(n, sqrt(add(squared(n), BigComplex.ONE))));
    }

    @NonNull
    public static BigComplex acosh(@NonNull BigComplex n) {
        return log(add(n, sqrt(subtract(squared(n), BigComplex.ONE))));
    }

    @NonNull
    public static BigComplex atanh(@NonNull BigComplex n) throws UndefinedException {
        return divide(log(divide(add(BigComplex.ONE, n), subtract(BigComplex.ONE, n))), TWO);
    }

    @NonNull
    public static BigComplex factorial(@NonNull BigComplex n) throws UndefinedException {
        // TODO wait for bigmath complex factorial
        if (n.isReal()) {
            return BigComplex.valueOf(factorial(n.re));
        } else {
            throw new UndefinedException();
        }
    }

    @NonNull
    public static BigDecimal factorial(@NonNull BigDecimal n) throws UndefinedException {
        try {
            return BigDecimalMath.factorial(n, MC);
        } catch (ArithmeticException e) {
            throw new UndefinedException(e.getMessage());
        }
    }

    @NonNull
    public static BigDecimal permutation(@NonNull BigDecimal n, @NonNull BigDecimal r) throws UndefinedException {
        if (n.signum() >= 0 && r.signum() >= 0 && isInteger(n) && isInteger(r) && n.compareTo(r) >= 0) {
            return divide(factorial(n), factorial(n.subtract(r)));
        } else {
            throw new UndefinedException();
        }
    }

    @NonNull
    public static BigDecimal combination(@NonNull BigDecimal n, @NonNull BigDecimal r) throws UndefinedException {
        if (n.signum() >= 0 && r.signum() >= 0 && isInteger(n) && isInteger(r) && n.compareTo(r) >= 0) {
            return divide(factorial(n), multiply(factorial(r), factorial(n.subtract(r))));
        } else {
            throw new UndefinedException();
        }
    }

    @NonNull
    public static BigComplex scaleByPowerOfTen(@NonNull BigComplex n, int exp) {
        if (isZero(n)) {
            return BigComplex.ZERO;
        }
        return BigComplex.valueOf(
                n.re.movePointRight(exp).stripTrailingZeros(),
                n.im.movePointRight(exp).stripTrailingZeros());
    }


    public static boolean isZero(@NonNull BigComplex n) {
        return n.re.signum() == 0 && n.im.signum() == 0;
    }

    /**
     * @return true if this value is an integer
     */
    public static boolean isInteger(@NonNull BigDecimal n) {
        try {
            n.toBigIntegerExact();
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    @NonNull
    public static BigDecimal toRad(@NonNull BigDecimal deg) {
        MathContext mc = new MathContext(MC.getPrecision() + 4, MC.getRoundingMode());
        return deg.multiply(PI, mc).divide(new BigDecimal(180), mc);
    }

    @NonNull
    public static BigDecimal toDeg(@NonNull BigDecimal rad) {
        MathContext mc = new MathContext(MC.getPrecision() + 4, MC.getRoundingMode());
        return rad.multiply(new BigDecimal(180), mc).divide(PI, mc);
    }

    @NonNull
    public static BigComplex round(@NonNull BigComplex n) {
        final BigComplex rounded = n.round(MC);
        // strip trailing zeros of 0 bug
        final BigDecimal re = rounded.re.signum() == 0 ? BigDecimal.ZERO : rounded.re.stripTrailingZeros();
        final BigDecimal im = rounded.im.signum() == 0 ? BigDecimal.ZERO : rounded.im.stripTrailingZeros();
        return BigComplex.valueOf(re, im);
    }

    private Maffs() {}
}
