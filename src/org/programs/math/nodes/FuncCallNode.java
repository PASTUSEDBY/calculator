package org.programs.math.nodes;

import org.programs.math.exceptions.InvalidArgsException;
import org.programs.math.exceptions.NoSuchIdentifierException;
import org.programs.math.types.Func;
import org.programs.math.types.TNumber;
import org.programs.math.parser.SymbolTable;
import org.programs.math.types.Value;

import java.util.List;

/**
 * A Node which represents calling a function. Example: f(x)
 */
public class FuncCallNode implements Node {
    /**
     * The arguments given to this function (a list of nodes).
     */
    public final List<Node> args;

    /**
     * The name of the function which is being called.
     */
    public final String name;

    /**
     * Creates a function call node.
     * @param name The function name.
     * @param args The arguments passed to it.
     */
    public FuncCallNode(String name, List<Node> args) {
        this.name = name;
        this.args = args;
    }

    /**
     * {@inheritDoc}
     * Executes the function with the given arguments and returns the evaluated expression.
     * @param st The symbol table of this scope.
     * @return
     * @throws NoSuchIdentifierException If the function with the name does not exist.
     * @throws InvalidArgsException If the given number of arguments does not match with the function arguments.
     */
    @Override
    public TNumber visit(SymbolTable st) {
        if (!st.contains(name, true)) {
            throw new NoSuchIdentifierException(name, true);
        }

        Value v =  st.get(name, true);
        if (!(v instanceof Func)) {
            throw new NoSuchIdentifierException(name, true);
        }

        Func fn = (Func) v;

        if (args.size() < fn.min) {
            throw new InvalidArgsException(fn.min, args.size(), true);
        }

        if (args.size() > fn.max) {
            throw new InvalidArgsException(fn.max, args.size(), false);
        }

        return fn.execute(args, st);
    }

    public String toString() {
        return "FnCall (" + name + ", " + args + ")";
    }
}
