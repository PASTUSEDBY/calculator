package org.programs.math;

import org.programs.cli.FileManagement;
import org.programs.cli.REPL;
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
    private static boolean initialized;

    static {
        symbolTable = new SymbolTable();
    }

    private MathEvaluator() {
        //private
    }

    public static void init() {
        preload();
        SymbolTable.saveBuiltIns(symbolTable);
        FileManagement.loadGlobals();
        initialized = true;
    }

    private static void preload() {
        InputStream in = MathEvaluator.class.getResourceAsStream("BuiltIns.txt");
        Objects.requireNonNull(in, "The file BuiltIns.txt is not provided with this package.");

        try (Scanner sc = new Scanner(in)) {
            String builtIn = FileManagement.getInput(sc);
            Result<?, String> res = evaluate(builtIn);
            if (res.isError()) {
                throw new RuntimeException("Failed to load built ins: " + res.error);
            }
        }
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

            if (initialized)
                REPL.saveGlobal(nodes);
            return Result.success(results);
        } catch (BaseException e) {
            return Result.failure(e.toString());
        }
    }
}
