package org.programs.math.exceptions;

/**
 * Exception to indicate an invalid syntax was given (which is semantically not correct in mathematics).
 */
public class InvalidSyntaxException extends BaseException {

    /**
     * Constructs this exception.
     * @param m The message.
     */
    public InvalidSyntaxException(String m) {
        super (m);
    }
}
