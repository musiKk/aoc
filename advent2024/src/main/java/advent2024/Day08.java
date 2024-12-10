package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day08 {
    public static void main(String[] args) throws Exception {
        var grid = Files.lines(Path.of("input08"))
                .map(String::toCharArray)
                .toArray(l -> new char[l][]);
        solve(grid, false);
        solve(grid, true);
    }

    static void solve(char[][] grid, boolean part2) {
        int rows = grid.length;
        int cols = grid[0].length;
        Map<Character, List<int[]>> coordsByChar = new HashMap<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                char c = grid[row][col];
                if (c != '.') {
                    coordsByChar.computeIfAbsent(c, __ -> new ArrayList<>()).add(
                        new int[] { row, col }
                    );
                }
            }
        }

        var antipodes = new boolean[rows][cols];
        for (var entry : coordsByChar.entrySet()) {
            markAntipodes(entry.getValue(), antipodes, part2);
        }

        int antipodesCount = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (antipodes[row][col]) {
                    antipodesCount++;
                }
            }
        }
        System.err.println(antipodesCount);
    }

    static void markAntipodes(List<int[]> coords, boolean[][] antipodes, boolean part2) {
        for (int i = 0; i < coords.size() - 1; i++) {
            for (int j = i + 1; j < coords.size(); j++) {
                if (!part2)
                    markAntipodes(coords.get(i), coords.get(j), antipodes);
                    else
                    markAntipodes2(coords.get(i), coords.get(j), antipodes);
            }
        }
    }

    static void markAntipodes2(int[] coord1, int[] coord2, boolean[][] antipodes) {
        int row1 = coord1[0], col1 = coord1[1];
        int row2 = coord2[0], col2 = coord2[1];

        int rDiff = row1 - row2;
        int cDiff = col1 - col2;

        int rows = antipodes.length, cols = antipodes[0].length;

        int m = 0;
        while (true) {
            int rowA = row1 + m * rDiff;
            int colA = col1 + m * cDiff;
            if (rowA < 0 || rowA >= rows || colA < 0 || colA >= cols) break;
            antipodes[rowA][colA] = true;
            m++;
        }

        m = 0;
        while (true) {
            int rowA = row2 - m * rDiff;
            int colA = col2 - m * cDiff;
            if (rowA < 0 || rowA >= rows || colA < 0 || colA >= cols) break;
            antipodes[rowA][colA] = true;
            m++;
        }
    }

    static void markAntipodes(int[] coord1, int[] coord2, boolean[][] antipodes) {
        int row1 = coord1[0], col1 = coord1[1];
        int row2 = coord2[0], col2 = coord2[1];

        int rDiff = row1 - row2;
        int cDiff = col1 - col2;

        int rowA1 = row1 + rDiff;
        int colA1 = col1 + cDiff;
        int rowA2 = row2 - rDiff;
        int colA2 = col2 - cDiff;

        int rows = antipodes.length, cols = antipodes[0].length;
        if (rowA1 >= 0 && colA1 >= 0 && rowA1 < rows && colA1 < cols)
            antipodes[rowA1][colA1] = true;
        if (rowA2 >= 0 && colA2 >= 0 && rowA2 < rows && colA2 < cols)
            antipodes[rowA2][colA2] = true;
    }
}
