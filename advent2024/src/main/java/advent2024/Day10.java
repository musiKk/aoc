package advent2024;

import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

public class Day10 {
    public static void main(String[] args) throws Exception {
        var grid = Files.lines(Path.of("input10"))
            .map(String::toCharArray)
            .<int[]>map(r -> CharBuffer.wrap(r).chars().map(c -> c - '0').toArray())
            .toArray(l -> new int[l][]);
        solve(grid, false);
        solve(grid, true);
    }

    static void solve(int[][] grid, boolean part2) {
        int totalScore = 0;
        int rows = grid.length, cols = grid[0].length;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int level = grid[row][col];
                if (level == 0) {
                    totalScore += calcScore(grid, row, col, part2);
                }
            }
        }
        System.err.println(totalScore);
    }

    static int[][] STEPS = new int[][] {
        { -1, 0 },
        { 1, 0 },
        { 0, -1 },
        { 0, 1 },
    };

    static int calcScore(int[][] grid, int sRow, int sCol, boolean part2) {
        int rows = grid.length, cols = grid[0].length;

        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[] { sRow, sCol, 0 });

        boolean[][] seen = new boolean[rows][cols];

        int score = 0;
        while (!q.isEmpty()) {
            var coord = q.remove();

            int row = coord[0], col = coord[1], prevLevel = coord[2];
            if (!part2 && seen[row][col]) continue;
            seen[row][col] = true;
            if (grid[row][col] == 9) {
                score++;
                continue;
            }

            for (var step : STEPS) {
                int cRow = row + step[0];
                int cCol = col + step[1];
                if (cRow < 0 || cRow == rows || cCol < 0 || cCol == cols) continue;
                int cLevel = grid[cRow][cCol];
                if (prevLevel + 1 != cLevel) continue;
                q.add(new int[] { cRow, cCol, cLevel });
            }

        }

        return score;
    }
}
