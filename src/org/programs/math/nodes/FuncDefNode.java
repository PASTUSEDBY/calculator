package org.programs.math.nodes;

import org.programs.math.parser.SymbolTable;
import org.programs.math.types.Func;
import org.programs.math.types.NativeFunc;
import org.programs.math.types.Parameter;
import org.programs.math.types.TNumber;

import java.util.List;

/**
 * A node which represents defining a function. Example: fn f(x) = x + 2
 */
public class FuncDefNode implements Node {
    /**
     * The name of the function.
     */
    public final String funcName;

    /**
     * The parameters of this function.
     */
    public final List<Parameter> parameters;

    /**
     * The body expression of this function.
     * {@code null} if native function.
     */
    public final Node expr;

    /**
     * Creates a function definition node.
     * @param name The name of the function.
     * @param ps The parameters.
     * @param e The expression.
     */
    public FuncDefNode(String name, List<Parameter> ps, Node e) {
        funcName = name;
        parameters = ps;
        expr = e;
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
     * Makes a new function with the data, and stores it in the global symbol table.
     * @param st The symbol table of this scope.
     * @return
     */
    @Override
    public TNumber visit(SymbolTable st) {
        Func fn = expr != null ?
                new Func(funcName, expr, parameters) :
                new NativeFunc(funcName, parameters);

        st.set(funcName, fn);

        return null;
    }
}
