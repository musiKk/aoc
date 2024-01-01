package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day23 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input23.txt");
        char[][] map;
        try (var stream = Files.lines(p)) {
            List<char[]> charLines = new ArrayList<>();
            stream.forEach(s -> charLines.add(s.toCharArray()));
            map = charLines.toArray(new char[0][]);
        }

        // part1(map);
        part2(map);
    }

    static void part2(char[][] map) {
        for (char[] row : map) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] == '#') continue;
                row[i] = '.';
            }
        }
        // dumpMap(map);
        solutions.clear();
        cache.clear();
        part1(map);
    }

    static void part1(char[][] map) {
        // close entry
        // map[0][1] = '#';

        var solution = rec(map, 0, 1, 0);
        System.err.println(solutions);
        System.err.println(solution);
    }

    static List<Integer> solutions = new ArrayList<>();
    static Map<Coord, Integer> cache = new HashMap<>();

    static int rec(char[][] map, int row, int col, int steps) {
        int height = map.length;
        int width = map[0].length;

        char ch = map[row][col];
        map[row][col] = 'X';

        // System.err.printf("%s %d steps @ %d, %d%n", ch, steps, row, col);
        // dumpMap(map);

        if (row == height - 1 && col == width - 2) {
            // dumpMap(map);
            solutions.add(steps);
            return 0;
        }

        var coord = new Coord(row, col);
        // if (cache.containsKey(coord)) {
        //     return cache.get(coord);
        // }

        if ("<>v^".indexOf(ch) != -1) {
            int newRow = row;
            int newCol = col;
            if (ch == '>') {
                newCol++;
            } else if (ch == '<') {
                newCol--;
            } else if (ch == '^') {
                newRow--;
            } else if (ch == 'v') {
                newRow++;
            }

            if (map[newRow][newCol] == '#' || map[newRow][newCol] == 'O') return -1;

            map[row][col] = 'O';
            int subSolution = rec(map, newRow, newCol, steps + 1);
            int solution;
            if (subSolution == -1) {
                solution = -1;
            } else {
                solution = 1 + subSolution;
            }
            map[row][col] = ch;
            // cache.put(coord, solution);
            return solution;
        }

        int solution = -1;
        for (var ds : new int[][] { {0, 1}, {0, -1}, {1, 0}, {-1, 0} }) {
            int dr = ds[0];
            int dc = ds[1];

            int newRow = row + dr;
            int newCol = col + dc;

            if (newRow < 0) continue;
            if (map[newRow][newCol] == '#' || map[newRow][newCol] == 'O') continue;

            map[row][col] = 'O';
            int subSolution = rec(map, newRow, newCol, steps + 1);
            if (subSolution != -1) {
                solution = Math.max(solution, 1 + subSolution);
            }
            map[row][col] = ch;
        }

        // cache.put(coord, solution);
        return solution;
    }

    static void dumpMap(char[][] map) {
        for (char[] cs : map) {
            System.err.println(new String(cs));
        }
    }

    record Coord(int row, int col) {}

}
