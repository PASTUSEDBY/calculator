package org.programs.math.extra;

import org.programs.math.exceptions.RTException;
import org.programs.math.types.ComplexNum;

public final class Trigonometry {
    private static final ComplexNum i = ComplexNum.IMAGINARY_UNIT;
    private static final ComplexNum r = ComplexNum.REAL_UNIT;
    private static final ComplexNum e = ComplexNum.E;
    private static final ComplexNum pi = ComplexNum.PI;

    public static ComplexNum sin(ComplexNum x) {
        ComplexNum z = i.multiply(x),
                a = ComplexNum.E.pow(z),
                b = ComplexNum.E.pow(z.negate());

        return a.subtract(b).divide(i.add(i));
    }

    public static ComplexNum cos(ComplexNum x) {
        ComplexNum z = i.multiply(x),
                a = ComplexNum.E.pow(z),
                b = ComplexNum.E.pow(z.negate());

        return a.add(b).divide(r.add(r));
    }

    public static ComplexNum tan(ComplexNum x) {
        if (oddMultipleOfHalfPi(x)) {
            throw new RTException("Odd multiple of PI / 2 was given to tan!");
        }

        return sin(x).divide(cos(x));
    }

    public static ComplexNum sec(ComplexNum x) {
        if (oddMultipleOfHalfPi(x)) {
            throw new RTException("Odd multiple of PI / 2 was given to sec!");
        }

        return cos(x).reciprocal();
    }

    public static ComplexNum cosec(ComplexNum x) {
        if (intMultipleOfPi(x)) {
            throw new RTException("Multiple of PI was given to cosec!");
        }

        return sin(x).reciprocal();
    }

    public static ComplexNum cot(ComplexNum x) {
        if (intMultipleOfPi(x)) {
            throw new RTException("Multiple of PI was given to cot!");
        }

        return cos(x).divide(sin(x));
    }

    public static ComplexNum asin(ComplexNum z) {
        return r.subtract(z.multiply(z))
                .root(r.add(r))
                .subtract(z.multiply(i))
                .pow(i)
                .log(e);
    }

    public static ComplexNum acos(ComplexNum z) {
        return r.subtract(z.multiply(z))
                .root(r.add(r))
                .multiply(i)
                .add(z)
                .pow(i)
                .log(e)
                .negate();
    }

    public static ComplexNum atan(ComplexNum z) {
        return i.subtract(z)
                .divide(i.add(z))
                .log(e)
                .multiply(i)
                .divide(r.add(r))
                .negate();
    }

    public static ComplexNum acot(ComplexNum z) {
        if (z.isZero()) {
            return pi.divide(r.add(r));
        }

        return atan(z.reciprocal());
    }

    private static boolean oddMultipleOfHalfPi(ComplexNum x) {
        ComplexNum divisor = pi.divide(r.add(r));

        return x.isReal() && Math.abs(x.real / divisor.real % 2) == 1;
    }

    private static boolean intMultipleOfPi(ComplexNum x) {
        return x.isReal() && Math.abs(x.real) % pi.real == 0;
    }
}
