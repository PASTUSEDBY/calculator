package org.programs.math.nodes;

import org.programs.math.exceptions.NoSuchIdentifierException;
import org.programs.math.types.ComplexNum;
import org.programs.math.parser.SymbolTable;
import org.programs.math.types.Value;

/**
 * A Node which represents an identifier (or a variable).
 */
public class IdentifierNode implements Node {
    /**
     * The name of the identifier.
     */
    public final String idName;

    private final String fnName;

    /**
     * Creates an identifier node.
     * @param idName The name of the identifier.
     * @param fnName The name of the function it is currently in (if any).
     */
    public IdentifierNode(String idName, String fnName) {
        this.idName = idName;
        this.fnName = fnName;
    }

    /**
     * {@inheritDoc}
     * Returns the identifier's value from the symbol table.
     * @param st The symbol table of this scope.
     * @return
     * @throws NoSuchIdentifierException If the identifier does not exist.
     */
    @Override
    public ComplexNum visit(SymbolTable st) {
        String varName = SymbolTable.makeVarName(idName, fnName);

        if (st.contains(varName, false)) {
            return (ComplexNum) st.get(varName, false);
        }

        Value v = st.get(idName, true);

        if (v instanceof ComplexNum) {
            return (ComplexNum) v;
        }

        throw new NoSuchIdentifierException(idName, false);
    }

    @Override
    public String toString() {
        return idName;
    }
}
