package org.programs.math.exceptions;

/**
 * Exception to indicate an identifier does not exist.
 */
public class NoSuchIdentifierException extends BaseException {

    /**
     * Constructs this exception.
     * @param name The name of the identifier.
     * @param fn Whether this identifier is a function or a variable (normal identifier).
     */
    public NoSuchIdentifierException(String name, boolean fn) {
        super ((fn ? "Function" : "Identifier") + " with name '" + name + "' does not exist!");
    }
}
