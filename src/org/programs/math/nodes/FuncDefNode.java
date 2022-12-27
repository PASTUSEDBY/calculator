package org.programs.math.nodes;

import org.programs.math.parser.SymbolTable;
import org.programs.math.types.ComplexNum;
import org.programs.math.types.Func;
import org.programs.math.types.NativeFunc;
import org.programs.math.types.Parameter;

import java.util.List;

/**
 * A node which represents defining a function. Example: fn f(x) = x + 2
 */
public class FuncDefNode implements Node {
    /**
     * The function defined.
     */
    public final Func fn;

    /**
     * Creates a function definition node, and creates a new function with the data.
     * @param name The name of the function.
     * @param ps The parameters.
     * @param e The expression.
     */
    public FuncDefNode(String name, List<Parameter> ps, Node e) {
        if (e == null) {
            fn = new NativeFunc(name, ps);
        } else {
            fn = new Func(name, e, ps);
        }
    }

    /**
     * Creates a function definition node.
     * @param name The name of the function.
     * @param ps The parameters.
     */
    public FuncDefNode(String name, List<Parameter> ps) {
        this(name, ps, null);
    }

    /**
     * {@inheritDoc}
     * Stores the function in the global symbol table.
     * @param st The symbol table of this scope.
     * @return
     */
    @Override
    public ComplexNum visit(SymbolTable st) {
        st.set(fn.name, fn);

        return null;
    }

    public String toString() {
        return fn.toString();
    }
}
