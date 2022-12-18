package org.programs.math.types;

import org.programs.math.exceptions.RTException;

/**
 * <p>Represents a constant number, like 42 or 6.9!
 * <p>This class also provides all the helper methods for all math operations.
 */
public final class TNumber implements Value {
    public enum AngleType {
        DEGREES,
        GRADES,
        RADIANS
    }

    private static AngleType angleType;

    static {
        setAngleType(AngleType.DEGREES); //default
    }

    public static final TNumber PI = create(Math.PI);

    public static final TNumber E = create(Math.E);

    /**
     * The constant number value.
     */
    public final double value;

    /**
     * Creates a TNumber object.
     * @param n The double value.
     */
    public TNumber(double n) {
        value = n;
    }

    /**
     * Static helper method to create a new TNumber object.
     * @param val The double value.
     * @return The TNumber object.
     *
     * @see TNumber#TNumber(double)
     */
    private static TNumber create(double val) {
        return new TNumber(val);
    }

    /**
     * Adds two numbers.
     * @param other The other number.
     * @return The sum.
     */
    public TNumber add(TNumber other) {
        return create(value + other.value);
    }

    /**
     * Subtracts two numbers.
     * @param other The other number.
     * @return The difference.
     */
    public TNumber subtract(TNumber other) {
        return create(value - other.value);
    }

    /**
     * Multiplies two numbers.
     * @param other The other number.
     * @return The product.
     */
    public TNumber multiply(TNumber other) {
        return create(value * other.value);
    }

    /**
     * Divides two numbers.
     * @param other The other number.
     * @return The quotient.
     */
    public TNumber divide(TNumber other) {
        if (other.value == 0) {
            return create(Double.NaN);
        }

        return create(value / other.value);
    }

    /**
     * Divides two numbers but returns the division after truncating the fractional part.
     * @param other The other number.
     * @return The quotient.
     */
    public TNumber intDivide(TNumber other) {
        return trimFraction(divide(other));
    }

    /**
     * Gives back the remainder after division.
     * @param other The other number.
     * @return The remainder.
     */
    public TNumber modulus(TNumber other) {
        return create(value % other.value);
    }

    /**
     * Returns the number raised to the other number.
     * @param other The other number.
     * @return The result.
     */
    public TNumber pow(TNumber other) {
        return create(Math.pow(value, other.value));
    }

    /**
     * Returns the absolute value of the number.
     * @return The absolute value.
     */
    public TNumber absolute() {
        return create(Math.abs(value));
    }

    /**
     * Returns the negative of the number.
     * @return The negated number.
     */
    public TNumber negate() {
        return create(-value);
    }

    /**
     * Returns the root of the number.
     * @param other The other number.
     * @return The root.
     */
    public TNumber root(TNumber other) {
        if (other.value <= 0) {
            throw new RTException("Root index can't be less than or equal to 0.");
        }
        return create(Math.pow(value, 1 / other.value));
    }

    /**
     * Returns the sin of the number.
     * @return The sin.
     */
    public TNumber sin() {
        return create(Math.sin(radians()));
    }

    /**
     * Returns the cos of the number.
     * @return The cos.
     */
    public TNumber cos() {
        return create(Math.cos(radians()));
    }

    /**
     * Returns the tan of the number.
     * @return The tan.
     */
    public TNumber tan() {
        return create(Math.tan(radians()));
    }

    /**
     * Returns the log of the number base the other number.
     * @param base The base.
     * @return The logarithm of this number base the other number.
     */
    public TNumber log(TNumber base) {
        return create(Math.log(value) / Math.log(base.value));
    }

    /**
     * Returns the factorial of the number.
     * @return The factorial.
     */
    public TNumber factorial() {
        if (value < 0 || value != (int) value) {
            throw new RTException("Can't compute factorial for value: " + this);
        }
        double fact = 1;
        for (int i = 1; i <= (int) value; i++) {
            fact *= i;
        }

        return create(fact);
    }

    public TNumber floor() {
        return create(Math.floor(value));
    }

    public TNumber ceil() {
        return create(Math.ceil(value));
    }

    private static TNumber trimFraction(TNumber num) {
        double value = num.value;
        return create(value < 0 ? Math.ceil(value) : Math.floor(value));
    }

    /**
     * Returns the radians of this angle (type dependant). Used in sin, cos tan.
     * @return The radians.
     * @see TNumber#sin()
     * @see TNumber#cos()
     * @see TNumber#tan()
     */
    private double radians() {
        return switch (angleType) {
            case DEGREES -> Math.toRadians(value);
            case GRADES -> value * Math.PI / 200;
            default -> value;
        };
    }

    public static void setAngleType(AngleType type) {
        angleType = type;
    }

    @Override
    public String toString() {
        if (value != value) {
            return "Math Error";
        }
        if (value == (long) value) {
            return "" + (long) value;
        }
        return "" + value;
    }
}
