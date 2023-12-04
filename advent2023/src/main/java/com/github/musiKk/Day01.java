package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Day01 {
    static Map<String, Integer> digits = Map.of(
        "one", 1,
        "two", 2,
        "three", 3,
        "four", 4,
        "five", 5,
        "six", 6,
        "seven", 7,
        "eight", 8,
        "nine", 9
    );

    public static void main(String[] args) throws Throwable {
        List<String> lines;
        try (var stream = Files.lines(Path.of("input01.txt"))) {
            lines = stream.toList();
        }

        int sum = lines.stream()
            .map(Day01::mapLine)
            .mapToInt(Day01::lineToInt)
            .sum();

        System.err.println(sum);
    }

    static int lineToInt(String line) {
        char c1, c2;
        if (line.length() == 1) {
            c1 = line.charAt(0);
            c2 = line.charAt(0);
        } else {
            c1 = line.charAt(0);
            c2 = line.charAt(line.length() - 1);
        }
        return Integer.parseInt(new String(new char[] { c1, c2 }));
    }

    static String mapLine(String line) {
        StringBuilder sb = new StringBuilder();

        outer:
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if ('0' <= c && c <= '9') {
                sb.append(c);
                continue;
            }
            for (Map.Entry<String, Integer> e : digits.entrySet()) {
                if (line.startsWith(e.getKey(), i)) {
                    sb.append(e.getValue().toString());
                    continue outer;
                }
            }
        }

        return sb.toString();
    }
}
