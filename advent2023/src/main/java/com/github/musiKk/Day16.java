package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Day16 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input16.txt");
        char[][] grid;
        try (var s = Files.lines(p)) {
            grid = s.map(String::toCharArray).toList().toArray(new char[0][]);
        }

        part1(grid);
        part2(grid);
    }

    static void part2(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        int maxEnergized = 0;
        for (int row = 0; row < rows; row++) {
            maxEnergized = Math.max(maxEnergized, countEnergize(grid, row, 0, RIGHT));
            maxEnergized = Math.max(maxEnergized, countEnergize(grid, row, cols - 1, LEFT));
        }
        for (int col = 0; col < cols; col++) {
            maxEnergized = Math.max(maxEnergized, countEnergize(grid, 0, col, DOWN));
            maxEnergized = Math.max(maxEnergized, countEnergize(grid, rows - 1, col, UP));
        }
        System.err.println(maxEnergized);
    }

    static void part1(char[][] grid) {
        System.err.println(countEnergize(grid, 0, 0, RIGHT));
    }

    static int countEnergize(char[][] grid, int startRow, int startCol, Direction startDirection) {
        int rows = grid.length;
        int cols = grid[0].length;

        Deque<Beam> q = new ArrayDeque<>();
        q.add(new Beam(startDirection, startRow, startCol));
        boolean[][] energized = new boolean[rows][cols];
        boolean[][] splitsDone = new boolean[rows][cols];
        int energizedCount = 0;

        while (!q.isEmpty()) {
            int count = q.size();
            for (int i = 0; i < count; i++) {
                // try { Thread.sleep(1); } catch (Exception e) {}
                Beam beam = q.remove();

                int row = beam.row;
                int col = beam.col;
                if (row < 0 || col < 0 || row == rows || col == cols) continue;

                if (!energized[row][col]) {
                    energized[row][col] = true;
                    energizedCount++;
                }

                char ch = grid[row][col];
                switch (ch) {
                    case '.' -> q.add(beam.move());
                    case '/', '\\' -> q.add(beam.mirror(ch));
                    case '|', '-' -> {
                        // System.err.println("splitting");
                        var beams = beam.split(ch);
                        if (beams.size() > 1 && !splitsDone[row][col]) {
                            q.addAll(beams);
                            splitsDone[row][col] = true;
                        } else if (beams.size() == 1) {
                            q.addAll(beams);
                        }
                    }

                }
            }
            // System.err.println(energizedCount);
        }
        // Arrays.stream(energized)
        //     .map(l -> {
        //         var sb = new StringBuilder();
        //         for (var b : l) {
        //             if (b) sb.append('#');
        //             else sb.append('.');
        //         }
        //         return sb.toString();
        //     }).forEach(System.err::println);
        return energizedCount;

    }

    static Direction LEFT = Direction.LEFT;
    static Direction UP = Direction.UP;
    static Direction DOWN = Direction.DOWN;
    static Direction RIGHT = Direction.RIGHT;

    record Beam(Direction dir, int row, int col) {
        Beam move() {
            return switch (dir) {
                case UP -> new Beam(dir, row - 1, col);
                case RIGHT -> new Beam(dir, row, col + 1);
                case DOWN -> new Beam(dir, row + 1, col);
                case LEFT -> new Beam(dir, row, col - 1);
            };
        }
        List<Beam> split(char split) {
            return switch (split) {
                case '|' -> switch (dir) {
                    case RIGHT, LEFT -> List.of(
                        new Beam(UP, row - 1, col),
                        new Beam(DOWN, row + 1, col)
                    );
                    default -> List.of(move());
                };
                case '-' -> switch (dir) {
                    case UP, DOWN -> List.of(
                        new Beam(LEFT, row, col - 1),
                        new Beam(RIGHT, row, col + 1)
                    );
                    default -> List.of(move());
                };
                default -> throw new RuntimeException();
            };
        }
        Beam mirror(char mirror) {
            return switch (mirror) {
                case '/' -> switch (dir) {
                    case UP -> new Beam(RIGHT, row, col + 1);
                    case RIGHT -> new Beam(UP, row - 1, col);
                    case DOWN -> new Beam(LEFT, row, col - 1);
                    case LEFT -> new Beam(DOWN, row + 1, col);
                };
                case '\\' -> switch (dir) {
                    case UP -> new Beam(LEFT, row, col - 1);
                    case RIGHT -> new Beam(DOWN, row + 1, col);
                    case DOWN -> new Beam(RIGHT, row, col + 1);
                    case LEFT -> new Beam(UP, row - 1, col);
                };
                default -> throw new RuntimeException();
            };
        }
    }

    enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

}
