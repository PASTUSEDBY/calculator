package org.programs.math.lexer;

/**
 * A Token represents the smallest unit of the grammar with some meaning.
 * <p>This class is abstract, and is extended by other classes.
 * @param <T> The type of the value contained in the token.
 *
 * @see NumToken
 * @see OpToken
 * @see IdentifierToken
 */
public abstract class Token<T> {

    /**
     * The type of the token.
     */
    public final TokenType tokenType;

    /**
     * The value of the token.
     */
    public final T value;

    /**
     * Constructs a token.
     * @param tt The token type.
     * @param value The token value.
     */
    public Token(TokenType tt, T value) {
        tokenType = tt;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tokenType);
        if (value != null) {
            sb.append(":").append(value);
        }
        return sb.toString();
    }
}
