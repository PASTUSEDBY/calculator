package org.programs.math.types;

import org.programs.math.exceptions.RTException;
import org.programs.math.nodes.Node;
import org.programs.math.parser.SymbolTable;

import java.util.List;
import java.util.Objects;

/**
 * Represents a native function.
 * <p>This function is not user-defined. Rather, it's implementation is defined in the source code itself.
 * <p>Functions which have the {@code native} keyword after it's parameter list instead of body expression,
 *    is a native function.
 * <p>
 *
 * @see Func
 */
public class NativeFunc extends Func {
    /**
     * Constructs a native function.
     * @param name The name of the function.
     * @param as The parameter list.
     */
    public NativeFunc(String name, List<Parameter> as) {
        super (name, null, as);
    }

    /**
     * Executes this native function with the given arguments.
     * @param args The arguments provided to the function.
     * @param parent The outer symbol table.
     * @return The evaluated number.
     * @throws RTException If the native function implementation is not defined.
     */
    @Override
    public TNumber execute(List<Node> args, SymbolTable parent) {
        TNumber first, second;
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
            case "sin" -> first.sin();
            case "cos" -> first.cos();
            case "tan" -> first.tan();
            case "log" -> first.log(Objects.requireNonNull(second));
            case "floor" -> first.floor();
            case "ceil" -> first.ceil();
            case "approx" -> {
                Objects.requireNonNull(second);
                if (second.value < 0) {
                    throw new RTException("Approximation decimal count is less than zero.");
                }

                double power = Math.pow(10, second.value), val = first.value * power;
                yield new TNumber(Math.round(val) / power);
            }
            default -> throw new RTException("Native function implementation not available for function: '" + name + "'.");
        };
    }
}
