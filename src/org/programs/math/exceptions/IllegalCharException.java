package org.programs.math.exceptions;

/**
 * Exception to indicate an invalid or unexpected character was encountered during lexing.
 */
public class IllegalCharException extends BaseException {

    /**
     * Constructs this exception.
     * @param cause The character which caused this exception.
     * @param pos The position at which this character was encountered.
     */
    public IllegalCharException(char cause, int pos) {
        super ("Unexpected character " + cause + " at position " + pos);
    }
}
