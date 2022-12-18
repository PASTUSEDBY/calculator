package org.programs.math.lexer;

/**
 * The types of tokens.
 */
public enum TokenType {
    /**
     * An identifier.
     */
    IDENTIFIER,

    /**
     * A keyword.
     */
    KEYWORD,

    /**
     * A comma.
     */
    COMMA(","),

    /**
     * A real number.
     */
    NUMBER,

    /**
     * Equals sign (=).
     */
    EQUAL("="),

    /**
     * Plus sign (+).
     */
    PLUS("+"),

    /**
     * Minus sign (-).
     */
    MINUS("-"),

    /**
     * Multiplication sign. (*)
     */
    MULTIPLY("*"),

    /**
     * Division sign. (/)
     */
    DIVIDE("/"),

    /**
     * Integer division sign. Returns the integer value and truncates the fractional part. (//)
     */
    INT_DIV("//"),

    /**
     * Modulus sign. Gives the remainder. (%)
     */
    MODULUS("%"),

    /**
     * Exponentiation sign. (^)
     */
    POW("^"),

    /**
     * Left parenthesis. ( ( )
     */
    LPAREN("("),

    /**
     * Right parenthesis. ( ) )
     */
    RPAREN(")"),

    /**
     * Pipe operator. Absolute value. (| |)
     */
    PIPE("|"),

    /**
     * Factorial operator. (!)
     */
    FACTORIAL("!"),

    /**
     * Statement end. Either semicolon or new line.
     */
    STATEMENT_END,

    /**
     * End Of File. This marks the end of the given input.
     */
    EOF;

    /**
     * The symbol of this token. (If any).
     */
    public final String symbol;

    TokenType(String s) {
        symbol = s;
    }

    TokenType() {
        this(null);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return symbol == null ? super.toString() : symbol;
    }
}