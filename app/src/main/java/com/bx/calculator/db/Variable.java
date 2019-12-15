package com.bx.calculator.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.bx.calculator.calc.CUnit;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import ch.obermuhlner.math.big.BigComplex;

@Entity(tableName = "variables")
public class Variable implements Parcelable {

    public static final Parcelable.Creator<Variable> CREATOR = new Creator<Variable>() {
        @Override
        public Variable createFromParcel(Parcel source) {
            final String display = source.readString();
            final String re = source.readString();
            final String im = source.readString();
            BigComplex value;
            try {
                value = BigComplex.valueOf(new BigDecimal(re), new BigDecimal(im));
            } catch (NumberFormatException | NullPointerException e) {
                value = BigComplex.ZERO;
            }
            final int order = source.readInt();
            return new Variable(display, value, order);
        }

        @Override
        public Variable[] newArray(int size) {
            return new Variable[size];
        }
    };

    @PrimaryKey
    @NonNull
    public String display;
    @NonNull
    public BigComplex value;
    /**
     * Order shown in variable recycler view.
     */
    public int order;

    public Variable(@NonNull String display, @NonNull BigComplex value, int order) {
        this.display = display;
        this.value = value;
        this.order = order;
    }

    @Nullable
    public static Variable getDefault(@NonNull String display) {
        for (int i = 0; i < CUnit.variables.size(); i++) {
            if (CUnit.variables.get(i).toString().equals(display)) {
                return new Variable(display, BigComplex.ZERO, i);
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(display);
        dest.writeString(value.re.toString());
        dest.writeString(value.im.toString());
        dest.writeInt(order);
    }
}
