package org.programs.math.types;

import org.programs.math.exceptions.RTException;
import org.programs.math.nodes.Node;
import org.programs.math.parser.SymbolTable;

import java.util.List;

/**
 * <p>Represents a user-defined function.
 *    Stores information about itself like names, parameters, etc.
 * <p>Each function has its own unique SymbolTable (or variable scope) along with its outer SymbolTable
 *    (from where it was called).
 * <p>
 *
 * @see SymbolTable
 */
public class Func implements Value {

    /**
     * The name of the function.
     */
    public final String name;

    /**
     * The function expression.
     */
    public final Node expr;

    /**
     * The parameters of the function.
     */
    public final List<Parameter> parameters;

    /**
     * The minimum number of required arguments.
     */
    public final int min;

    /**
     * The maximum number of required arguments.
     */
    public final int max;

    /**
     * The number of times the function is called before it has ended.
     * This helps to check if the function is being called recursively (kind of).
     * If the {@code callCount} is greater than 100, then this function is being recursively called.
     * Note that this kind of helps, since doing nested calls like f(f(f...)) 100 times may also trigger this.
     *
     */
    private int callCount;

    /**
     * Constructs a function object.
     * @param name The name of the function.
     * @param body The function expression.
     * @param as The parameters of the function.
     */
    public Func(String name, Node body, List<Parameter> as) {
        this.name = name;
        expr = body;
        parameters = as;

        int tempMin = 0;
        for (Parameter a : parameters) {
            if (a.type != Parameter.Type.OPTIONAL) {
                tempMin++;
            }
        }

        if (tempMin == parameters.size()) {
            min = max = tempMin;
        } else {
            min = tempMin;
            max = parameters.size();
        }
    }

    /**
     * Executes the function expression according to the arguments provided to it, when called.
     * @param args The arguments provided to the function.
     * @param parent The outer symbol table.
     * @return The number from the function execution.
     *
     * @throws RTException If the function recursively calls itself.
     */
    public TNumber execute(List<Node> args, SymbolTable parent) {
        try {
            if (++callCount > 100) {
                throw new RTException("Function '" + name + "' recursively calls itself.");
            }

            SymbolTable symbolTable = new SymbolTable(parent);

            for (int i = 0; i < max; i++) {
                TNumber val = getArg(args, i, symbolTable);
                String paramName = parameters.get(i).getName();
                symbolTable.set(paramName, val);
            }

            return expr.visit(symbolTable);
        } finally {
            --callCount;
        }
    }

    /**
     * Gets the argument (if present), else the default value provided to the parameter.
     * This method should not throw a NullPointerException ever.
     * If argument is not present, a default value should always be present since the parameter was optional.
     * @param args The arguments provided.
     * @param index The index of the argument.
     * @param st The SymbolTable of this function.
     * @return The argument.
     */
    protected TNumber getArg(List<Node> args, int index, SymbolTable st) {
        if (index >= args.size()) {
            return parameters.get(index).defaultVal.visit(st);
        }
        return args.get(index).visit(st);
    }

    @Override
    public String toString() {
        return "(Args: " + parameters + ", ArgCount:{" + min + "," + max + "}, Body: " + expr + ")";
    }
}
