package org.programs.math.extra;

import org.programs.math.exceptions.RTException;
import org.programs.math.types.ComplexNum;

import java.util.Objects;

import static org.programs.math.types.ComplexNum.*;

public final class Trigonometry {
    private static final ComplexNum REAL_TWO = REAL_UNIT.add(REAL_UNIT);
    private static final ComplexNum IM_TWO = IMAGINARY_UNIT.add(IMAGINARY_UNIT);
    private static final ComplexNum PI_HALF = PI.divide(REAL_TWO);

    public enum AngleType {
        DEGREES,
        RADIANS,
        GRADES
    }

    private static AngleType angleType;

    static {
        setAngleType(AngleType.RADIANS);
    }

    public static void setAngleType(AngleType a) {
        angleType = Objects.requireNonNull(a);
    }

    public static ComplexNum sin(ComplexNum x) {
        x = resolveInAngle(x);
        ComplexNum z = IMAGINARY_UNIT.multiply(x),
                a = E.pow(z),
                b = E.pow(z.negate());

        return a.subtract(b).divide(IM_TWO);
    }

    public static ComplexNum cos(ComplexNum x) {
        x = resolveInAngle(x);
        ComplexNum z = IMAGINARY_UNIT.multiply(x),
                a = ComplexNum.E.pow(z),
                b = ComplexNum.E.pow(z.negate());

        return a.add(b).divide(REAL_TWO);
    }

    public static ComplexNum tan(ComplexNum x) {
        x = resolveInAngle(x);
        if (oddMultipleOfHalfPi(x)) {
            throw new RTException("Odd multiple of PI / 2 was given to tan!");
        }

        return sin(x).divide(cos(x));
    }

    public static ComplexNum sec(ComplexNum x) {
        x = resolveInAngle(x);
        if (oddMultipleOfHalfPi(x)) {
            throw new RTException("Odd multiple of PI / 2 was given to sec!");
        }

        return cos(x).reciprocal();
    }

    public static ComplexNum cosec(ComplexNum x) {
        x = resolveInAngle(x);
        if (intMultipleOfPi(x)) {
            throw new RTException("Multiple of PI was given to cosec!");
        }

        return sin(x).reciprocal();
    }

    public static ComplexNum cot(ComplexNum x) {
        x = resolveInAngle(x);
        if (intMultipleOfPi(x)) {
            throw new RTException("Multiple of PI was given to cot!");
        }

        return cos(x).divide(sin(x));
    }

    public static ComplexNum asin(ComplexNum z) {
        ComplexNum x = REAL_UNIT.subtract(z.pow(REAL_TWO))
                .root(REAL_TWO)
                .subtract(z.multiply(IMAGINARY_UNIT))
                .pow(IMAGINARY_UNIT)
                .log(E);

        return resolveOutAngle(x);
    }

    public static ComplexNum acos(ComplexNum z) {
        return resolveOutAngle(
                PI_HALF.subtract(asin(z))
        );
    }

    public static ComplexNum atan(ComplexNum z) {
        ComplexNum x = IMAGINARY_UNIT.subtract(z)
                .divide(IMAGINARY_UNIT.add(z))
                .log(E)
                .multiply(IMAGINARY_UNIT)
                .divide(REAL_TWO)
                .negate();

        return resolveOutAngle(x);
    }

    public static ComplexNum acot(ComplexNum z) {
        if (z.isZero()) {
            return resolveOutAngle(PI_HALF);
        }

        return atan(z.reciprocal());
    }

    private static boolean oddMultipleOfHalfPi(ComplexNum x) {
        return x.isReal() && Math.abs(x.real / PI_HALF.real % 2) == 1;
    }

    private static boolean intMultipleOfPi(ComplexNum x) {
        return x.isReal() && Math.abs(x.real) % PI.real == 0;
    }

    private static ComplexNum resolveInAngle(ComplexNum x) {
        validateAngle(x);

        return switch (angleType) {
            case DEGREES -> new ComplexNum(Math.toRadians(x.real), 0);
            case GRADES -> new ComplexNum(x.real * Math.PI / 200, 0);
            default -> x;
        };
    }

    private static ComplexNum resolveOutAngle(ComplexNum x) {
        validateAngle(x);

        return switch (angleType) {
            case DEGREES -> new ComplexNum(x.real * 180 / Math.PI, 0);
            case GRADES -> new ComplexNum(x.real * 200 / Math.PI, 0);
            default -> x;
        };
    }

    private static void validateAngle(ComplexNum x) {
        if (angleType == AngleType.RADIANS) {
            return;
        }

        //This leaves us with other two.
        if (!x.isReal()) {
            throw new RTException(
                    "Current angle type is: " + angleType + ". If you wish to use complex "
                    + "numbers as parameter, you must switch to RADIANS. Received number: "
                    + x
            );
        }
    }
}
