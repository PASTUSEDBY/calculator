package org.programs.math.exceptions;

/**
 * A general abstract exception. All other specific exceptions extend this class.
 */
public abstract class BaseException extends RuntimeException {

    /**
     * Creates a BaseException.
     * @param message The error message.
     */
    public BaseException(String message) {
        super (message);
    }

    /**
     * Gives back the name of this exception.
     * @return The exception name.
     */
    private String formatName() {
        String[] xs = getClass().getName().split("\\.");
        return xs[xs.length - 1];
    }

    public String toString() {
        return formatName() + ": " + getMessage();
    }
}
