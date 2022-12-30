package org.programs.math.nodes;

import org.programs.math.parser.SymbolTable;
import org.programs.math.types.ComplexNum;

/**
 * A node which represents assigning values to variables.
 */
public class AssignmentNode implements Node {
    /**
     * The name of the identifier.
     */
    public final String idName;

    /**
     * The body expression.
     */
    public final Node expr;

    /**
     * Constructs a new assignment node.
     * @param name The name of the identifier.
     * @param e The expression.
     */
    public AssignmentNode(String name, Node e) {
        idName = name;
        expr = e;
    }

    /**
     * {@inheritDoc}
     * Evaluates the expression, and stores this value to the
     * specified identifier in the global symbol table.
     * @param st The symbol table of this scope.
     * @return
     */
    @Override
    public ComplexNum visit(SymbolTable st) {
        SymbolTable.check(idName);
        ComplexNum num = expr.visit(st);
        st.set(idName, num);

        return num;
    }

    public String toString() {
        return idName + " = " + expr;
    }
}
