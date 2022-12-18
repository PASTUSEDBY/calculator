package org.programs.math.exceptions;

/**
 * Exception which indicates a required parameter is defined after an optional in a function
 * parameter list.
 *
 * <p>Example: {@code fn f(x, y=5, z) -> x+y+z} - required parameter defined after optional parameter.
 */
public class ReqAfterOptionalException extends BaseException {

    /**
     * Constructs this exception.
     * @param name The function name.
     */
    public ReqAfterOptionalException(String name) {
        super (
                "A function which contains optional parameters should not be followed by required parameters." +
                        "\nIn function: '" + name + "'"
        );
    }
}
