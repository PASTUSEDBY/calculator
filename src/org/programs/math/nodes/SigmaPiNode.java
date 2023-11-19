package org.programs.math.nodes;

import org.programs.math.exceptions.IdentifierExistsException;
import org.programs.math.exceptions.RTException;
import org.programs.math.parser.SymbolTable;
import org.programs.math.types.ComplexNum;
import org.programs.math.types.Parameter;

import static org.programs.math.types.ComplexNum.REAL_UNIT;
import static org.programs.math.types.ComplexNum.ZERO;

/**
 * A Node which represents summation or product. Eg - sum(x = 1, 10, x)
 */
public class SigmaPiNode implements Node {
    /**
     * The type of this operation (whether summation or product).
     */
    public enum Type {
        SIGMA,
        PI;

        public String toString() {
            return this == PI ? "product" : "sum";
        }
    }

    /**
     * The type of this operation.
     */
    public final Type type;

    /**
     * Parameter to store the initial details.
     */
    public final Parameter init;

    /**
     * The end expression.
     */
    public final Node upto;

    /**
     * The expression to evaluate.
     */
    public final Node evaluationExpr;

    /**
     * Constructs a new node.
     * @param i The initial parameter.
     * @param u The end number.
     * @param e The expression.
     * @param t The type of the operation.
     */
    public SigmaPiNode(Parameter i, Node u, Node e, Type t) {
        init = i;
        upto = u;
        evaluationExpr = e;
        type = t;
    }

    /**
     * {@inheritDoc}
     * Returns the result after evaluation.
     * @param st The symbol table of this scope.
     * @return The sum or product.
     * @throws RTException If the first and second parameters are non real.
     */
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

        try {
            while (initial.real <= upto.real) {
                st.set(name, initial);
                ComplexNum evaluated = evaluationExpr.visit(st);
                result = type == Type.SIGMA ? result.add(evaluated) : result.multiply(evaluated);

                initial = initial.add(REAL_UNIT);
            }
        } finally {
            st.remove(name);
        }

        return result;
    }

    public String toString() {
        return "" + type + "("
                + init + ", "
                + upto + ", "
                + evaluationExpr + ")";
    }
}
