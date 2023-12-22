package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day18 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input18.example");

        List<Instruction> instructions = new ArrayList<>();
        List<Instruction> instructionsPart2 = new ArrayList<>();
        try (var stream = Files.lines(p)) {
            stream.forEach(l -> {
                instructions.add(Instruction.parse(l));
                instructionsPart2.add(Instruction.parsePart2(l));
            });
        }

        // System.err.println(instructions);
        // System.err.println(instructionsPart2);

        // part1(instructions);
        part2(instructionsPart2);
    }

    static void part2(List<Instruction> instructions) {
        int row = 0;
        int col = 0;
        int maxRow = 0;
        int maxCol = 0;
        int minRow = 0;
        int minCol = 0;
        long dug = 0;

        List<Line> horizontals = new ArrayList<>();
        List<Line> verticals = new ArrayList<>();

        Coord prev = new Coord(row, col);

        for (var instruction : instructions) {
            System.err.println(instruction);
            var dir = instruction.dir;
            var len = instruction.len;

            row = dir.newRow(row, len);
            col = dir.newCol(col, len);

            Coord next = new Coord(row, col);
            var line = Line.of(prev, next);
            if (dir == Dir.L || dir == Dir.R)
                horizontals.add(line);
            if (dir == Dir.U || dir == Dir.D)
                verticals.add(line);
            prev = next;

            maxRow = Math.max(maxRow, row);
            minRow = Math.min(minRow, row);
            maxCol = Math.max(maxCol, col);
            minCol = Math.min(minCol, col);
            dug += len;
        }

        horizontals.sort(Comparator.comparingInt(Line::startRow).thenComparingInt(Line::startCol));
        verticals.sort(Comparator.comparingInt(Line::startCol).thenComparingInt(Line::startRow));

        // System.err.println("top left: " + minRow + ", " + minCol);
        // System.err.println("bot rght: " + maxRow + ", " + maxCol);
        // System.err.println("ended at: " + row + ", " + col);
        // System.err.println("ended at " + prev);
        // System.err.println(dug);
        horizontals.stream().forEach(System.err::println);
        System.err.println();
        verticals.stream().forEach(System.err::println);

        // Deque<Coord> q = new ArrayDeque<>();
        // q.add(Coord.of(row + 1, col + 1));
        // while (!q.isEmpty()) {
        //     Coord coord = q.remove();

        //     var rowLine = horizontals.stream().filter(l -> l.start.row > coord.row && l.start.col > coord.col).findFirst();
        //     var colLine = verticals.stream().filter(l -> l.start.row > coord.row && l.start.col > coord.col).findFirst();

        //     System.err.println(rowLine);
        //     System.err.println(colLine);
        // }

        int totalRows = maxRow - minRow;

        long inside = 0;
        for (int r = minRow; r <= maxRow; r++) {
            int absRow = r - minRow;

            int lastCol = minCol;
            boolean in = false;
            for (Line colLine : verticals) {
                if (colLine.start.row >= r || colLine.end.row < r) continue;
                if (in) {
                    inside += colLine.start.col - lastCol - 1;
                    in = false;
                } else {
                    lastCol = colLine.start.col + 1;
                    in = true;
                }
            }
            if (absRow % 10_000 == 0) {
                System.err.printf("%-5d: %.2f - %d%n", absRow, ((double) absRow / totalRows) * 100, inside);
            }
            // try { Thread.sleep(1); } catch (Exception e) {}
            // if (absRow == 100) break;
        }
        System.err.println(inside + dug);

    }

    record Line(Coord start, Coord end) {
        int startRow() {
            return start.row;
        }
        int startCol() {
            return start.col;
        }
        static Line of(Coord start, Coord end) {
            if (start.row == end.row) {
                if (start.col < end.col) {
                    return new Line(start, end);
                } else {
                    return new Line(end, start);
                }
            } else if (start.col == end.col) {
                if (start.row < end.row) {
                    return new Line(start, end);
                } else {
                    return new Line(end, start);
                }
            } else {
                throw new RuntimeException();
            }
        }
    }

    static void part1(List<Instruction> instructions) {
        int row = 0;
        int col = 0;
        int maxRow = 0;
        int maxCol = 0;
        int minRow = 0;
        int minCol = 0;
        Set<Coord> dug = new HashSet<>();
        dug.add(Coord.of(0, 0));
        for (var instruction : instructions) {
            var dir = instruction.dir;
            for (int i = 0; i < instruction.len; i++) {
                row = dir.newRow(row, 1);
                col = dir.newCol(col, 1);

                maxRow = Math.max(maxRow, row);
                minRow = Math.min(minRow, row);
                maxCol = Math.max(maxCol, col);
                minCol = Math.min(minCol, col);
                dug.add(Coord.of(row, col));
            }
        }

        Map<Coord, Type> groundMap = new HashMap<>();
        fillGroundMap(Coord.of(minRow, minCol), Coord.of(maxRow, maxCol), dug, groundMap);

        long dugCoord = groundMap.values().stream().filter(Type.IN::equals).count();
        System.err.println(dugCoord);
    }

    static void fillGroundMap(Coord topLeft, Coord botRight, Set<Coord> dug, Map<Coord, Type> groundMap) {
        dug.forEach(coord -> {
            groundMap.put(coord, Type.IN);
        });

        for (int row = topLeft.row; row <= botRight.row; row++) {
            for (int col = topLeft.col; col <= botRight.col; col++) {
                if (groundMap.containsKey(Coord.of(row, col))) continue;

                visit(topLeft, botRight, row, col, groundMap);
            }
        }
    }

    static void visit(Coord topLeft, Coord botRight, int startRow, int startCol, Map<Coord, Type> groundMap) {
        Deque<Coord> q = new ArrayDeque<>();
        q.add(Coord.of(startRow, startCol));

        Type type = Type.IN;
        while (!q.isEmpty()) {
            Coord coord = q.remove();

            int row = coord.row;
            int col = coord.col;
            if (row < topLeft.row || row > botRight.row || col < topLeft.col || col > botRight.col) {
                type = Type.OUT;
                continue;
            }

            if (groundMap.containsKey(coord)) continue;
            groundMap.put(coord, Type.UNKNOWN);

            q.add(Coord.of(row + 1, col));
            q.add(Coord.of(row - 1, col));
            q.add(Coord.of(row, col + 1));
            q.add(Coord.of(row, col - 1));
        }

        for (var e : groundMap.entrySet()) {
            if (e.getValue() == Type.UNKNOWN) {
                e.setValue(type);
            }
        }
    }

    enum Type {
        IN, OUT, UNKNOWN;
    }

    record Coord(int row, int col) {
        static Coord of(int row, int col) {
            return new Coord(row, col);
        }
    }

    enum Dir {
        R, D, L, U;
        int newRow(int row, int len) {
            return switch (this) {
                case U -> row - len;
                case D -> row + len;
                default -> row;
            };
        }
        int newCol(int col, int len) {
            return switch (this) {
                case L -> col - len;
                case R -> col + len;
                default -> col;
            };
        }
    }
    record Instruction(Dir dir, int len) {
        static Instruction parse(String line) {
            String[] parts = line.split(" ");
            return new Instruction(Dir.valueOf(parts[0]), Integer.parseInt(parts[1]));
        }
        static Instruction parsePart2(String line) {
            String[] parts = line.split(" ");
            String hexPart = parts[2];
            int len = Integer.parseInt(hexPart.substring(2, 7), 16);
            return new Instruction(Dir.values()[hexPart.charAt(7) - '0'], len);
        }
    }

}
