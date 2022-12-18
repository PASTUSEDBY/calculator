package org.programs.math.lexer;

/**
 * Represents an identifier token.
 */
public class IdentifierToken extends Token<String> {
    /**
     * Constructs an Identifier Token.
     * @param tt The token type. Either {@code KEYWORD} or {@code IDENTIFIER}.
     * @param name The name of the identifier.
     */
    public IdentifierToken(TokenType tt, String name) {
        super (tt, name);
    }
}
