package org.programs.math.nodes;

import org.programs.math.exceptions.IdentifierExistsException;
import org.programs.math.parser.SymbolTable;
import org.programs.math.types.Parameter;
import org.programs.math.types.ComplexNum;

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
    public ComplexNum visit(SymbolTable st) {
        String name = init.getName();
        if (
                st.contains(name, false) ||
                st.isGlobal() && SymbolTable.globalIdentifiers.contains(name)
        ) {
            throw new IdentifierExistsException(init.name, false);
        }

        ComplexNum initial = init.defaultVal.visit(st);
        ComplexNum upto = this.upto.visit(st);

        ComplexNum result = new ComplexNum(type == Type.SIGMA ? 0 : 1, 0);

        while (initial.real <= upto.real) {
            st.set(name, initial);
            ComplexNum evaluated = evaluationExpr.visit(st);
            result = type == Type.SIGMA ? result.add(evaluated) : result.multiply(evaluated);

            initial = initial.add(ComplexNum.REAL_UNIT);
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
