package org.programs.math.lexer;

import org.programs.math.types.ComplexNum;

/**
 * Represents a number token.
 */
public class NumToken extends Token<ComplexNum> {
    /**
     * Constructs a Number Token.
     * @param value The number value.
     */
    public NumToken(ComplexNum value) {
        super (TokenType.NUMBER, value);
    }
}
