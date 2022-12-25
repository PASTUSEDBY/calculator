package org.programs.math.lexer;

import org.programs.math.exceptions.IllegalCharException;
import org.programs.math.types.ComplexNum;
import org.programs.math.extra.Result;

import java.util.*;

/**
 * The Lexer converts the given String input into a series of tokens.
 * This is helpful for the next part of parsing these tokens.
 * The Lexer also checks and raises an error upon encountering an invalid token.
 */
public final class Lexer {
    private static final HashSet<String> keywords = new HashSet<>(
            Arrays.asList(
                    "fn",
                    "native",
                    "sum",
                    "\u03A3", //sigma symbol
                    "pi",
                    "\u03C0", //pi symbol
                    "e",
                    "i",
                    "product",
                    "\u03A0" //product symbol
            )
    );

    /**
     * The String input.
     */
    public final String text;

    /**
     * The current position at which the lexer is currently at in the {@link Lexer#text}.
     */
    private int position;

    /**
     * The length of the input.
     */
    private final int length;

    /**
     * The current character the lexer is at.
     */
    private char current;

    /**
     * Creates a lexer based on the input.
     * @param input The input string.
     */
    public Lexer(String input) {
        text = input;
        length = text.length();
        position = -1;
        advance();
    }

    /**
     * Advances through the input. If the input is fully iterated, {@code current} is set to {@code null}.
     */
    private void advance() {
        position++;
        if (notEnded()) {
            current = text.charAt(position);
        }
    }

    /**
     * Iterates through the string and converts the characters into meaningful tokens.
     * @return An unmodifiable list of tokens.
     */
    public Result<List<Token<?>>, String> lex() {
        List<Token<?>> tokens = new ArrayList<>();

        while (notEnded()) {
            try {
                Token<?> token = getToken();
                advance();
                if (token == null) {
                    continue;
                }

                tokens.add(token);
            } catch (IllegalCharException e) {
                return Result.failure(e.toString());
            }
        }

        tokens.add(operator(TokenType.EOF));
        return Result.success(Collections.unmodifiableList(tokens));
    }

    /**
     * Tries to convert the current part of the input to a meaningful token.
     * @return The token.
     * @throws IllegalCharException If the current part of input cannot be converted to a token.
     */
    private Token<?> getToken() {
        return switch (current) {
            case '+' -> operator(TokenType.PLUS);
            case '-' -> operator(TokenType.MINUS);
            case '*' -> operator(TokenType.MULTIPLY);
            case '/' -> {
                if (peekNext('/')) {
                    yield operator(TokenType.INT_DIV);
                }
                yield operator(TokenType.DIVIDE);
            }
            case '^' -> operator(TokenType.POW);
            case '~' -> operator(TokenType.COMPLEMENT);
            case '(' -> operator(TokenType.LPAREN);
            case ')' -> operator(TokenType.RPAREN);
            case '|' -> operator(TokenType.PIPE);
            case '!' -> operator(TokenType.FACTORIAL);
            case ' ', '\t' -> null;
            case '.' -> makeNumber();
            case ',' -> operator(TokenType.COMMA);
            case ';', '\n' -> operator(TokenType.STATEMENT_END);
            case '=' -> operator(TokenType.EQUAL);
            default -> {
                if (Character.isDigit(current)) {
                    yield makeNumber();
                }
                if (Character.isJavaIdentifierStart(current)) {
                    yield makeIdentifier();
                }
                throw new IllegalCharException(current, position);
            }
        };
    }

    /**
     * Parses the current part of input into a number token.
     * @return The number.
     * @throws IllegalCharException If the number contains unusual decimal dots.
     */
    private NumToken makeNumber() {
        boolean hasDot = false;
        StringBuilder numStr = new StringBuilder();

        while (notEnded() && (Character.isDigit(current) || peek('.'))) {
            if (peek('.')) {
                if (hasDot) {
                    throw new IllegalCharException(current, position);
                }
                hasDot = true;
            }
            numStr.append(current);
            advance();
        }

        goBack();

        String out = numStr.toString();
        if (out.equals(".")) {
            throw new IllegalCharException('.', position);
        }
        //All complex numbers are treated as real when parsed
        ComplexNum num = new ComplexNum(Double.parseDouble(out), 0);

        return new NumToken(num);
    }

    /**
     * Parses the current part of the input into an identifier token.
     * @return The identifier token.
     */
    private IdentifierToken makeIdentifier() {
        StringBuilder idNameStr = new StringBuilder().append(current);
        advance();

        while (notEnded() && Character.isJavaIdentifierPart(current)) {
            idNameStr.append(current);
            advance();
        }

        goBack();

        String idName = idNameStr.toString();
        if (keywords.contains(idName)) {
            return new IdentifierToken(TokenType.KEYWORD, idName);
        }
        return new IdentifierToken(TokenType.IDENTIFIER, idName);
    }

    /**
     * Checks if the given character is equal to the next character from current position.
     * <p>If they are equal, the character is consumed, otherwise not.
     * @param toMatch The character to match against.
     * @return {@code true} if the given character equals, {@code false} otherwise.
     */
    private boolean peekNext(char toMatch) {
        advance();
        boolean isMatch = notEnded() && peek(toMatch);
        if (!isMatch) {
            goBack();
        }
        return isMatch;
    }

    /**
     * Checks if the current character is equal to the given one.
     * @param toMatch The character to match.
     * @return {@code true} If the given character equals to this.
     */
    private boolean peek(char toMatch) {
        return current == toMatch;
    }

    /**
     * Decreases the {@code position} by 1. If position is 0, does nothing.
     */
    private void goBack() {
        if (position == 0) {
            return;
        }

        current = text.charAt(--position);
    }

    /**
     * Checks if the lexer has ended or not.
     * @return {@code true} if it has not ended.
     */
    private boolean notEnded() {
        return position < length;
    }

    /**
     * Creates an operator token with the given token type.
     * @param tt The token type.
     * @return An OpToken.
     * @see OpToken
     */
    private OpToken operator(TokenType tt) {
        return new OpToken(tt);
    }
}
