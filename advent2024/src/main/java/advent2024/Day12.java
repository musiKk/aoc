package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Day12 {
    public static void main(String[] args) throws Exception {
        var grid = Files.lines(Path.of("input12"))
                .map(String::toCharArray)
                .toArray(l -> new char[l][]);
        solve(grid);
    }

    static void solve(char[][] grid) {
        int rows = grid.length, cols = grid[0].length;

        var areaCodes = new int[rows][cols];
        for (var row : areaCodes) Arrays.fill(row, -1);

        int areaId = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (areaCodes[row][col] != -1) continue;
                floodArea(grid, row, col, areaCodes, areaId++);
            }
        }
        var areas = part1(areaCodes);
        for (var row : areaCodes) System.err.println(Arrays.toString(row));
        part2(areaCodes, areaId - 1, areas);
    }

    static int[][] DIRS = new int[][] {
        { -1, 0 },
        { 0, 1 },
        { 1, 0 },
        { 0, -1 }
    };

    static void floodArea(char[][] grid, int sRow, int sCol, int[][] areas, int areaId) {
        int rows = grid.length, cols = grid[0].length;

        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[] { sRow, sCol });

        while (!q.isEmpty()) {
            var coord = q.remove();

            int row = coord[0], col = coord[1];
            if (areas[row][col] != -1) continue;

            areas[row][col] = areaId;

            char c = grid[row][col];

            for (var dir : DIRS) {
                int nRow = row + dir[0], nCol = col + dir[1];
                if (nRow < 0 || nRow == rows || nCol < 0 || nCol == cols) continue;

                char cc = grid[nRow][nCol];
                if (c != cc) continue;
                // if (areas[nRow][nCol] != -1) continue;
                q.add(new int[] { nRow, nCol });
            }
        }
    }

    static Map<Integer, Integer> part1(int[][] areaCodes) {
        int rows = areaCodes.length, cols = areaCodes[0].length;

        Map<Integer, Integer> areas = new HashMap<>();
        Map<Integer, Integer> peris = new HashMap<>();

        for (int row = 0; row < rows; row++) {
            int prev = -1;
            for (int col = 0; col < cols; col++) {
                int c = areaCodes[row][col];
                if (c != prev) {
                    if (prev != -1) peris.merge(prev, 1, Integer::sum);
                    peris.merge(c, 1, Integer::sum);
                }
                prev = c;
            }
            peris.merge(prev, 1, Integer::sum);
        }
        for (int col = 0; col < cols; col++) {
            int prev = -1;
            for (int row = 0; row < rows; row++) {
                int c = areaCodes[row][col];
                if (c != prev) {
                    if (prev != -1) peris.merge(prev, 1, Integer::sum);
                    peris.merge(c, 1, Integer::sum);
                }
                prev = c;
                areas.merge(c, 1, Integer::sum);
            }
            peris.merge(prev, 1, Integer::sum);
        }

        int totalPrice = 0;
        for (int c : areas.keySet()) {
            totalPrice += areas.get(c) * peris.get(c);
        }
        System.err.println(totalPrice);
        return areas;
    }

    static void part2(int[][] areaCodes, int maxCode, Map<Integer, Integer> areas) {
        int rows = areaCodes.length, cols = areaCodes[0].length;

        Map<Integer, Integer> sides = new HashMap<>();

        for (int code = 0; code <= maxCode; code++) {
            // enter from left
            for (int col = 0; col < cols; col++) {
                int lastRowWithSide = -1;
                for (int row = 0; row < rows; row++) {
                    int curArea = areaCodes[row][col];
                    if (curArea != code) continue;

                    if (col == 0 || areaCodes[row][col - 1] != code) {
                        // existing side
                        if (lastRowWithSide != -1 && lastRowWithSide == row - 1) {
                            lastRowWithSide = row;
                            continue;
                        }

                        // new area entered from left
                        System.err.printf("area %d has new left side at %d, %d%n", code, row, col);
                        sides.merge(code, 1, Integer::sum);
                        lastRowWithSide = row;
                    }
                }
            }

            // enter from right
            for (int col = cols - 1; col >= 0; col--) {
                int lastRowWithSide = -1;
                for (int row = 0; row < rows; row++) {
                    int curArea = areaCodes[row][col];
                    if (curArea != code) continue;

                    if (col == cols - 1 || areaCodes[row][col + 1] != code) {
                        // existing side
                        if (lastRowWithSide != -1 && lastRowWithSide == row - 1) {
                            lastRowWithSide = row;
                            continue;
                        }

                        // new area entered from right
                        System.err.printf("area %d has new right side at %d, %d%n", code, row, col);
                        sides.merge(code, 1, Integer::sum);
                        lastRowWithSide = row;
                    }
                }
            }

            // enter from top
            for (int row = 0; row < rows; row++) {
                int lastColWithSide = -1;
                for (int col = 0; col < cols; col++) {
                    int curArea = areaCodes[row][col];
                    if (curArea != code) continue;

                    if (row == 0 || areaCodes[row - 1][col] != code) {
                        // existing side
                        if (lastColWithSide != -1 && lastColWithSide == col - 1) {
                            lastColWithSide = col;
                            continue;
                        }

                        // new area entered from top
                        System.err.printf("area %d has new top side at %d, %d%n", code, row, col);
                        sides.merge(code, 1, Integer::sum);
                        lastColWithSide = col;
                    }
                }
            }

            // enter from bottom
            for (int row = rows - 1; row >= 0; row--) {
                int lastColWithSide = -1;
                for (int col = 0; col < cols; col++) {
                    int curArea = areaCodes[row][col];
                    if (curArea != code) continue;

                    if (row == rows - 1 || areaCodes[row + 1][col] != code) {
                        // existing side
                        if (lastColWithSide != -1 && lastColWithSide == col - 1) {
                            lastColWithSide = col;
                            continue;
                        }

                        // new area entered from bottom
                        System.err.printf("area %d has new bottom side at %d, %d%n", code, row, col);
                        sides.merge(code, 1, Integer::sum);
                        lastColWithSide = col;
                    }
                }
            }

        }
        System.err.println(areas);
        System.err.println(sides);

        int totalPrice = 0;
        for (int areaCode = 0; areaCode <= maxCode; areaCode++) {
            totalPrice += areas.get(areaCode) * sides.get(areaCode);
        }
        System.err.println(totalPrice);
    }
}
