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

    /**
     * Whether the variable should be queried locally or globally.
     * If the variable is not found in local, it fallbacks to global.
     */
    private final boolean isGlobal;

    /**
     * Creates an identifier node.
     * @param idName The name of the identifier.
     */
    public IdentifierNode(String idName, boolean g) {
        this.idName = idName;
        isGlobal = g;
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
        if (!isGlobal) {
            if (st.contains(idName, false)) {
                return (ComplexNum) st.get(idName, false);
            }
        }

        //Fallback to global

        Value v = st.get(idName, true);

        if (v instanceof ComplexNum) {
            return (ComplexNum) v;
        }

        throw new NoSuchIdentifierException(idName, false);

        /*if (st.contains(idName, false)) {
            return (ComplexNum) st.get(idName, false);
        }

        Value v = st.get(idName, true);

        if (v instanceof ComplexNum) {
            return (ComplexNum) v;
        }

        throw new NoSuchIdentifierException(idName, false);*/
    }

    @Override
    public String toString() {
        return idName;
    }
}
