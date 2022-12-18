package org.programs.math.lexer;

import org.programs.math.types.TNumber;

/**
 * Represents a number token.
 */
public class NumToken extends Token<TNumber> {
    /**
     * Constructs a Number Token.
     * @param value The number value.
     */
    public NumToken(TNumber value) {
        super (TokenType.NUMBER, value);
    }
}
