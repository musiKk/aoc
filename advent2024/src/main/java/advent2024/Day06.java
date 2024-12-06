package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;

public class Day06 {
    public static void main(String[] args) throws Exception {
        var grid = Files.lines(Path.of("input06"))
            .map(String::toCharArray)
            .toArray(l -> new char[l][]);
        part1(grid);
        part2(grid);
    }

    static void part2(char[][] grid) {
        int rows = grid.length;
        int cols = grid.length;
        int guardRow = -1;
        int guardCol = -1;

        outer: for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                char c = grid[row][col];
                if ("^v<>".indexOf(c) != -1) {
                    guardRow = row;
                    guardCol = col;
                    break outer;
                }
            }
        }

        int obstacles = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                System.err.println("placing obstacle in " + row + ", " + col);
                if (row == guardRow && col == guardCol) continue;

                char c = grid[row][col];
                if (c == '#') continue;

                grid[row][col] = '#';
                if (loops(grid, guardRow, guardCol)) {
                    System.err.println(" => yes");
                    obstacles++;
                }
                grid[row][col] = '.';
            }
        }
        System.err.println(obstacles);
    }

    static boolean loops(char[][] grid, int gRow, int gCol) {
        int rows = grid.length;
        int cols = grid.length;

        var visitedDir = new boolean[rows][cols][4];
        var dirs = new int[][] {
            { -1, 0 },
            { 0, 1 },
            { 1, 0 },
            { 0, -1 }
        };

        int dir = switch (grid[gRow][gCol]) {
            case '^' -> 0;
            case '>' -> 1;
            case 'v' -> 2;
            case '<' -> 3;
            default -> throw new RuntimeException();
        };

        while (true) {
            if (visitedDir[gRow][gCol][dir]) return true;
            visitedDir[gRow][gCol][dir] = true;
            int newgRow = gRow + dirs[dir][0];
            int newgCol = gCol + dirs[dir][1];
            if (newgRow < 0 || newgRow == rows) return false;
            if (newgCol < 0 || newgCol == cols) return false;
            if (grid[newgRow][newgCol] == '#') {
                dir++;
                dir %= 4;
            } else {
                gRow = newgRow;
                gCol = newgCol;
            }
        }
    }

    static void part1(char[][] grid) {
        int rows = grid.length;
        int cols = grid.length;
        int guardRow = -1;
        int guardCol = -1;

        outer: for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                char c = grid[row][col];
                if ("^v<>".indexOf(c) != -1) {
                    guardRow = row;
                    guardCol = col;
                    break outer;
                }
            }
        }

        var visited = new boolean[rows][cols];
        var dirs = new int[][] {
            { -1, 0 },
            { 0, 1 },
            { 1, 0 },
            { 0, -1 }
        };
        int dir = switch (grid[guardRow][guardCol]) {
            case '^' -> 0;
            case '>' -> 1;
            case 'v' -> 2;
            case '<' -> 3;
            default -> throw new RuntimeException();
        };

        int count = 0;
        while (true) {
            if (!visited[guardRow][guardCol]) count++;
            visited[guardRow][guardCol] = true;
            int newGuardRow = guardRow + dirs[dir][0];
            int newGuardCol = guardCol + dirs[dir][1];
            if (newGuardRow < 0 || newGuardRow == rows) break;
            if (newGuardCol < 0 || newGuardCol == cols) break;
            if (grid[newGuardRow][newGuardCol] == '#') {
                dir++;
                dir %= 4;
            } else {
                guardRow = newGuardRow;
                guardCol = newGuardCol;
            }
        }
        System.err.println(count);
    }
}
