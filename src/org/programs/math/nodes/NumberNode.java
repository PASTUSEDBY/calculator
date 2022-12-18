package org.programs.math.nodes;

import org.programs.math.types.TNumber;
import org.programs.math.parser.SymbolTable;

/**
 * A Node which holds a number. Yes, nothing else.
 */
public class NumberNode implements Node {
    /**
     * The number.
     */
    public final TNumber num;

    /**
     * Creates a number node.
     * @param t The number token.
     */
    public NumberNode(TNumber t) {
        num = t;
    }

    /**
     * {@inheritDoc}
     * Returns the constant number value.
     * @param st The symbol table of this scope.
     * @return
     */
    @Override
    public TNumber visit(SymbolTable st) {
        return num;
    }

    public String toString() {
        return num.toString();
    }
}
