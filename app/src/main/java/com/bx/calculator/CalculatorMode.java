package com.bx.calculator;

import com.bx.calculator.calc.CUnit;

/**
 * Classes that implement this interface are a calculator mode and have to implement actions that
 * respond to actions, typically button presses.
 */
public interface CalculatorMode {

    void execute();
    void clear();
    void delete();
    void insert(CUnit unit);
    void changeInputMode();
}
