package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

public class Day04 {
    public static void main(String[] args) throws Exception {
        var grid = Files.lines(Path.of("input04"))
            .map(String::toCharArray)
            .<char[]>toArray((l) -> new char[l][]);

        part1(grid);
        part2(grid);
    }

    static void part1(char[][] grid) {
        int finds = 0;
        int rows = grid.length;
        int cols = grid[0].length;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                for (int dir = 0; dir < DIRS.length; dir++) {
                    if(search(grid, row, col, "XMAS", dir)) finds++;
                }
            }
        }
        System.err.println(finds);
    }

    static void part2(char [][] grid) {
        int finds = 0;
        int rows = grid.length;
        int cols = grid[0].length;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (
                    search(grid, row, col, "MAS", 3)
                    && search(grid, row, col + 2, "MAS", 5)
                ) finds++;
                if (
                    search(grid, row + 2, col + 2, "MAS", 7)
                    && search(grid, row, col + 2, "MAS", 5)
                ) finds++;
                if (
                    search(grid, row, col, "MAS", 3)
                    && search(grid, row + 2, col, "MAS", 1)
                ) finds++;
                if (
                    search(grid, row + 2, col + 2, "MAS", 7)
                    && search(grid, row + 2, col, "MAS", 1)
                ) finds++;
            }
        }
        System.err.println(finds);
    }

    static int[][] DIRS = new int[][] {
        { -1, 0 },  // 0: U
        { -1, 1 },  // 1: UR
        { 0, 1 },   // 2: R
        { 1, 1 },   // 3: DR
        { 1, 0 },   // 4: D
        { 1, -1 },  // 5: DL
        { 0, -1 },  // 6: L
        { -1, -1 }  // 7: UL
    };

    static boolean search(char[][] grid, int row, int col, String word, int dir) {
        Queue<Element> q = new ArrayDeque<>();
        q.add(Element.of(row, col, dir, 0));

        while (!q.isEmpty()) {
            var el = q.remove();

            if (el.row() < 0 || el.row() >= grid.length || el.col() < 0 || el.col() >= grid[0].length) continue;

            if (grid[el.row()][el.col()] != word.charAt(el.steps)) continue;

            if (el.steps == word.length() - 1) {
                return true;
            }

            q.add(el.next());
        }

        return false;
    }

    record Element(int[] startCoords, int[] coords, int dir, int steps) {
        static Element of(int row, int col, int dir, int steps) {
            return new Element(new int[] {row, col}, new int[] {row, col}, dir, steps);
        }

        int row() {
            return coords[0];
        }

        int col() {
            return coords[1];
        }

        Element next() {
            return new Element(
                startCoords,
                new int[] { coords[0] + DIRS[dir][0], coords[1] + DIRS[dir][1] },
                dir, steps + 1);
        }
    }
}
