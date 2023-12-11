package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Day10 {

    public static void main(String[] args) throws Throwable {
        Path p = Path.of("input10.txt");
        List<String> lines;
        try (var s = Files.lines(p)) {
            lines = s.toList();
        }

        int rows = lines.size();
        int cols = lines.get(0).length();

        var start = findStart(lines);
        // var loopTiles = part1(start, lines);
        // part2(lines, loopTiles, rows, cols);

        var coords = part1New(start, new Input(lines));
        part2(lines, coords, rows, cols);
    }

    static void part2(List<String> lines, Set<Coord> loopTiles, int rows, int cols) {
        char[][] area = new char[rows][cols];
        loopTiles.stream().forEach(c -> area[c.row][c.col] = lines.get(c.row).charAt(c.col));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (area[i][j] == 0) {
                    area[i][j] = '.';
                }
            }
        }

        Arrays.stream(area).map(Arrays::toString).forEach(System.err::println);;

        int insideTiles = 0;
        boolean inside = false;
        for (int row = 0; row < rows; row++) {
            inside = false;
            char lastCorner = '.';
            int rowInsideTiles = 0;
            for (int col = 0; col < cols; col++) {
                char c = area[row][col];
                if (c == '.' && inside) {
                    rowInsideTiles++;
                } else if (c == '|' || c == 'S') {
                    // may only work for my input
                    inside = !inside;
                } else if (c == 'L' || c == 'F') {
                    lastCorner = c;
                } else if (c == 'J') {
                    if (lastCorner == 'F') {
                        inside = !inside;
                    }
                    lastCorner = c;
                } else if (c == '7') {
                    if (lastCorner == 'L') {
                        inside = !inside;
                    }
                    lastCorner = c;
                }
            }
            System.err.println("row " + row + ": " + rowInsideTiles);
            insideTiles += rowInsideTiles;
        }
        System.err.println(insideTiles);
    }

    static Set<Coord> part1New(Coord start, Input input) {
        Set<Coord> result = new HashSet<>();
        result.add(start);

        Direction dir = null;

        if (input.neighbor(start, Direction.UP).map(c -> "|7F".indexOf(c) >= 0).orElse(false)) {
            dir = Direction.UP;
        } else if (input.neighbor(start, Direction.DOWN).map(c -> "|JL".indexOf(c) >= 0).orElse(false)) {
            dir = Direction.DOWN;
        } else if (input.neighbor(start, Direction.LEFT).map(c -> "-FL".indexOf(c) >= 0).orElse(false)) {
            dir = Direction.LEFT;
        } else if (input.neighbor(start, Direction.RIGHT).map(c -> "-J7".indexOf(c) >= 0).orElse(false)) {
            dir = Direction.RIGHT;
        } else {
            throw new RuntimeException();
        }
        Coord cur = start.neighbor(dir);

        int steps = 1;
        while (!cur.equals(start)) {
            result.add(cur);

            char cell = input.cell(cur);
            Direction nextDirection = switch (dir) {
                case LEFT ->
                    switch (cell) {
                        case '-' -> Direction.LEFT;
                        case 'L' -> Direction.UP;
                        case 'F' -> Direction.DOWN;
                        default -> throw new RuntimeException();
                    };
                case RIGHT ->
                    switch (cell) {
                        case '-' -> Direction.RIGHT;
                        case 'J' -> Direction.UP;
                        case '7' -> Direction.DOWN;
                        default -> throw new RuntimeException();
                    };
                case UP ->
                    switch (cell) {
                        case '|' -> Direction.UP;
                        case '7' -> Direction.LEFT;
                        case 'F' -> Direction.RIGHT;
                        default -> throw new RuntimeException();
                    };
                case DOWN ->
                    switch (cell) {
                        case '|' -> Direction.DOWN;
                        case 'L' -> Direction.RIGHT;
                        case 'J' -> Direction.LEFT;
                        default -> throw new RuntimeException();
                    };
            };
            Coord next = cur.neighbor(nextDirection);
            cur = next;
            dir = nextDirection;
            steps++;
        }
        System.err.println(steps);
        return result;
    }

    // static Set<Coord> part1(Coord start, List<String> lines) {
    //     Deque<Coord> q = new ArrayDeque<>();
    //     q.add(start);

    //     Set<Coord> seen = new HashSet<>();
    //     int steps = 0;
    //     while (!q.isEmpty()) {
    //         int size = q.size();
    //         for (int i = 0; i < size; i++) {
    //             Coord coord = q.remove();
    //             if (seen.contains(coord)) {
    //                 continue;
    //             }
    //             seen.add(coord);

    //             int row = coord.row;
    //             int col = coord.col;
    //             int outside = coord.outside;
    //             char c = lines.get(row).charAt(col);

    //             System.err.println("looking at " + coord + "(" + c + ")");

    //             // left
    //             if (col > 0 && "S-LF".indexOf(lines.get(row).charAt(col - 1)) >= 0 && "S-7J".indexOf(c) >= 0) {
    //                 char neighbor = lines.get(row).charAt(col - 1);
    //                 int newOutside = -1;
    //                 if (neighbor == '-' && (outside & OUTSIDE_UP) == OUTSIDE_UP) {
    //                     newOutside = OUTSIDE_UP;
    //                 }
    //                 if (neighbor == '-' && (outside & OUTSIDE_DOWN) == OUTSIDE_DOWN) {
    //                     newOutside = OUTSIDE_DOWN;
    //                 }
    //                 if (neighbor == 'L' && (outside & OUTSIDE_UP) == OUTSIDE_UP) {
    //                     newOutside = OUTSIDE_UP | OUTSIDE_RIGHT;
    //                 }
    //                 if (neighbor == 'L' && (outside & OUTSIDE_UP) == OUTSIDE_UP) {
    //                     newOutside = OUTSIDE_DOWN | OUTSIDE_LEFT;
    //                 }
    //                 if (neighbor == 'F' && (outside & OUTSIDE_UP) == OUTSIDE_UP) {
    //                     newOutside = OUTSIDE_UP | OUTSIDE_LEFT;
    //                 }
    //                 if (neighbor == 'F' && (outside & OUTSIDE_DOWN) == OUTSIDE_DOWN) {
    //                     newOutside = OUTSIDE_DOWN | OUTSIDE_RIGHT;
    //                 }

    //                 if (newOutside == -1)
    //                     throw new RuntimeException();

    //                 q.add(new Coord(row, col - 1, newOutside));
    //             }
    //             // right
    //             if (col < lines.size() - 1 && "S-J7".indexOf(lines.get(row).charAt(col + 1)) >= 0 && "S-LF".indexOf(c) >= 0) {
    //                 char neighbor = lines.get(row).charAt(col + 1);
    //                 int newOutside = -1;
    //                 if (neighbor == '-' && (outside & OUTSIDE_UP) == OUTSIDE_UP) {
    //                     newOutside = OUTSIDE_UP;
    //                 }
    //                 if (neighbor == '-' && (outside & OUTSIDE_DOWN) == OUTSIDE_DOWN) {
    //                     newOutside = OUTSIDE_DOWN;
    //                 }
    //                 if (neighbor == 'J' && (outside & OUTSIDE_UP) == OUTSIDE_UP) {
    //                     newOutside = OUTSIDE_UP | OUTSIDE_LEFT;
    //                 }
    //                 if (neighbor == 'J' && (outside & OUTSIDE_DOWN) == OUTSIDE_DOWN) {
    //                     newOutside = OUTSIDE_DOWN | OUTSIDE_RIGHT;
    //                 }
    //                 if (neighbor == '7' && (outside & OUTSIDE_UP) == OUTSIDE_UP) {
    //                     newOutside = OUTSIDE_UP | OUTSIDE_RIGHT;
    //                 }
    //                 if (neighbor == '7' && (outside & OUTSIDE_DOWN) == OUTSIDE_DOWN) {
    //                     newOutside = OUTSIDE_DOWN | OUTSIDE_LEFT;
    //                 }

    //                 if (newOutside == -1) throw new RuntimeException();

    //                 q.add(new Coord(row, col + 1, newOutside));
    //             }
    //             // up
    //             if (row > 0 && "S|7F".indexOf(lines.get(row - 1).charAt(col)) >= 0 && "S|LJ".indexOf(c) >= 0) {
    //                 char neighbor = lines.get(row - 1).charAt(col);
    //                 int newOutside = -1;
    //                 if (neighbor == '|' && (outside & OUTSIDE_LEFT) == OUTSIDE_LEFT) {
    //                     newOutside = OUTSIDE_LEFT;
    //                 }
    //                 if (neighbor == '|' && (outside & OUTSIDE_RIGHT) == OUTSIDE_RIGHT) {
    //                     newOutside = OUTSIDE_RIGHT;
    //                 }
    //                 if (neighbor == '7' && (outside & OUTSIDE_LEFT) == OUTSIDE_LEFT) {
    //                     newOutside = OUTSIDE_LEFT | OUTSIDE_DOWN;
    //                 }
    //                 if (neighbor == '7' && (outside & OUTSIDE_RIGHT) == OUTSIDE_RIGHT) {
    //                     newOutside = OUTSIDE_RIGHT | OUTSIDE_UP;
    //                 }
    //                 if (neighbor == 'F' && (outside & OUTSIDE_LEFT) == OUTSIDE_LEFT) {
    //                     newOutside = OUTSIDE_LEFT | OUTSIDE_UP;
    //                 }
    //                 if (neighbor == 'F' && (outside & OUTSIDE_RIGHT) == OUTSIDE_RIGHT) {
    //                     newOutside = OUTSIDE_RIGHT | OUTSIDE_DOWN;
    //                 }

    //                 if (newOutside == -1)
    //                     throw new RuntimeException();

    //                 q.add(new Coord(row - 1, col, newOutside));
    //             }
    //             // down
    //             if (row < lines.size() - 1 && "S|LJ".indexOf(lines.get(row + 1).charAt(col)) >= 0 && "S|LF".indexOf(c) >= 0) {
    //                 char neighbor = lines.get(row + 1).charAt(col);
    //                 int newOutside = -1;
    //                 if (neighbor == '|' && (outside & OUTSIDE_LEFT) == OUTSIDE_LEFT) {
    //                     newOutside = OUTSIDE_LEFT;
    //                 }
    //                 if (neighbor == '|' && (outside & OUTSIDE_RIGHT) == OUTSIDE_RIGHT) {
    //                     newOutside = OUTSIDE_RIGHT;
    //                 }
    //                 if (neighbor == 'L' && (outside & OUTSIDE_LEFT) == OUTSIDE_LEFT) {
    //                     newOutside = OUTSIDE_LEFT | OUTSIDE_DOWN;
    //                 }
    //                 if (neighbor == 'L' && (outside & OUTSIDE_RIGHT) == OUTSIDE_RIGHT) {
    //                     newOutside = OUTSIDE_RIGHT | OUTSIDE_UP;
    //                 }
    //                 if (neighbor == 'J' && (outside & OUTSIDE_LEFT) == OUTSIDE_LEFT) {
    //                     newOutside = OUTSIDE_LEFT | OUTSIDE_UP;
    //                 }
    //                 if (neighbor == 'J' && (outside & OUTSIDE_RIGHT) == OUTSIDE_RIGHT) {
    //                     newOutside = OUTSIDE_RIGHT | OUTSIDE_DOWN;
    //                 }

    //                 if (newOutside == -1) throw new RuntimeException();

    //                 q.add(new Coord(row + 1, col, newOutside));
    //             }
    //         }
    //         steps++;
    //     }
    //     System.err.println(steps);
    //     return seen;
    // }

    static Coord findStart(List<String> lines) {
        for (int r = 0; r < lines.size(); r++) {
            String line = lines.get(r);
            for (int c = 0; c < line.length(); c++) {
                if (line.charAt(c) == 'S') {
                    return new Coord(r, c);
                }
            }
        }
        throw new RuntimeException();
    }

    // static int OUTSIDE_LEFT = 1;
    // static int OUTSIDE_UP = 2;
    // static int OUTSIDE_RIGHT = 4;
    // static int OUTSIDE_DOWN = 8;
    static enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    record Coord(int row, int col) {
        Coord neighbor(Direction dir) {
            return switch (dir) {
                case DOWN -> new Coord(row + 1, col);
                case LEFT -> new Coord(row, col - 1);
                case RIGHT -> new Coord(row, col + 1);
                case UP -> new Coord(row - 1, col);

            };
        }
    }
    record Input(List<String> lines) {
        Optional<Character> neighbor(Coord coord, Direction dir) {
            return switch (dir) {
                case UP -> coord.row > 0
                        ? Optional.of(lines.get(coord.row - 1).charAt(coord.col))
                        : Optional.empty();
                case DOWN -> coord.row < lines.size() - 1
                        ? Optional.of(lines.get(coord.row + 1).charAt(coord.col))
                        : Optional.empty();
                case LEFT -> coord.col > 0
                        ? Optional.of(lines.get(coord.row).charAt(coord.col - 1))
                        : Optional.empty();
                case RIGHT -> coord.col < lines.get(coord.row).length() - 1
                        ? Optional.of(lines.get(coord.row).charAt(coord.col + 1))
                        : Optional.empty();
            };
        }
        char cell(Coord coord) {
            return lines.get(coord.row).charAt(coord.col);
        }
    }

}
