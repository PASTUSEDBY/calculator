package org.programs.math.types;

import org.programs.math.exceptions.RTException;

/**
 * <p>Represents a constant complex number, like 6 + 9i or 4.2!
 * <p>This class also provides helper methods for basic math operations.
 */
public final class ComplexNum implements Value {
    public static final ComplexNum E = create(Math.E, 0);
    public static final ComplexNum PI = create(Math.PI, 0);
    public static final ComplexNum IMAGINARY_UNIT = create(0, 1);

    public static final ComplexNum REAL_UNIT = create(1, 0);
    public static final ComplexNum ZERO = create(0, 0);

    /**
     * The real part of the number.
     */
    public final double real;

    /**
     * The imaginary part of the number.
     */
    public final double imaginary;


    /**
     * Constructs a new complex number.
     * @param r The real part.
     * @param i The imaginary part.
     */
    public ComplexNum(double r, double i) {
        real = r;
        imaginary = i;
    }

    /**
     * Static helper method to create complex numbers.
     * @param r The real part.
     * @param i The imaginary part.
     * @return A complex number.
     * @see ComplexNum#ComplexNum(double, double)
     */
    private static ComplexNum create(double r, double i) {
        return new ComplexNum(r, i);
    }

    /**
     * Adds two numbers.
     * @param other The other number.
     * @return The sum.
     */
    public ComplexNum add(ComplexNum other) {
        return create(real + other.real, imaginary + other.imaginary);
    }

    /**
     * Subtracts two numbers.
     * @param other The other number.
     * @return The difference.
     */
    public ComplexNum subtract(ComplexNum other) {
        return create(real - other.real, imaginary - other.imaginary);
    }

    /**
     * Multiples two numbers.
     * <p>(a + bi)(c + di) = (ac - bd) + (ad + bc)i
     * @param other The other number.
     * @return The product.
     */
    public ComplexNum multiply(ComplexNum other) {
        return create(
                real * other.real - imaginary * other.imaginary,
                real * other.imaginary + imaginary * other.real
        );
    }

    /**
     * Divides two numbers. An exception is thrown if the divisor is zero.
     * <p>(a + bi) / (c + di) = (a + bi)(1 / c + di)
     * <p>(a + bi)(c - di) / |c + di|^2
     * <p>{(ac - bd) + (bc - ad)i} / (c^2 + d^2)
     * @param other The other number.
     * @return The quotient.
     */
    public ComplexNum divide(ComplexNum other) {
        if (other.isZero()) {
            throw new RTException("Division by 0!");
        }

        if (other.isReal()) {
            return create(real / other.real, imaginary / other.real);
        }

        ComplexNum cOther = other.conjugate();
        double divisor = other.modulusSquare();

        return multiply(cOther)
                .divide(create(divisor, 0));
    }

    /**
     * Just like division, except truncates the decimal part.
     * @param other The other number.
     * @return The truncated quotient.
     */

    public ComplexNum intDivide(ComplexNum other) {
        ComplexNum quot = divide(other);
        return create(
                (long) quot.real,
                (long) quot.imaginary
        );
    }

    /**
     * Negates this number.
     * @return The result.
     */
    public ComplexNum negate() {
        return create(-real, -imaginary);
    }

    /**
     * Performs logarithm of this number to the given base.
     * <p>ln z = ln |z| + iθ
     * <p>log a base b = ln a / ln b
     * @param base The base.
     * @return The result.
     */
    public ComplexNum log(ComplexNum base) {
        ComplexNum naturalLog = create(
                Math.log(modulus()),
                argument()
        );

        if (base.equals(E)) {
            return naturalLog;
        }

        ComplexNum naturalBase = base.log(E);

        return naturalLog.divide(naturalBase);
    }

    /**
     * Performs exponentiation of this number to the given number.
     * <p>e^(x + iy) = e^x * e^(iy) = e^x * (cos y + i sin y)
     * <p>a^x = e^(x ln a)
     * @param other The power to raise to.
     * @return The result.
     */
    public ComplexNum pow(ComplexNum other) {
        if (!E.equals(this)) {
            return E.pow(
                    log(E).multiply(other)
            );
        }

        ComplexNum realExp = create(Math.exp(other.real), 0),
                imExp = create(
                        Math.cos(other.imaginary),
                        Math.sin(other.imaginary)
                );

        return realExp.multiply(imExp);
    }

    /**
     * Performs the factorial. Supported for whole numbers only.
     * @return The result.
     */
    public ComplexNum factorial() {
        if (!isInteger() || real < 0) {
            throw new RTException("Can't compute factorial for value: " + this);
        }

        double fact = 1;
        for (int i = 1; i <= (int) real; i++) {
            fact *= i;
        }

        return create(fact, 0);
    }

    /**
     * Performs the floor operation.
     * @return The result.
     */
    public ComplexNum floor() {
        return create(
                Math.floor(real),
                Math.floor(imaginary)
        );
    }

    /**
     * Performs the ceil operation.
     * @return The result.
     */
    public ComplexNum ceil() {
        return create(
                Math.ceil(real),
                Math.ceil(imaginary)
        );
    }

    /**
     * Performs the root of this number to the given number.
     * Essentially this is just a^(1/x).
     * @param other The power.
     * @return The result.
     */
    public ComplexNum root(ComplexNum other) {
        return pow(other.reciprocal());
    }

    /**
     * Returns the conjugate of this number.
     * <p>~(x + iy) = x - iy
     * @return The result.
     */
    public ComplexNum conjugate() {
        return create(real, -imaginary);
    }

    /**
     * Performs the reciprocal of this number.
     * @return The reciprocal.
     */
    public ComplexNum reciprocal() {
        return REAL_UNIT.divide(this);
    }

    /**
     * Returns the modulus of this number.
     * For real numbers, this is absolute value.
     * @return The result.
     */
    public double modulus() {
        return Math.sqrt(modulusSquare());
    }

    /**
     * Private method to just return the modulus square, for lesser loss of precision.
     * @return The result.
     */
    private double modulusSquare() {
        return real * real + imaginary * imaginary;
    }

    /**
     * Returns the argument of this complex number.
     * @return The result.
     */
    public double argument() {
        if (isImaginary()) {
            return Math.signum(imaginary) * Math.PI / 2;
        }

        if (isReal() && real < 0) {
            return Math.PI;
        }

        double x = Math.atan(imaginary / real);

        if (real > 0) {
            return x;
        } else {
            return Math.signum(imaginary) * Math.PI + x;
        }
    }

    public boolean isReal() {
        return imaginary == 0;
    }

    public boolean isImaginary() {
        return real == 0;
    }

    public boolean isZero() {
        return equals(ZERO);
    }

    public boolean isInteger() {
        return isReal() && real == (long) real;
    }

    public boolean equals(ComplexNum other) {
        return real == other.real && imaginary == other.imaginary;
    }

    private String format(double x, boolean isIm) {
        if (x == 0) {
            return "";
        }

        String val = "" + x;

        if (val.endsWith(".0")) {
            val = val.substring(0, val.length() - 2);
        }

        if (isIm && Math.abs(x) == 1) {
            val = x < 0 ? "-" : "";
        }

        return val + (isIm ? "i" : "");
    }

    public String toString() {
        if (isZero()) {
            return "0";
        }

        String im = format(imaginary, true);

        if (isImaginary()) {
            return im;
        }

        String re = format(real, false);

        if (isReal()) {
            return re;
        }

        String finalString = re;

        if (im.startsWith("-")) {
            im = im.substring(1);
        }

        finalString += imaginary > 0 ? " + " : " - ";

        return finalString + im;
    }
}
