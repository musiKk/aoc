package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day13 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input13.txt");
        List<Pattern> patterns = readPatterns(p);

        solve(patterns, 0);
        solve(patterns, 1);
    }

    static void solve(List<Pattern> patterns, int errors) {
        int rows = 0;
        int cols = 0;
        for (Pattern p : patterns) {
            int[] result = findMirror(p, errors);
            rows += result[0];
            cols += result[1];
        }
        System.err.println(rows * 100 + cols);
    }

    static int[] findMirror(Pattern p, int errors) {
        for (int row = 1; row < p.lines.size(); row++) {
            int rowErrors = checkRowMirror(p, row);
            if (rowErrors == errors) {
                return new int[] { row, 0 };
            }
        }
        for (int col = 1; col < p.lines.get(0).length(); col++) {
            int colErrors = checkColMirror(p, col);
            if (colErrors == errors) {
                return new int[] { 0, col };
            }
        }
        return null;
    }

    static int checkRowMirror(Pattern p, int row) {
        int top = row - 1;
        int bottom = row;
        int width = p.lines.get(0).length();
        int errors = 0;
        while (top >= 0 && bottom < p.lines.size()) {
            String topLine = p.lines.get(top);
            String bottomLine = p.lines.get(bottom);
            for (int i = 0; i < width; i++) {
                if (topLine.charAt(i) != bottomLine.charAt(i)) {
                    errors++;
                    if (errors > 1) return 2;
                }
            }
            top--;
            bottom++;
        }
        return errors;
    }

    static int checkColMirror(Pattern p, int col) {
        int width = p.lines.get(0).length();
        int left = col - 1;
        int right = col;
        int height = p.lines.size();
        int errors = 0;
        while (left >= 0 && right < width) {
            for (int i = 0; i < height; i++) {
                if (p.lines.get(i).charAt(left) != p.lines.get(i).charAt(right)) {
                    errors++;
                    if (errors > 1) return 2;
                }
            }
            left--;
            right++;
        }
        return errors;
    }

    static List<Pattern> readPatterns(Path p) throws Exception {
        List<Pattern> patterns = new ArrayList<>();
        try (var stream = Files.lines(p)) {
            List<String> chunk = new ArrayList<>();
            for (String line : stream.toList()) {
                if (line.isEmpty()) {
                    patterns.add(Pattern.of(chunk));
                    chunk = new ArrayList<>();
                } else {
                    chunk.add(line);
                }
            }
            patterns.add(Pattern.of(chunk));
        }
        return patterns;
    }

    record Pattern(List<String> lines) {
        static Pattern of(List<String> lines) {
            return new Pattern(lines);
        }
    }

}
