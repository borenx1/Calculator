package com.bx.calculator;

import android.text.Html;

import com.bx.calculator.calc.CParams;
import com.bx.calculator.calc.Calculate;
import com.bx.calculator.calc.CResult;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.calc.exception.VariableException;
import com.bx.calculator.calc.exception.SyntaxException;
import com.bx.calculator.math.Maffs;
import com.bx.calculator.math.UndefinedException;

import java.math.BigDecimal;

import org.junit.Test;

import ch.obermuhlner.math.big.BigComplex;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test() {

    }

    // ---------------- MAFFS ------------------------------------------------------------------------------------------------------------------------

    @Test
    public void maffsUndefined() {
        try {
            Maffs.divide(BigComplex.ONE, BigComplex.ZERO);
            assert false;
        } catch (UndefinedException e) {
            assert true;
        }
        try {
            Maffs.pow(BigComplex.ZERO, BigComplex.ZERO);
            assert false;
        } catch (UndefinedException e) {
            assert true;
        }
        try {
            Maffs.pow(BigComplex.ZERO, BigComplex.I);
            assert false;
        } catch (UndefinedException e) {
            assert true;
        }
        try {
            Maffs.pow(BigComplex.ZERO, BigComplex.ONE.negate());
            assert false;
        } catch (UndefinedException e) {
            assert true;
        }
        assertEquals(BigComplex.ZERO, Maffs.pow(BigComplex.ZERO, BigComplex.valueOf(1, 1)));
    }


    @Test
    public void maffsTrig() {
        assertEquals(BigComplex.ZERO, Maffs.sin(BigComplex.ZERO));
        assertEquals(BigComplex.ONE, Maffs.sin(BigComplex.valueOf(Maffs.multiply(Maffs.PI, new BigDecimal("0.5")))));
        assertEquals(BigComplex.valueOf(-1), Maffs.cos(BigComplex.valueOf(Maffs.PI)));
        assertEquals(BigComplex.valueOf(0.5), Maffs.sin(BigComplex.valueOf(Maffs.divide(Maffs.PI, new BigDecimal(6)))));
        assertEquals(BigComplex.ZERO, Maffs.sin(BigComplex.ZERO));
    }

    @Test
    public void maffsFactorial() {
        assertEquals(BigComplex.ONE, Maffs.factorial(BigComplex.ZERO));
        assertEquals(BigComplex.ONE, Maffs.factorial(BigComplex.ONE));
        assertEquals(BigComplex.valueOf(3628800), Maffs.factorial(BigComplex.valueOf(10)));
        try {
            Maffs.factorial(BigComplex.ONE.negate());
            assert false;
        } catch (UndefinedException e) {
            assert true;
        }
    }


    // ---------------- CALC -------------------------------------------------------------------------------------------------------------------------

    @Test
    public void calcEmptySequence() {
        try {
            Calculate.calculate(new CUnit[] {}, new CParams());
            assert false;
        } catch (SyntaxException e) {
            assert true;
        }
    }

    @Test
    public void calcNullAnswer() {
        try {
            Calculate.calculate(new CUnit[] {CUnit.ANS}, new CParams());
            assert false;
        } catch (VariableException e) {
            assert true;
        }
    }

    @Test
    public void calcCombineDigits() {
        assertEquals(BigComplex.ONE,
                Calculate.calculate(new CUnit[] {CUnit.ONE}, new CParams()).getAnswer());
        assertEquals(BigComplex.valueOf(20),
                Calculate.calculate(new CUnit[] {CUnit.TWO, CUnit.ZERO}, new CParams()).getAnswer());
        assertEquals(BigComplex.valueOf(new BigDecimal("0.5")),
                Calculate.calculate(new CUnit[] {CUnit.POINT, CUnit.FIVE}, new CParams()).getAnswer());
    }

    @Test
    public void calcSignNumbers() {
        assertEquals(BigComplex.ONE.negate(),
                Calculate.calculate(new CUnit[] {CUnit.MINUS, CUnit.ONE}, new CParams()).getAnswer());
        assertEquals(BigComplex.ONE,
                Calculate.calculate(new CUnit[] {CUnit.MINUS, CUnit.MINUS, CUnit.ONE}, new CParams()).getAnswer());
        assertEquals(BigComplex.ONE.negate(),
                Calculate.calculate(new CUnit[] {CUnit.MINUS, CUnit.PLUS, CUnit.ONE}, new CParams()).getAnswer());
    }

    @Test
    public void calcPlusMinus() {
        assertEquals(BigComplex.valueOf(2),
                Calculate.calculate(new CUnit[] {CUnit.ONE, CUnit.PLUS, CUnit.ONE}, new CParams()).getAnswer());
        assertEquals(BigComplex.ONE,
                Calculate.calculate(new CUnit[] {CUnit.TWO, CUnit.PLUS, CUnit.MINUS, CUnit.ONE}, new CParams()).getAnswer());
        assertEquals(BigComplex.valueOf(-3),
                Calculate.calculate(new CUnit[] {CUnit.MINUS, CUnit.TWO, CUnit.MINUS, CUnit.ONE}, new CParams()).getAnswer());
        assertEquals(BigComplex.valueOf(2),
                Calculate.calculate(new CUnit[] {CUnit.MINUS, CUnit.MINUS, CUnit.THREE, CUnit.PLUS, CUnit.MINUS, CUnit.ONE}, new CParams()).getAnswer());
    }

    @Test
    public void calcExp() {
        assertEquals(BigComplex.valueOf(300),
                Calculate.calculate(new CUnit[] {CUnit.THREE, CUnit.EXP, CUnit.TWO}, new CParams()).getAnswer());
        assertEquals(BigComplex.valueOf(new BigDecimal("0.03")),
                Calculate.calculate(new CUnit[] {CUnit.THREE, CUnit.EXP, CUnit.MINUS, CUnit.TWO}, new CParams()).getAnswer());
    }

    @Test
    public void calcTrigConversion() {
        assertEquals(BigComplex.valueOf(1),
                Calculate.calculate(new CUnit[] {CUnit.SIN, CUnit.NINE, CUnit.ZERO}, new CParams(CParams.ANGLE_DEG)).getAnswer());
//        assertEquals(BigComplex.valueOf(new BigDecimal("0.5")),
//                Calculate.calculate(new CSequence(CUnit.COS, CUnit.SIX, CUnit.ZERO), new CParams(CParams.ANGLE_DEG)).getAnswer());
    }


//    @Test
//    public void testCalculatorLogic() {
//        // 5. tan 0.5 pi
////        input = new CSequence(CPreFunc.TAN, CUnit.POINT, CDigit.FIVE, CValue.PI);
////        try {
////            result = Calculate.calculate(input, null, Calculate.ANGLE_RAD);
////            System.out.println(result.getAnswer());
////            assert false;
////        } catch (UndefinedException e) {
////            passed = printPassedTest(passed);
////        }
//        // 7. TRIG 1
//        assertSequenceResult(new CSequence(CPreFunc.SIN, CDigit.ZERO), null, AngleUnit.ANGLE_RAD, BigComplex.ZERO);
//        assertSequenceResult(new CSequence(CPreFunc.SIN, CUnit.POINT, CDigit.FIVE, CValue.PI), null, AngleUnit.ANGLE_RAD, BigComplex.ONE);
//        assertSequenceResult(new CSequence(CPreFunc.COS, CValue.PI), null, AngleUnit.ANGLE_RAD, BigComplex.valueOf(-1));
//        assertSequenceResult(new CSequence(CPreFunc.SIN, CUnit.LEFT_BRACKET, CValue.PI, COperator.DIVIDE, CDigit.SIX),
//                null, AngleUnit.ANGLE_RAD, BigComplex.valueOf(0.5));
//        passed = printPassedTest(passed);
//        // 8. TRIG 2 - rad/deg conversions
//        assertSequenceResult(new CSequence(CPreFunc.SIN, CDigit.NINE, CDigit.ZERO), null, AngleUnit.ANGLE_DEG, BigComplex.ONE);
//        assertSequenceResult(new CSequence(CPreFunc.COS, CDigit.SIX, CDigit.ZERO), null, AngleUnit.ANGLE_DEG, BigComplex.valueOf(0.5));
//    }
}