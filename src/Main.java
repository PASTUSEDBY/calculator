import org.programs.math.MathEvaluator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //TODO: This isn't final yet.

        Scanner sc = new Scanner(System.in);
        System.out.print("Give good input: ");
        String line = sc.nextLine();

        System.out.println(
                MathEvaluator.evaluate(line)
        );
    }
}
