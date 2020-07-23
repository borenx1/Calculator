package com.bx.calculator.db;

import com.bx.calculator.calc.CParams;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.calc.math.AngleUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.room.TypeConverter;
import ch.obermuhlner.math.big.BigComplex;

public class Converters {

    @TypeConverter
    public static String complex2String(BigComplex value) {
        final JSONArray array = new JSONArray();
        array.put(value.re.toString());
        array.put(value.im.toString());
        return array.toString();
    }

    @TypeConverter
    public static BigComplex string2Complex(String value) {
        try {
            final JSONArray array = new JSONArray(value);
            final BigDecimal re = new BigDecimal(array.getString(0));
            final BigDecimal im = new BigDecimal(array.getString(1));
            return BigComplex.valueOf(re, im);
        } catch (JSONException e) {
            return BigComplex.ZERO;
        }
    }

    @TypeConverter
    public static String sequence2String(CUnit[] value) {
        final JSONArray array = new JSONArray();
        Arrays.stream(value).forEach(unit -> array.put(unit.toString()));
        return array.toString();
    }

    @TypeConverter
    public static CUnit[] string2Sequence(String value) {
        try {
            final JSONArray array = new JSONArray(value);
            final CUnit[] units = new CUnit[array.length()];
            for (int i = 0; i < units.length; i++) {
                units[i] = CUnit.get(array.getString(i));
            }
            return units;
        } catch (JSONException e) {
            return new CUnit[0];
        }
    }

    @TypeConverter
    public static String params2String(CParams value) {
        try {
            final JSONObject object = new JSONObject();
            // TODO Decide on using string or int for saving AngleUnits
            object.put("angleUnit", value.getAngleUnit().ordinal());
            final JSONObject varMapObject = new JSONObject();
            for (final CUnit u: value.getVariables()) {
                final BigComplex val = value.getValue(u);
                if (val != null) {
                    final JSONArray numArray = new JSONArray();
                    numArray.put(val.re.toString());
                    numArray.put(val.im.toString());
                    varMapObject.put(u.toString(), numArray);
                }
            }
            object.put("variableMap", varMapObject);
            return object.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    @TypeConverter
    public static CParams string2Params(String value) {
        try {
            final JSONObject object = new JSONObject(value);
            final JSONObject varMapObject = object.getJSONObject("variableMap");

            final int angleUnit = object.getInt("angleUnit");

            final Map<CUnit, BigComplex> variables = new HashMap<>();
            final Iterator<String> varMapIterator = varMapObject.keys();
            while (varMapIterator.hasNext()) {
                final String v = varMapIterator.next();
                if (!varMapObject.isNull(v)) {
                    final JSONArray numArray = varMapObject.getJSONArray(v);
                    final BigDecimal re = new BigDecimal(numArray.getString(0));
                    final BigDecimal im = new BigDecimal(numArray.getString(1));
                    variables.put(CUnit.get(v), BigComplex.valueOf(re, im));
                }
            }
            // TODO Decide on using string or int for saving AngleUnits
            return new CParams(AngleUnit.values()[angleUnit], variables);
        } catch (JSONException e) {
            return new CParams();
        }
    }
}
