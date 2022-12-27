package org.programs.math.nodes;

import org.programs.math.lexer.OpToken;
import org.programs.math.parser.SymbolTable;
import org.programs.math.types.ComplexNum;

/**
 * A Node which represents unary operations.
 * <p>List of unary operations:
 * <p>
 * <p><p>{@code +} - Makes a number positive (Without this sign, all numbers are positive).
 * <p><p>{@code -} - Negates a number.
 * <p><p>{@code |number|} - Gives the absolute value of a number.
 * <p><p>{@code !} - Gives the factorial of a number.
 */
public class UnaryOpNode implements Node {
    /**
     * The operator.
     */
    public final OpToken op;

    /**
     * The operand.
     */
    public final Node node;

    /**
     * Creates a unary operation node.
     * @param t The operator token.
     * @param n The operand node.
     */
    public UnaryOpNode(OpToken t, Node n) {
        op = t;
        node = n;
    }

    /**
     * {@inheritDoc}
     * Returns the result after the unary operation.
     * @param st The symbol table of this scope.
     * @return
     */
    @Override
    public ComplexNum visit(SymbolTable st) {
        ComplexNum num = node.visit(st);
        return switch (op.tokenType) {
            case MINUS -> num.negate();
            case PIPE -> new ComplexNum(num.modulus(), 0);
            case FACTORIAL -> num.factorial();
            case COMPLEMENT -> num.conjugate();
            default -> num;
        };
    }

    public String toString() {
        return switch (op.tokenType) {
            case PIPE -> "|" + node + "|";
            case FACTORIAL -> "(" + node + ")!";
            default -> "(" + op.tokenType + node + ")";
        };
    }
}
