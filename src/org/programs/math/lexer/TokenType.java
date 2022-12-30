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
     * Exponentiation sign. (^)
     */
    POW("^"),

    COMPLEMENT("~"),

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
     * Expression end. Serves as separator between 2 expressions.
     */
    EXPRESSION_END(";"),

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
