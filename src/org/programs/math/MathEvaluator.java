package org.programs.math;

import org.programs.math.exceptions.BaseException;
import org.programs.math.nodes.AssignmentNode;
import org.programs.math.nodes.Node;
import org.programs.math.types.Result;
import org.programs.math.types.TNumber;
import org.programs.math.lexer.Lexer;
import org.programs.math.parser.Parser;
import org.programs.math.parser.SymbolTable;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public final class MathEvaluator {
    public static final SymbolTable symbolTable;

    static {
        symbolTable = new SymbolTable();
        preload();
        SymbolTable.saveBuiltIns(symbolTable);
    }

    private MathEvaluator() {
        //private
    }

    private static void preload() {
        InputStream in = MathEvaluator.class.getResourceAsStream("BuiltIns.txt");
        Objects.requireNonNull(in, "The file BuiltIns.txt is not provided with this package.");

        try (Scanner sc = new Scanner(in)) {
            String builtIn = getInput(sc);
            evaluate(builtIn);
        }
    }

    private static String getInput(Scanner sc) {
        StringBuilder text = new StringBuilder();

        while (sc.hasNextLine()) {
            text
                    .append(sc.nextLine())
                    .append('\n');
        }

        return text.toString();
    }

    public static Result<List<TNumber>, String> evaluate(String input) {
        Lexer lexer = new Lexer(input);

        return lexer.lex()
                .run(tokens -> new Parser(tokens).parse())
                .run(MathEvaluator::traverse);
    }

    private static Result<List<TNumber>, String> traverse(List<Node> nodes) {
        try {
            List<TNumber> results =
                    nodes.stream()
                            .map(node -> {
                                TNumber val = node.visit(symbolTable);
                                if (node instanceof AssignmentNode) return null;
                                else return val;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

            return Result.success(results);
        } catch (BaseException e) {
            return Result.failure(e.toString());
        }
    }
}
