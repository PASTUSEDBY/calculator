package org.programs.math.exceptions;

/**
 * Indicates that an exception is caught while the math expression is being evaluated at runtime,
 * after it is parsed.
 *
 * <p>Some common exceptions at runtime:
 * <p>- Division by zero
 * <p>- Function calls itself recursively
 * <p>- Factorial of negative or decimal number.
 */
public class RTException extends BaseException {

    /**
     * Constructs this exception.
     * @param message The error message.
     */
    public RTException(String message) {
        super (message);
    }
}
