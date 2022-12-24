package org.programs.math.nodes;

import org.programs.math.types.ComplexNum;
import org.programs.math.lexer.OpToken;
import org.programs.math.parser.SymbolTable;

/**
 * A Node which represents a binary operation.
 * <p>List of binary operations:
 * <p>
 * <p><p>{@code +} - Addition.
 * <p><p>{@code -} - Subtraction.
 * <p><p>{@code *} - Multiplication.
 * <p><p>{@code /} - Division.
 * <p><p>{@code //} - Integer Division (Gives the quotient in integer form by truncating the fractional part).
 * <p><p>{@code %} - Modulus (Gives the remainder).
 * <p><p>{@code ^} - Exponentiation.
 */
public class BinOpNode implements Node {
    /**
     * The left operand.
     */
    public final Node left;

    /**
     * The operator.
     */
    public final OpToken op;

    /**
     * The right operand.
     */
    public final Node right;

    /**
     * Creates a binary operation node.
     * @param l The left node.
     * @param o The operator token.
     * @param r The right node.
     */
    public BinOpNode(Node l, OpToken o, Node r) {
        left = l;
        op = o;
        right = r;
    }

    /**
     * {@inheritDoc}
     * Returns the result after the binary operation.
     * @param st The symbol table of this scope.
     * @return
     * @throws RuntimeException This should never happen.
     */
    @Override
    public ComplexNum visit(SymbolTable st) {
        ComplexNum leftNum = left.visit(st);
        ComplexNum rightNum = right.visit(st);

        return switch (op.tokenType) {
            case PLUS -> leftNum.add(rightNum);
            case MINUS -> leftNum.subtract(rightNum);
            case MULTIPLY -> leftNum.multiply(rightNum);
            case DIVIDE -> leftNum.divide(rightNum);
            case INT_DIV -> leftNum.intDivide(rightNum);
            case POW -> leftNum.pow(rightNum);
            default -> throw new RuntimeException("This should never happen!");
        };
    }

    public String toString() {
        return "(" + left + op.tokenType + right + ")";
    }
}
