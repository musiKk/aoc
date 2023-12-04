package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day03 {
    public static void main(String[] args) throws Throwable {
        Path path = Path.of("input03.txt");
        List<String> lines;
        try (var ls = Files.lines(path)) {
            lines = ls.toList();
        }

        Map<Coord, List<Integer>> parts = new HashMap<>();

        int partNumberSum = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            Coord connected = null;
            int curNum = 0;
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                if (c >= '0' && c <= '9') {
                    curNum *= 10;
                    curNum += c - '0';

                    outer: for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx == dy && dx == 0) continue;

                            if (connected(lines, i + dx, j + dy)) {
                                parts.computeIfAbsent(connected = new Coord(i + dx, j + dy), (__) -> new ArrayList<>());
                                continue outer;
                            }
                        }
                    }
                } else {
                    if (connected != null) {
                        partNumberSum += curNum;
                        parts.get(connected).add(curNum);
                    }
                    curNum = 0;
                    connected = null;
                }
            }
            if (connected != null) {
                partNumberSum += curNum;
                parts.get(connected).add(curNum);
            }
        }
        System.err.println(partNumberSum);
        System.err.println(parts);

        int gearRatios = 0;
        for (var e : parts.entrySet()) {
            var coord = e.getKey();
            if (e.getValue().size() == 2 && lines.get(coord.i).charAt(coord.j) == '*') {
                gearRatios += e.getValue().get(0) * e.getValue().get(1);
            }
            System.err.println(gearRatios);
        }
        System.err.println(gearRatios);
    }

    static boolean connected(List<String> lines, int i, int j) {
        if (i < 0 || j < 0 || i >= lines.size() || j >= lines.get(i).length()) {
            return false;
        }
        char c = lines.get(i).charAt(j);
        return c != '.' && !(c >= '0' && c <= '9');
    }

    record Coord(int i, int j) {}
}
