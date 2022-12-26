package org.programs.math.types;

import org.programs.math.nodes.Node;
import org.programs.math.parser.SymbolTable;

/**
 * Represents a parameter of a function, holding details like its name, default expression (if any), etc.
 */
public final class Parameter {

    /**
     * Represents the type of the parameter.
     */
    enum Type {

        /**
         * Required parameter.
         */
        REQUIRED,

        /**
         * Optional parameter.
         */
        OPTIONAL
    }

    /**
     * The name of this parameter as defined in the function.
     */
    public final String name;

    /**
     * The default value given to this parameter (if any).
     */
    public final Node defaultVal;

    /**
     * The type of this parameter. Required if no default value is given, optional otherwise.
     */
    public final Type type;

    /**
     * Constructs a parameter object.
     *
     * @param name   The name of the parameter.
     * @param oVal   The default value (if any).
     */
    public Parameter(String name, Node oVal) {
        this.name = name;
        defaultVal = oVal;
        if (defaultVal == null) {
            type = Type.REQUIRED;
        } else {
            type = Type.OPTIONAL;
        }
    }

    @Override
    public String toString() {
        return "(Name: " + name + ", Type: " + type + ", Val: " + defaultVal + ")";
    }
}
