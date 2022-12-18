package org.programs.math.lexer;

/**
 * Represents an operator token.
 */
public class OpToken extends Token<Void> {
    /**
     * Constructs an Operator Token.
     * @param tt The token type.
     */
    public OpToken(TokenType tt) {
        super(tt, null);
    }
}
