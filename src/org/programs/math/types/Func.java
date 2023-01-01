package org.programs.math.types;

import org.programs.math.exceptions.RTException;
import org.programs.math.extra.Trigonometry;
import org.programs.math.nodes.Node;
import org.programs.math.parser.SymbolTable;

import java.util.List;
import java.util.Objects;

/**
 * <p>Represents a function.
 *    Stores information about itself like names, parameters, etc.
 * <p>Each function has its own unique SymbolTable (or variable scope). This is to isolate all variables
 *    from one function to another.
 * <p>All functions are stored in the global SymbolTable.
 *
 * @see SymbolTable
 */
public final class Func implements Value {

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
     *
     * @param name The name of the function.
     * @param as   The parameters of the function.
     * @param body The function expression.
     */
    public Func(String name, List<Parameter> as, Node body) {
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
    public ComplexNum execute(List<Node> args, SymbolTable parent) {
        if (isNative()) {
            return execNative(args, parent);
        }

        try {
            if (++callCount > 100) {
                throw new RTException("Function '" + name + "' recursively calls itself.");
            }

            SymbolTable symbolTable = new SymbolTable();

            for (int i = 0; i < max; i++) {
                ComplexNum val = getArg(args, i, parent);
                String paramName = parameters.get(i).name;
                symbolTable.set(paramName, val);
            }

            return expr.visit(symbolTable);
        } finally {
            --callCount;
        }
    }

    /**
     * Checks if this function is native or not.
     * @return {@code true} if the function is native.
     */
    public boolean isNative() {
        return expr == null;
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
    private ComplexNum getArg(List<Node> args, int index, SymbolTable st) {
        if (index >= args.size()) {
            return parameters.get(index).defaultVal.visit(st);
        }
        return args.get(index).visit(st);
    }

    /**
     * Executes this native function.
     * <p>This function is not user-defined. Rather, it's implementation is defined in the source code itself.
     * @param args The arguments.
     * @param parent The outer symbol table.
     * @return The result.
     * @throws RTException If the native implementation is not found.
     */
    private ComplexNum execNative(List<Node> args, SymbolTable parent) {
        ComplexNum first, second;
        //There is at least one argument for each function
        first = getArg(args, 0, parent);
        //For second arg, we need to check
        if (parameters.size() == 2) {
            second = getArg(args, 1, parent);
        } else {
            second = null;
        }

        return switch (name) {
            case "root" -> first.root(Objects.requireNonNull(second));
            case "sin" -> Trigonometry.sin(first);
            case "cos" -> Trigonometry.cos(first);
            case "tan" -> Trigonometry.tan(first);
            case "cot" -> Trigonometry.cot(first);
            case "sec" -> Trigonometry.sec(first);
            case "cosec" -> Trigonometry.cosec(first);
            case "asin" -> Trigonometry.asin(first);
            case "acos" -> Trigonometry.acos(first);
            case "atan" -> Trigonometry.atan(first);
            case "acot" -> Trigonometry.acot(first);
            case "log" -> first.log(Objects.requireNonNull(second));
            case "floor" -> first.floor();
            case "ceil" -> first.ceil();
            case "arg" -> new ComplexNum(first.argument(), 0);
            default -> throw new RTException(
                    "Native function implementation not available for function: '" + name + "'."
            );
        };
    }

    public String toString() {
        String params = parameters.stream()
                .map(Parameter::toString)
                .toList()
                .toString();

        String body = isNative() ? "native" : "= " + expr;
        return "fn " + name + "(" + params.substring(1, params.length() - 1) + ") " + body;
    }
}
