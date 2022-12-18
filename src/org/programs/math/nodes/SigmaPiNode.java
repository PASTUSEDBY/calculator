package org.programs.math.nodes;

import org.programs.math.exceptions.IdentifierExistsException;
import org.programs.math.parser.SymbolTable;
import org.programs.math.types.Parameter;
import org.programs.math.types.TNumber;

public class SigmaPiNode implements Node {
    public enum Type {
        SIGMA,
        PI
    }

    public final Type type;
    public final Parameter init;
    public final Node upto;

    public final Node evaluationExpr;

    public SigmaPiNode(Parameter i, Node u, Node e, Type t) {
        init = i;
        upto = u;
        evaluationExpr = e;
        type = t;
    }

    @Override
    public TNumber visit(SymbolTable st) {
        String name = init.getName();
        if (
                st.contains(name, false) ||
                st.isGlobal() && SymbolTable.globalIdentifiers.contains(name)
        ) {
            throw new IdentifierExistsException(init.name, false);
        }

        TNumber initial = init.defaultVal.visit(st);
        TNumber upto = this.upto.visit(st);

        TNumber result = new TNumber(type == Type.SIGMA ? 0 : 1);

        while (initial.value <= upto.value) {
            st.set(name, initial);
            TNumber evaluated = evaluationExpr.visit(st);
            result = type == Type.SIGMA ? result.add(evaluated) : result.multiply(evaluated);

            initial = initial.add(new TNumber(1));
        }

        st.remove(name);

        return result;
    }

    @Override
    public String toString() {
        return "\u03A3 ("
                + init.name + "=" + init.defaultVal
                + ", " + upto
                + ", " + evaluationExpr
                + ")";
    }
}
