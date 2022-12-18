package org.programs.math.exceptions;

/**
 * Exception to indicate that an invalid number of arguments was passed to a function.
 */
public class InvalidArgsException extends BaseException {

    /**
     * Constructs this exception.
     * @param req Required number of arguments.
     * @param given Number of arguments given.
     * @param few Boolean to indicate whether too few or too many arguments were given.
     */
    public InvalidArgsException(int req, int given, boolean few) {
        super (
                "Too " + (few ? "few " : "many ")
                        + "arguments given. \nRequired: " + req
                        + ", given: " + given
        );
    }
}
