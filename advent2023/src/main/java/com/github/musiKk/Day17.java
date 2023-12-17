package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Day17 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input17.txt");

        var map = readMap(p);
        // Arrays.stream(map).map(Arrays::toString).forEach(System.err::println);

        part1(map);
        part2(map);
    }

    static void part2(int[][] map) {
        var cost = itpt2(map);
        System.err.println(cost);
    }

    static void part1(int[][] map) {
        // boolean[][] seen = new boolean[map.length][map[0].length];
        // var cost = rec(map, 0, 0, 0, Direction.RIGHT, new HashMap<>(), seen);
        var cost = it(map);
        System.err.println(cost);
    }

    static int itpt2(int[][] map) {
        int rows = map.length;
        int cols = map[0].length;

        Queue<Step> q = new PriorityQueue<>(Comparator.comparingInt(Step::currentCost));
        q.add(new Step(1, 0, Direction.DOWN, 1, map[1][0]));
        q.add(new Step(0, 1, Direction.RIGHT, 1, map[0][1]));

        Map<CoordAndDir, Integer> minCost = new HashMap<>();

        while (!q.isEmpty()) {
            Step step = q.remove();

            int row = step.row;
            int col = step.col;
            int currentCost = step.currentCost;
            var currentStraight = step.currentStraight;
            if (row == rows - 1 && col == cols - 1 && currentStraight >= 4) return currentCost;
            var dir = step.dir;

            for (Direction newDir : Direction.values()) {
                if (newDir == dir.opposite()) continue;
                if (newDir == dir && currentStraight == 10) continue;
                if (newDir != dir && currentStraight < 4) continue;

                int newRow = newDir.newRow(row);
                int newCol = newDir.newCol(col);

                if (newRow < 0 || newRow == rows || newCol < 0 || newCol == cols) continue;

                int newStraight = newDir == dir ? currentStraight + 1 : 1;

                var coordAndDir = new CoordAndDir(new Coord(newRow, newCol), newDir, newStraight);
                int newCost = currentCost + map[newRow][newCol];
                if (newCost < minCost.getOrDefault(coordAndDir, Integer.MAX_VALUE)) {
                    minCost.put(coordAndDir, newCost);
                    q.add(new Step(newRow, newCol, newDir, newStraight, newCost));
                }
            }
        }

        return -1;
    }

    static int it(int[][] map) {
        int rows = map.length;
        int cols = map[0].length;

        Queue<Step> q = new PriorityQueue<>(Comparator.comparingInt(Step::currentCost));
        q.add(new Step(1, 0, Direction.DOWN, 1, map[1][0]));
        q.add(new Step(0, 1, Direction.RIGHT, 1, map[0][1]));

        Map<CoordAndDir, Integer> minCost = new HashMap<>();

        while (!q.isEmpty()) {
            Step step = q.remove();

            int row = step.row;
            int col = step.col;
            int currentCost = step.currentCost;
            if (row == rows - 1 && col == cols - 1) return currentCost;
            var dir = step.dir;
            var currentStraight = step.currentStraight;

            for (Direction newDir : Direction.values()) {
                if (newDir == dir.opposite()) continue;
                if (newDir == dir && currentStraight == 3) continue;

                int newRow = newDir.newRow(row);
                int newCol = newDir.newCol(col);

                if (newRow < 0 || newRow == rows || newCol < 0 || newCol == cols) continue;

                int newStraight = newDir == dir ? currentStraight + 1 : 1;

                var coordAndDir = new CoordAndDir(new Coord(newRow, newCol), newDir, newStraight);
                int newCost = currentCost + map[newRow][newCol];
                if (newCost < minCost.getOrDefault(coordAndDir, Integer.MAX_VALUE)) {
                    minCost.put(coordAndDir, newCost);
                    q.add(new Step(newRow, newCol, newDir, newStraight, newCost));
                }
            }
        }

        return -1;
    }

    record Coord(int row, int col) {}
    record CoordAndDir(Coord coord, Direction dir, int currentStraight) {}

    record Step(int row, int col, Direction dir, int currentStraight, int currentCost) {}

    static Integer rec(int[][] map, int row, int col, int currentStraight, Direction dir, Map<String, Integer> cache, boolean[][] seen) {
        int rows = map.length;
        int cols = map[0].length;

        if (row == rows - 1 && col == cols - 1) return 0;

        String cacheKey = row + "-" + col + "-" + dir + "-" + currentStraight;
        if (cache.containsKey(cacheKey)) return cache.get(cacheKey);

        Integer minCost = null;

        for (Direction newDir : Direction.values()) {
            if (newDir == dir.opposite()) continue;
            if (newDir == dir && currentStraight == 3) continue;

            int newStraight = newDir == dir ? currentStraight + 1 : 1;
            int newRow = newDir.newRow(row);
            int newCol = newDir.newCol(col);

            if (newRow >= 0 && newCol >= 0 && newRow < rows && newCol < cols && !seen[newRow][newCol]) {
                seen[newRow][newCol] = true;

                Integer subResult = rec(map, newRow, newCol, newStraight, newDir, cache, seen);
                if (subResult != null) {
                    if (minCost == null) {
                        minCost = map[newRow][newCol] + subResult;
                    } else {
                        minCost = Math.min(minCost, map[newRow][newCol] + subResult);
                    }
                }

                seen[newRow][newCol] = false;
            }
        }

        cache.put(cacheKey, minCost);
        return minCost;
    }

    enum Direction {
        LEFT, UP, RIGHT, DOWN;

        Direction opposite() {
            return switch (this) {
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
                case UP -> DOWN;
                case DOWN -> UP;
            };
        }
        int newRow(int row) {
            return switch (this) {
                case UP -> row - 1;
                case DOWN -> row + 1;
                default -> row;
            };
        }
        int newCol(int col) {
            return switch (this) {
                case LEFT -> col - 1;
                case RIGHT -> col + 1;
                default -> col;
            };
        }
    }

    static int[][] readMap(Path p) throws Exception {
        int[][] map;
        try (var s = Files.lines(p)) {
            var l = s
                .map(String::toCharArray)
                .map(chs -> {
                    int[] ints = new int[chs.length];
                    for (int i = 0; i < chs.length; i++) {
                        ints[i] = chs[i] - '1' + 1;
                    }
                    return ints;
                }).toList();
            map = new int[l.size()][];
            for (int i = 0; i < l.size(); i++) {
                map[i] = l.get(i);
            }
        }
        return map;
    }

}
