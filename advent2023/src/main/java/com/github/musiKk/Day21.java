package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day21 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input21.txt");
        int startRow = 0;
        int startCol = 0;
        char[][] map;

        try (var stream = Files.lines(p)) {
            List<String> lines = stream.toList();
            map = new char[lines.size()][lines.get(0).length()];
            for (int row = 0; row < lines.size(); row++) {
                String line = lines.get(row);
                for (int col = 0; col < line.length(); col++) {
                    map[row][col] = line.charAt(col);
                    if (map[row][col] == 'S') {
                        startRow = row;
                        startCol = col;
                        map[row][col] = '.';
                    }
                }
            }
        }

        // part1(map, startRow, startCol);
        part2(map, startRow, startCol);
    }

    static void dumpMap(char[][] map) {
        Arrays.stream(map).map(String::new).forEach(System.err::println);
    }

    static void part2(char[][] map, int startRow, int startCol) {
        int height = map.length;
        int width = map[0].length;

        Set<Coord> visited = new HashSet<>();

        Deque<Coord> q = new ArrayDeque<>();
        q.add(new Coord(startRow, startCol));

        int steps = 0;
        Map<Coord, Integer> coordToMinSteps = new HashMap<>();
        while (!q.isEmpty()) {
            int len = q.size();
            for (int i = 0; i < len; i++) {
                var coord = q.remove();

                int row = coord.row;
                int col = coord.col;
                if (row < 0 || row == height || col < 0 || col == width) continue;
                if (map[row][col] == '#') continue;

                if (visited.contains(coord)) continue;
                visited.add(coord);

                coordToMinSteps.put(coord, steps);

                q.add(new Coord(row - 1, col));
                q.add(new Coord(row + 1, col));
                q.add(new Coord(row, col - 1));
                q.add(new Coord(row, col + 1));
            }
            steps++;
        }

        var evenCorners = coordToMinSteps.values().stream().filter(v -> v % 2 == 0 && v > 65).count();
        var oddCorners = coordToMinSteps.values().stream().filter(v -> v % 2 == 1 && v > 65).count();
        var evenFull = coordToMinSteps.values().stream().filter(v -> v % 2 == 0).count();
        var oddFull = coordToMinSteps.values().stream().filter(v -> v % 2 == 1).count();

        long n = 202300;
        long result = ((n+1)*(n+1)) * oddFull + (n*n) * evenFull - (n+1) * oddCorners + n * evenCorners;
        System.err.println(result);
    }

    static void part1(char[][] map, int startRow, int startCol) {
        Deque<Coord> q = new ArrayDeque<>();
        q.add(new Coord(startRow, startCol));

        int steps = 0;
        int reached = 0;
        while (!q.isEmpty()) {
            System.err.println("reached after " + steps + " steps: " + reached);

            if (steps == 64) break;

            int len = q.size();
            Set<Coord> newSteps = new HashSet<>();
            for (int i = 0; i < len; i++) {
                var pos = q.remove();

                int r = pos.row;
                int c = pos.col;
                if (r < 0 || r == map.length || c < 0 || c == map[0].length) continue;

                if (r > 0 && map[r - 1][c] == '.')
                    newSteps.add(new Coord(r - 1, c));
                if (r < map.length - 1 && map[r + 1][c] == '.')
                    newSteps.add(new Coord(r + 1, c));
                if (c > 0 && map[r][c - 1] == '.')
                    newSteps.add(new Coord(r, c - 1));
                if (c < map[0].length - 1 && map[r][c + 1] == '.')
                    newSteps.add(new Coord(r, c + 1));

            }
            reached = newSteps.size();
            q.addAll(newSteps);
            steps++;
        }

        System.err.println(reached);
    }

    record Coord(int row, int col) {}

}
