package org.programs.math.nodes;

import org.programs.math.types.ComplexNum;
import org.programs.math.parser.SymbolTable;

/**
 * A Node which holds a number. Yes, nothing else.
 */
public class NumberNode implements Node {
    /**
     * The number.
     */
    public final ComplexNum num;

    /**
     * Creates a number node.
     * @param t The number token.
     */
    public NumberNode(ComplexNum t) {
        num = t;
    }

    /**
     * {@inheritDoc}
     * Returns the constant number value.
     * @param st The symbol table of this scope.
     * @return
     */
    @Override
    public ComplexNum visit(SymbolTable st) {
        return num;
    }

    public String toString() {
        return num.toString();
    }
}
