package com.bx.calculator.calc;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import ch.obermuhlner.math.big.BigComplex;
import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * Contains static methods to format sequences and numbers into strings.
 */
public final class CFormat {

    /**
     * @return The strings of the CUnits in sequence, like displayed on the screen.
     * @throws NullPointerException Sequence or a unit is null.
     */
    @NonNull
    public static Spanned toDisplayString(@Nullable CUnit[] sequence) throws NullPointerException {
        if (sequence == null) {
            return SpannableString.valueOf("");
        }
        final StringBuilder s = new StringBuilder();
        for (CUnit u: sequence) {
            s.append(Objects.requireNonNull(u).toString());
        }
        return Html.fromHtml(s.toString(), Html.FROM_HTML_MODE_LEGACY);
    }

    /**
     * Returns the index of the unit in the sequence that
     * corresponds to the position in the display string.
     *
     * @param index Position in display string.
     * @return Index of object.
     */
    public static int display2UnitIndex(@NonNull CUnit[] sequence, final int index) {
        if (index >= toDisplayString(sequence).length())
            return sequence.length;
        int unitIndex = 0;
        int stringIndex = 0;
        for (final CUnit unit: sequence) {
            if (stringIndex + unit.length() > index) {
                break;
            } else {
                stringIndex += unit.length();
                unitIndex++;
            }
        }
        return unitIndex;
    }

    /**
     * Returns the index position of the string form of the unit
     * at {@code #unitIndex} in sequence.
     *
     * @param unitIndex Index of unit in sequence.
     * @return The position of the unit in string form.
     */
    public static int unit2DisplayIndex(@NonNull CUnit[] sequence, int unitIndex) {
        if (unitIndex >= sequence.length)
            return toDisplayString(sequence).length();
        int stringIndex = 0;
        for (int i = 0; i < unitIndex; i++) {
            stringIndex += sequence[i].length();
        }
        return stringIndex;
    }

    /**
     * Returns {@link #toScientificString(BigComplex, int)} if the exponent of the value is <= lower limit or >= upper limit,
     * or {@link #toPlainString(BigComplex, int)} if the value is within the bounds.
     *
     * @param n value
     * @param sigfig significant figures for rounding
     * @param lower lower limit of order of magnitude
     * @param upper upper limit of order of magnitude
     * @return {@link #toPlainString(BigComplex, int)} or {@link #toScientificString(BigComplex, int)}
     */
    @NonNull
    public static CharSequence toConditionalString(@NonNull BigComplex n, int sigfig, int lower, int upper) throws IllegalArgumentException {
        if (lower <= 0 || upper <= 0) {
            throw new IllegalArgumentException("lower and upper must be positive");
        }
        final BigComplex rounded = toSignificantFigures(n, sigfig);
        final int reExponent = BigDecimalMath.exponent(rounded.re);
        final int imExponent = BigDecimalMath.exponent(rounded.im);
        if (reExponent <= -lower || imExponent <= -lower || reExponent > upper || imExponent > upper) {
            return toScientificString(rounded);
        } else {
            return toPlainString(rounded);
        }
    }

    @NonNull
    public static CharSequence toConditionalString(@NonNull BigComplex n, @NonNull Context context) throws IllegalStateException {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final int sigfig = preferences.getInt("output_sigfig", 10);
        final int scientificLower = preferences.getInt("output_scientific_lower", 3);
        final int scientificUpper = preferences.getInt("output_scientific_upper", 6);
        try {
            return CFormat.toConditionalString(n, sigfig, scientificLower, scientificUpper);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
    }

    @NonNull
    public static CharSequence toPlainString(@NonNull BigComplex n) {
        return toComplexString(n, false);
    }

    @NonNull
    public static CharSequence toPlainString(@NonNull BigComplex n, int sigfig) throws IllegalArgumentException {
        return toPlainString(toSignificantFigures(n, sigfig));
    }

    @NonNull
    public static CharSequence toScientificString(@NonNull BigComplex n) {
        return toComplexString(n, true);
    }

    @NonNull
    public static CharSequence toScientificString(@NonNull BigComplex n, int sigfig)  throws IllegalArgumentException {
        return toScientificString(toSignificantFigures(n, sigfig));
    }

    @NonNull
    public static String toPlainString(@NonNull BigDecimal n, int sigfig) throws IllegalArgumentException {
        return toSignificantFigures(n, sigfig).toPlainString();
    }

    @NonNull
    public static CharSequence toScientificString(@NonNull BigDecimal n, int sigfig) throws IllegalArgumentException {
        return toScientificString(toSignificantFigures(n, sigfig));
    }

    @NonNull
    public static CharSequence toScientificString(@NonNull BigDecimal n) {
        if (n.signum() == 0) {
            return "0";
        }
        final String mantissa = BigDecimalMath.mantissa(n).toPlainString();
        final int exponent = BigDecimalMath.exponent(n);
        return Html.fromHtml(String.format(Locale.US, "%s×10<sup><small>%d</sup></small>", mantissa, exponent), Html.FROM_HTML_MODE_LEGACY);
    }

    @NonNull
    public static BigDecimal toSignificantFigures(@NonNull BigDecimal n, int sigfig) throws IllegalArgumentException {
        if (sigfig <= 0) {
            throw new IllegalArgumentException("sigfig must be positive");
        }
        if (n.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return n.setScale(sigfig + n.scale() - n.precision(), RoundingMode.HALF_EVEN).stripTrailingZeros();
    }

    @NonNull
    public static BigComplex toSignificantFigures(@NonNull BigComplex n, int sigfig) throws IllegalArgumentException {
        return BigComplex.valueOf(toSignificantFigures(n.re, sigfig), toSignificantFigures(n.im, sigfig));
    }

    @NonNull
    private static CharSequence toComplexString(@NonNull BigComplex n, boolean scientific) {
        if (n.re.signum() == 0 && n.im.signum() == 0) {
            return "0";
        }
        final CharSequence reString = scientific ? toScientificString(n.re) : n.re.toPlainString();
        if (n.isReal()) {
            return reString;
        }

        final SpannableStringBuilder imString = new SpannableStringBuilder();
        if (n.im.abs().compareTo(BigDecimal.ONE) != 0) {
            // remember use abs()
            imString.append(scientific ? toScientificString(n.im.abs()) : n.im.abs().toPlainString());
        }
        imString.append('i');
        if (n.re.signum() == 0) {
            return n.im.signum() >= 0 ? imString : new SpannableStringBuilder("-").append(imString);
        }
        if (n.im.signum() >= 0) {
            return new SpannableStringBuilder(reString).append(" + ").append(imString);
        } else {
            return new SpannableStringBuilder(reString).append(" - ").append(imString);
        }
    }

    private CFormat() {}
}
