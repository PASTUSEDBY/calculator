package org.programs.cli;

import org.programs.math.MathEvaluator;
import org.programs.math.extra.Result;
import org.programs.math.extra.Trigonometry;
import org.programs.math.nodes.AssignmentNode;
import org.programs.math.nodes.FuncDefNode;
import org.programs.math.nodes.Node;
import org.programs.math.types.ComplexNum;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class REPL {
    private static final ArrayList<String> history;
    static {
        history = new ArrayList<>();
    }

    public static void init() {
        FileManagement.init();
        MathEvaluator.init();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter input > ");
                String line = sc.nextLine();
                String[] parsed = line.split("\\s+");
                if (parsed.length == 0) {
                    eval(line);
                    continue;
                }

                String cmd = parsed[0];
                try {
                    switch (cmd) {
                        case "cat" -> {
                            checkArgs(parsed, 1);
                            changeAngleType(parsed[1]);
                        }
                        case "del" -> {
                            checkArgs(parsed, 1);
                            MathEvaluator.symbolTable.remove(parsed[1]);
                            FileManagement.deleteDefinition(parsed[1]);
                        }
                        case "history" -> {
                            if (parsed.length == 1) {
                                if (history.isEmpty())
                                    System.out.println("No history!");
                                else for (int i = 0; i < history.size(); i++)
                                    System.out.println(i + 1 + ": " + history.get(i));
                                continue;
                            }

                            executeLastExpr(parsed[1]);
                        }
                        case "help" -> printHelpText();
                        case "exit" -> exit();
                        default -> eval(line);
                    }
                } catch (RuntimeException err) {
                    System.out.println(err);
                }
            }
        }
    }

    private static void history(String input) {
        if (history.size() == 20) {
            history.remove(0);
        }

        history.add(input);
    }

    private static void changeAngleType(String input) {
        String upInput = input.toUpperCase();
        Trigonometry.AngleType[] angles = Trigonometry.AngleType.values();

        for (Trigonometry.AngleType angle : angles) {
            if (angle.name().startsWith(upInput)) {
                Trigonometry.setAngleType(angle);
                System.out.println("Successfully set the angle type as: " + angle);
                return;
            }
        }

        System.out.println("Angle type must be between degrees/radians/grades, received input: " + input);
    }

    public static void saveGlobal(List<Node> exprs) {
        for (Node expr : exprs) {
            if (!(expr instanceof FuncDefNode || expr instanceof AssignmentNode)) {
                continue;
            }

            if (expr instanceof FuncDefNode funcDef) {
                FileManagement.deleteDefinition(funcDef.fn.name);
                FileManagement.write(expr.toString());
                continue;
            }

            Node tExpr = expr;
            StringBuilder sb = new StringBuilder();
            String varName = "";

            while (tExpr instanceof AssignmentNode assign) {
                FileManagement.deleteDefinition(assign.idName);
                tExpr = assign.expr;

                sb.append(assign.idName).append(" = ");

                if (varName.isEmpty()) varName = assign.idName;
            }

            sb.append(MathEvaluator.symbolTable.get(varName, true));

            FileManagement.write(sb.toString());
        }
    }

    private static void exit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    private static void executeLastExpr(String x) {
        int idx = Integer.parseInt(x) - 1;
        String expr = history.get(idx);
        System.out.println(expr);
        eval(expr);
    }

    private static void eval(String input) {
        Result<List<ComplexNum>, String> res = MathEvaluator.evaluate(input);
        if (res.isError()) {
            System.out.println(res.error);
            return;
        }

        history(input);

        res.result.forEach(System.out::println);
    }

    private static void printHelpText() {
        InputStream in = REPL.class.getResourceAsStream("help.txt");
        if (in == null) {
            System.out.println("Help text does not exist.");
            return;
        }

        try (Scanner sc = new Scanner(in)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
            }
        }
    }

    private static void checkArgs(String[] args, int required) {
        int given = args.length - 1;
        if (given < required) {
            throw new IllegalArgumentException("Required arguments: " + required + ", given: " + given);
        }
    }
}
