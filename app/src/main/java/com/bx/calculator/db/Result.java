package com.bx.calculator.db;

import com.bx.calculator.calc.CParams;
import com.bx.calculator.calc.CUnit;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import ch.obermuhlner.math.big.BigComplex;

/**
 * Represents a history of results from past calculations. Position from oldest to newest.
 */
@Entity(tableName = "calculation_history")
public class Result {

    @PrimaryKey(autoGenerate = true)
    public int position;
    public CUnit[] input;
    public CParams params;
    public BigComplex answer;

    public Result(CUnit[] input, CParams params, BigComplex answer) {
		// update history adapter diff callback
        this.input = input;
        this.params = params;
        this.answer = answer;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Result[position=%s, input=%s, params=%s, answer=%s]", position, Arrays.toString(input), params, answer);
    }
}
