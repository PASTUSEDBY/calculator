package org.programs.math.exceptions;

/**
 * Exception to indicate that an identifier was already declared, and it exists in the current scope.
 */
public class IdentifierExistsException extends BaseException {

    /**
     * Constructs this exception.
     * @param name The name of the identifier.
     * @param fn Whether this identifier is a function or a variable (normal identifier).
     */
    public IdentifierExistsException(String name, boolean fn) {
        super ((fn ? "Function" : "Identifier") + " with name '" + name + "' is already defined.");
    }
}
