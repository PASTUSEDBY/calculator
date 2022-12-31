package org.programs.math.nodes;

import org.programs.math.exceptions.IdentifierExistsException;
import org.programs.math.exceptions.RTException;
import org.programs.math.parser.SymbolTable;
import org.programs.math.types.ComplexNum;
import org.programs.math.types.Parameter;

import static org.programs.math.types.ComplexNum.REAL_UNIT;
import static org.programs.math.types.ComplexNum.ZERO;

public class SigmaPiNode implements Node {
    public enum Type {
        SIGMA,
        PI;

        public String toString() {
            return this == PI ? "product" : "sum";
        }
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
        String name = init.name;
        if (st.contains(name, false)) {
            throw new IdentifierExistsException(name, false);
        }

        ComplexNum initial = init.defaultVal.visit(st);
        ComplexNum upto = this.upto.visit(st);

        if (!initial.isReal() || !upto.isReal()) {
            throw new RTException("Sum or product's first two parameters must be real!");
        }

        ComplexNum result = type == Type.SIGMA ? ZERO : REAL_UNIT;

        while (initial.real <= upto.real) {
            st.set(name, initial);
            ComplexNum evaluated = evaluationExpr.visit(st);
            result = type == Type.SIGMA ? result.add(evaluated) : result.multiply(evaluated);

            initial = initial.add(REAL_UNIT);
        }

        st.remove(name);

        return result;
    }

    public String toString() {
        return "" + type + "("
                + init + ", "
                + upto + ", "
                + evaluationExpr + ")";
    }
}
