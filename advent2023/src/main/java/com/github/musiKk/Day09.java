package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day09 {

    public static void main(String[] args) throws Throwable {
        var path = Path.of("input09.txt");
        List<String> lines;
        try (var s = Files.lines(path)) {
            lines = s.toList();
        }

        part1(lines);
    }

    static void part1(List<String> lines) {
        var result = lines.stream()
                .map(Day09::extrapolate)
                .reduce(Extrapolation::sum);
        System.err.println(result);
    }

    static Extrapolation extrapolate(List<Integer> values) {
        List<Integer> diffs = new ArrayList<>();
        boolean allZero = values.get(0) == 0;
        for (int i = 0; i < values.size() - 1; i++) {
            int d = values.get(i + 1) - values.get(i);
            diffs.add(d);

            allZero &= values.get(i + 1) == 0;
        }
        if (allZero) {
            return new Extrapolation(0, 0);
        } else {
            Extrapolation toAdd = extrapolate(diffs);
            return new Extrapolation(
                values.get(0) - toAdd.left,
                values.get(values.size() - 1) + toAdd.right);
        }
    }

    static Extrapolation extrapolate(String line) {
        List<Integer> values = Arrays.stream(line.split(" ")).map(Integer::parseInt).toList();
        return extrapolate(values);
    }

    record Extrapolation(int left, int right) {
        static Extrapolation sum(Extrapolation e1, Extrapolation e2) {
            return new Extrapolation(e1.left + e2.left, e1.right + e2.right);
        }
    }

}
