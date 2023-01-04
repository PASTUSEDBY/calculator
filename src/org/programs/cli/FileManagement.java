package org.programs.cli;

import org.programs.math.MathEvaluator;
import org.programs.math.extra.Result;

import java.io.*;
import java.util.Scanner;

public final class FileManagement {
    private static File cache;
    private static final String CACHE_DIR = "./cache/";
    private static final String STORAGE = CACHE_DIR + "global.txt";
    private static final String TEMP_STORAGE = CACHE_DIR + "tmp.txt";

    public static void init() {
        cache = new File(STORAGE);

        try {
            makeFiles();
        } catch (IOException err) {
            System.out.println("Could not create storage file, skipping...");
            cache = null;
        }
    }

    public static void deleteDefinition(String variable) {
        if (cache == null) {
            return;
        }

        File temp = new File(TEMP_STORAGE);

        try {
            temp.createNewFile();
        } catch (IOException err) {
            return; //Failed
        }

        try (
                Scanner sc = new Scanner(cache);
                FileWriter fw = new FileWriter(temp);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw)
                ) {
            while (sc.hasNextLine()) {
                String line = getModifiedDef(sc.nextLine(), variable);
                if (!line.contains("=")) {
                    continue;
                }

                pw.println(line.trim());
            }
        } catch (IOException err) {
            //ignore
        }

        if (cache.delete()) {
            temp.renameTo(cache);
        }

        temp.delete();
    }

    public static void write(String data) {
        if (cache == null) {
            return;
        }


        try (
                FileWriter fw = new FileWriter(cache, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw)
                ) {
            pw.println(data);
        } catch (IOException err) {
            //do nothing
        }
    }

    public static String getInput(Scanner sc) {
        StringBuilder text = new StringBuilder();

        while (sc.hasNextLine()) {
            text
                    .append(sc.nextLine())
                    .append(';');
        }

        return text.toString();
    }

    public static void loadGlobals() {
        if (cache == null) {
            return;
        }

        try (Scanner sc = new Scanner(cache)) {
            Result<?, String> res = MathEvaluator.evaluate(getInput(sc));
            if (res.isError())
                System.out.println("Could not load definitions, skipping...");
            else
                System.out.println("Definitions loaded successfully...");
        } catch (IOException err) {
            System.out.println("Could not load definitions due to a file error, skipping...");
        }
    }

    private static String getModifiedDef(String line, String variable) {
        if (line.startsWith("fn")) {
            if (line.startsWith("fn " + variable)) return "";
        }

        return line.replace(variable + " = ", "");
    }

    private static void makeFiles() throws IOException {
        if (!cache.exists()) {
            if (cache.getParentFile().mkdirs())
                System.out.println("Created the cache directory!");

            if (cache.createNewFile())
                System.out.println("Created storage file!");
        } else
            System.out.println("Storage file exists, loading definitions...");
    }
}
