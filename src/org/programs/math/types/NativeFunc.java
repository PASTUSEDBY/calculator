package org.programs.math.types;

import org.programs.math.exceptions.RTException;
import org.programs.math.extra.Trigonometry;
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
    public ComplexNum execute(List<Node> args, SymbolTable parent) {
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
            default -> throw new RTException("Native function implementation not available for function: '" + name + "'.");
        };
    }
}
