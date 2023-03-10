package org.programs.math;

import org.programs.math.exceptions.BaseException;
import org.programs.math.extra.Result;
import org.programs.math.lexer.Lexer;
import org.programs.math.nodes.AssignmentNode;
import org.programs.math.nodes.Node;
import org.programs.math.parser.Parser;
import org.programs.math.parser.SymbolTable;
import org.programs.math.types.ComplexNum;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
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
            Result<?, String> res = evaluate(builtIn);
            if (res.isError()) {
                throw new RuntimeException("Failed to load built ins: " + res.error);
            }
        }
    }

    private static String getInput(Scanner sc) {
        StringBuilder text = new StringBuilder();

        while (sc.hasNextLine()) {
            text
                    .append(sc.nextLine())
                    .append(';');
        }

        return text.toString();
    }

    public static Result<List<ComplexNum>, String> evaluate(String input) {
        Lexer lexer = new Lexer(input);

        return lexer.lex()
                .run(tokens -> new Parser(tokens).parse())
                .run(MathEvaluator::traverse);
    }

    private static Result<List<ComplexNum>, String> traverse(List<Node> nodes) {
        try {
            List<ComplexNum> results =
                    nodes.stream()
                            .map(node -> {
                                ComplexNum val = node.visit(symbolTable);
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
