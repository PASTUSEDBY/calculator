package org.programs.math.parser;

import org.programs.math.exceptions.*;
import org.programs.math.extra.Result;
import org.programs.math.types.*;
import org.programs.math.lexer.*;
import org.programs.math.nodes.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * The Parser takes the converted tokens from the {@code Lexer} and does a syntax analysis on the
 * tokens. It checks if the given math expression is a valid math expression, and groups the expressions
 * based on the precedence of the operators.
 *
 * <p>For example, an expression {@code 450 - 30 + 6 * 9} is grouped up such that multiplication evaluates
 * before subtraction and addition.
 *
 * <p>The tokens are parsed and an AST is returned (Abstract Syntax Tree). To read more about AST, check
 * out {@link Node} class.
 */
public final class Parser {

    /**
     * The list of tokens.
     */
    public final List<Token<?>> tokens;

    /**
     * The current position at which the {@code Parser} is currently at in {@link Parser#tokens}.
     */
    private int position;

    /**
     * The current token the lexer is at.
     */
    private Token<?> current;

    /**
     * The current name of the function.
     */
    private String fnName;

    /**
     * The variable names of this function (as defined in the parameter list). Order is not maintained.
     */
    private final HashSet<String> varNames;

    /**
     * Constructs the parser.
     * @param ts List of tokens.
     */
    public Parser(List<Token<?>> ts) {
        tokens = ts;
        varNames = new HashSet<>();
        position = -1;
        advance();
    }

    /**
     * Returns a result containing the AST after parsing the tokens, or an error message if failed.
     * @return The result.
     */
    public Result<List<Node>, String> parse() {
        try {
            return Result.success(expressions());
        } catch (BaseException e) {
            return Result.failure(e.toString());
        }
    }

    /**
     * Advances through the list of tokens. If the list is fully iterated, {@code current} is set to
     * {@code null}.
     */
    private void advance() {
        position++;
        if (position < tokens.size()) {
            current = tokens.get(position);
        } else {
            current = null;
        }
    }

    /**
     * Tries to parse this token as an atom.
     * <p>An atom, by definition here, is a value which has the highest precedence.
     * <p>Precedence : 6
     * <p>Values which can be atoms:
     * <p>- Number
     * <p>- An expression within parenthesis
     * <p>- An expression within pipe (absolute value {@code |x|})
     * <p>- An identifier or a function call.
     * @return A node.
     */
    private Node atom() {
        return switch (current.tokenType) {
            case NUMBER -> {
                NumberNode num = new NumberNode((ComplexNum) current.value);
                advance();
                yield num;
            }
            case LPAREN -> {
                advance();
                Node expr = plusMinus();

                if (!peek(TokenType.RPAREN)) {
                    invalid(')');
                }

                advance();
                yield expr;
            }
            case PIPE -> {
                OpToken op = (OpToken) current;
                advance();
                Node expr = plusMinus();

                if (!peek(TokenType.PIPE)) {
                    invalid('|');
                }

                advance();
                yield new UnaryOpNode(op, expr);
            }
            case IDENTIFIER -> varOrFnCall();
            case KEYWORD -> exprKeywords();
            default -> throw new InvalidSyntaxException(
                    "Unexpected end of input. Expected '+', '-', '(', '|', a number or a function call. Found: " + current.tokenType
            );
        };
    }

    /**
     * Checks if the atom is followed by a factorial operator.
     * <p>Factorial operator precedence: 5
     * @return A node.
     * @see Parser#atom()
     */
    private Node atomF() {
        Node at = atom();
        if (peek(TokenType.FACTORIAL)) {
            at = new UnaryOpNode((OpToken) current, at);
            advance();
        }

        return at;
    }

    private Node atomFI() {
        Node atf = atomF();
        if (matchKeyword("i")) {
            OpToken op = new OpToken(TokenType.MULTIPLY);
            Node i = new NumberNode(ComplexNum.IMAGINARY_UNIT);
            atf = new BinOpNode(atf, op, i);
            advance();
        }

        return atf;
    }

    /**
     * Checks if the atom (optional factorial operator) is followed by a {@link TokenType#POW} operator,
     * and if yes, is followed by a factor.
     * <p>Precedence: 4
     * @return A node.
     * @see Parser#atomF()
     * @see Parser#unarySign()
     */
    private Node power() {
        return binOp(this::atomFI, this::unarySign, TokenType.POW);
    }

    /**
     * Checks if a {@link TokenType#PLUS} or a {@link TokenType#MINUS} operator is followed by another
     * expression.
     * <p>Precedence: 3
     * @return A node.
     */
    private Node unarySign() {
        return switch (current.tokenType) {
            case PLUS, MINUS, COMPLEMENT -> {
                Token<?> op = current;
                advance();
                yield new UnaryOpNode((OpToken) op, unarySign());
            }
            default -> power();
        };
    }

    /**
     * Checks if the current token is followed by an {@link TokenType#IDENTIFIER}
     * or an {@link TokenType#LPAREN}.
     * <p>Precedence: 3
     * <p>Implicit multiplication is something like {@code x(x + 1)}, where it resolves to
     * {@code x * (x + 1)}, and has a higher precedence than normal multiplication.
     * @return A node.
     */
    private Node implicitMulti() {
        return binOp(
                this::unarySign,
                TokenType.LPAREN,
                TokenType.IDENTIFIER,
                TokenType.KEYWORD
        );
    }

    /**
     * Checks if this expression is a binary operation.
     * <p>Precedence: 2
     * <p>List of binary operations of precedence 2:
     * <p>- Multiplication
     * <p>- Division
     * <p>- Integer division
     * <p>- Modulus (Remainder)
     * @return A node.
     */
    private Node multiDiv() {
        return binOp(
                this::implicitMulti,
                TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.INT_DIV
        );
    }

    /**
     * Checks if this expression is a binary operation.
     * <p>Precedence: 1
     * <p>List of binary operations of precedence 1:
     * <p>- Addition
     * <p>- Subtraction.
     * @return A node.
     */
    private Node plusMinus() {
        return binOp(this::multiDiv, TokenType.PLUS, TokenType.MINUS);
    }

    /**
     * Represents variable assignment, like {@code x = 1}.
     * Stuff like {@code x = y = z + 2} is possible, since assignments return values,
     * but it still cannot be put in parentheses, since they are single expression.
     * {@code y + (x = z = 1)} is invalid.
     * @return A node.
     */
    private Node assignment() {
        if (!peek(TokenType.IDENTIFIER) || !peekNext(TokenType.EQUAL)) {
            return plusMinus();
        }

        IdentifierToken id = (IdentifierToken) current;
        String idName = id.value;

        advance(); //identifier

        advance(); //equals

        Node assign = assignment();

        SymbolTable.globalIdentifiers.add(idName);
        return new AssignmentNode(idName, assign);
    }

    /**
     * Parses all the expressions. All expressions are separated by a newline, or a semicolon.
     * Function definitions and assignments are always single expressions.
     * @return A list of nodes.
     */
    private List<Node> expressions() {
        List<Node> exprs = new ArrayList<>();
        while (!peek(TokenType.EOF)) {
            if (peek(TokenType.STATEMENT_END)) {
                advance();
                continue;
            }

            Node node = matchKeyword("fn") ? funcDef() : assignment();
            exprs.add(node);

            if (!peek(TokenType.STATEMENT_END) && !peek(TokenType.EOF)) {
                throw new InvalidSyntaxException(
                        "Unexpected end of input. Expected '+', '-', '*', '/', '//' or '^'. Found: " + current.tokenType
                );
            }

            if (peek(TokenType.EOF)) {
                continue;
            }

            advance();
        }

        return exprs;
    }

    /**
     * Parses a binary operation with the two given functions (both functions should return {@link Node}).
     * This is only parsed when the current token type matches among the given token types.
     * @param funcLeft A supplier function which gives the left operand. (Node)
     * @param funcRight A supplier function which gives the right operand. (Node)
     * @param ops A list of operator token types.
     * @return The resulting node.
     *
     * @see OpToken
     * @see TokenType
     * @see Supplier
     */
    private Node binOp(Supplier<Node> funcLeft, Supplier<Node> funcRight, TokenType ... ops) {
        List<TokenType> OPS = Arrays.asList(ops);
        Node left = funcLeft.get();

        while (OPS.contains(current.tokenType)) {
            Token<?> opToken = current;
            if (!peek(TokenType.LPAREN) && !peek(TokenType.IDENTIFIER)) {
                advance();
            } else {
                opToken = new OpToken(TokenType.MULTIPLY);
            }

            Node right = funcRight.get();
            left = new BinOpNode(left, (OpToken) opToken, right);
        }

        return left;
    }

    /**
     * Parses a binary operation with the given function (function should return {@link Node}).
     * The same supplier function is used to get both the left and right operands.
     * <p>This is same as calling {@code binOp(f, f, ...ops)}.
     * @param func A supplier function which gives the left and right operands. (Node)
     * @param ops A list of operator token types.
     * @return The resulting node.
     *
     * @see Parser#binOp(Supplier, Supplier, TokenType...)
     * @see Supplier
     */
    private Node binOp(Supplier<Node> func, TokenType ... ops) {
        return binOp(func, func, ops);
    }

    /**
     * Checks whether this identifier is a normal variable or a function call.
     * @return The resulting node.
     * @throws InvalidSyntaxException If the syntax is invalid.
     */
    private Node varOrFnCall() {
        IdentifierToken token = (IdentifierToken) current;
        String idName = token.value;
        advance();

        if (SymbolTable.hasVar(idName, varNames)) {
            //This is an identifier
            return new IdentifierNode(idName, fnName);
        }

        //That extra check was necessary bcz someone can do variable(variable + 1)
        //thinking it as multiplication
        //in a function
        if (!peek(TokenType.LPAREN)) {
            return new IdentifierNode(idName, fnName);
        }
        advance();

        List<Node> exprs = new ArrayList<>();
        while (!peek(TokenType.RPAREN) && !peek(TokenType.EOF)) {
            Node expr = plusMinus();
            exprs.add(expr);

            if (peek(TokenType.RPAREN) || peek(TokenType.EOF)) continue;

            if (!peek(TokenType.COMMA)) {
                invalid(',', false);
            }

            advance();
        }

        if (peek(TokenType.EOF)) {
            invalid(')');
        }

        if (peekBack(TokenType.COMMA)) {
            throw new InvalidSyntaxException(
                    "Expression expected after ','. Found: " + current.tokenType
            );
        }

        advance();
        return new FuncCallNode(idName, exprs);
    }

    /**
     * Parses this statement as a function definition.
     * <p>A function definition generally looks like:
     * <blockquote><pre>
     *     fn f(x, y, z=some default value) native
     *     fn f(x, y, z=some default value) -> x+y+z
     * </pre></blockquote>
     *
     * @throws InvalidSyntaxException If the syntax is invalid.
     * @throws IdentifierExistsException If the function is already defined, or the variable in the
     * parameter list is already defined.
     * @throws ReqAfterOptionalException If an optional parameter is followed by a required parameter.
     */
    private Node funcDef() {
        //fn x(a, b, ..., n=expr) ((native) | (-> expr))
        advance(); //fn
        if (!peek(TokenType.IDENTIFIER)) {
            throw new InvalidSyntaxException(
                    "Identifier name expected. Found: " + current
            );
        }
        IdentifierToken id = (IdentifierToken) current;
        fnName = id.value;

        advance();

        if (!peek(TokenType.LPAREN)) {
            invalid('(', false);
        }
        advance();

        List<Parameter> parameters = new ArrayList<>();
        boolean hasDefault = false;
        while (!peek(TokenType.RPAREN) && !peek(TokenType.EOF)) {
            if (!peek(TokenType.IDENTIFIER)) {
                throw new InvalidSyntaxException(
                        "Identifier name expected. Found: " + current.tokenType
                );
            }

            IdentifierToken varID = (IdentifierToken) current;
            Node defaultExpr = null;

            if (varNames.contains(varID.value)) {
                throw new IdentifierExistsException(varID.value, false);
            }

            varNames.add(varID.value);

            advance();
            if (peek(TokenType.EQUAL)) {
                advance();
                defaultExpr = plusMinus();
                hasDefault = true;
            }

            if (hasDefault && defaultExpr == null) {
                throw new ReqAfterOptionalException(fnName);
            }

            Parameter argParam = new Parameter(varID.value, defaultExpr, fnName);
            parameters.add(argParam);

            if (peek(TokenType.RPAREN) || peek(TokenType.EOF)) continue;

            if (!peek(TokenType.COMMA)) {
                invalid(',', false);
            }

            advance();
        }

        if (peek(TokenType.EOF)) {
            invalid(')');
        }

        if (peekBack(TokenType.COMMA)) {
            throw new InvalidSyntaxException(
                    "Identifier expected after ','. Found: " + current.tokenType
            );
        }
        advance();

        if (matchKeyword("native")) {
            advance();
            cleanUp();
            return new FuncDefNode(id.value, parameters);
        }

        if (!peek(TokenType.EQUAL)) { //not equal for expression body
            throw new InvalidSyntaxException(
                    "Expected '='. Found: " + current.tokenType
            );
        }
        advance();

        Node bodyExpr = plusMinus();
        cleanUp();

        return new FuncDefNode(id.value, parameters, bodyExpr);
    }

    /**
     * Parses some predefined keywords as expressions.
     * @return A node.
     */
    private Node exprKeywords() {
        if (matchKeyword("sum", "\u03A3", "\u03A0", "product")) {
            return parseSigmaOrPi();
        }

        if (matchKeyword("pi", "\u03C0")) {
            advance();
            return new NumberNode(ComplexNum.PI);
        }

        if (matchKeyword("e")) {
            advance();
            return new NumberNode(ComplexNum.E);
        }

        if (matchKeyword("i")) {
            advance();
            return new NumberNode(ComplexNum.IMAGINARY_UNIT);
        }

        throw new InvalidSyntaxException("Unexpected keyword: " + current);
    }

    /**
     * Parses the summation or product.
     * @return A node.
     */
    private Node parseSigmaOrPi() {
        //sum | product (variable=init, upto, expression)
        SigmaPiNode.Type type;

        if (matchKeyword("sum", "\u03A3")) {
            type = SigmaPiNode.Type.SIGMA;
        } else {
            type = SigmaPiNode.Type.PI;
        }
        advance(); //sum | product: KW
        if (!peek(TokenType.LPAREN)) {
            invalid('(', false);
        }

        advance();

        if (!peek(TokenType.IDENTIFIER)) {
            throw new InvalidSyntaxException("Expected variable name. Found: " + current.tokenType);
        }

        IdentifierToken varID = (IdentifierToken) current;
        if (varNames.contains(varID.value)) {
            throw new IdentifierExistsException(varID.value, false);
        }

        varNames.add(varID.value);

        advance();

        if (!peek(TokenType.EQUAL)) {
            invalid('=', false);
        }

        advance();

        Parameter init = new Parameter(varID.value, plusMinus(), fnName);


        if (!peek(TokenType.COMMA)) {
            invalid(',', false);
        }

        advance();

        Node upto = plusMinus();

        if (!peek(TokenType.COMMA)) {
            invalid(',', false);
        }

        advance();

        Node rExpr = plusMinus();

        if (!peek(TokenType.RPAREN)) {
            invalid(')');
        }

        advance();
        varNames.remove(varID.value);

        return new SigmaPiNode(init, upto, rExpr, type);
    }

    /**
     * Throws a {@link InvalidSyntaxException} for unexpected end of input.
     * @param expected The character expected, but was not found.
     * @param end If the character was to be expected at the end of this expression.
     */
    private static void invalid(char expected, boolean end) {
        throw new InvalidSyntaxException("Unexpected end of input. Expected '" + expected + (end ? "' at end." : "'."));
    }

    /**
     * This is same as calling {@link Parser#invalid(char, boolean)} with the second parameter as {@code true}.
     * @param expected The character expected, but was not found.
     */
    private static void invalid(char expected) {
        invalid(expected, true);
    }

    /**
     * Checks if the current token is a keyword and of the same name.
     * @param names The name of the keyword.
     * @return {@code true} if all the conditions satisfy.
     */
    private boolean matchKeyword(String ... names) {
        if (!peek(TokenType.KEYWORD)) {
            return false;
        }

        for (String name : names) {
            if (current.value.equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a token exists in the previous position.
     * @param tt The token type.
     * @return {@code true} if the condition satisfies.
     */
    private boolean peekBack(TokenType tt) {
        if (position == 0) return false;

        return tokens.get(position - 1).tokenType == tt;
    }

    /**
     * Checks if the current token is equal to the given one.
     * @param tt The token type.
     * @return {@code true} If the condition satisfies.
     */
    private boolean peek(TokenType tt) {
        return current.tokenType == tt;
    }

    /**
     * Checks if the next token is equal to the given one.
     * @param tt The token type.
     * @return {@code true} If the condition satisfies.
     */
    private boolean peekNext(TokenType tt) {
        if (position + 1 >= tokens.size()) return false;
        Token<?> token = tokens.get(position + 1);

        return token.tokenType == tt;
    }

    /**
     * Cleans up. Nothing else.
     * <p>Resets {@code fnName} and {@code varNames} after the function definition.
     */
    private void cleanUp() {
        SymbolTable.globalIdentifiers.remove(fnName);
        fnName = null;
        varNames.clear();
    }
}
