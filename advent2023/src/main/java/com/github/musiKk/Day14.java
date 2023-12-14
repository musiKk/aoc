package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Day14 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input14.txt");
        char[][] field;
        try (var stream = Files.lines(p)) {
            var l = stream.map(String::toCharArray).toList();
            field = new char[l.size()][];
            for (int i = 0; i < l.size(); i++) {
                field[i] = l.get(i);
            }
        }

        char[][] field1 = new char[field.length][];
        for (int r = 0; r < field.length; r++) {
            field1[r] = field[r].clone();
        }
        part1(field1);
        part2(field);
    }

    static void part2(char[][] field) {
        int cycles = 1_000_000_000;

        Set<Integer> seen = new HashSet<>();

        int tillRepeat = 0;
        while (true) {
            try { Thread.sleep(1); } catch (Exception e) {}
            int hc = Arrays.stream(field).map(Arrays::hashCode).toList().hashCode();
            if (seen.contains(hc)) {
                break;
            }
            seen.add(hc);
            tillRepeat++;

            north(field);
            west(field);
            south(field);
            east(field);
        }

        int tillCycle = 0;
        seen.clear();
        while (true) {
            try { Thread.sleep(1); } catch (Exception e) {}
            int hc = Arrays.stream(field).map(Arrays::hashCode).toList().hashCode();
            if (seen.contains(hc)) {
                break;
            }
            seen.add(hc);
            tillCycle++;

            north(field);
            west(field);
            south(field);
            east(field);
        }

        int remaining = (cycles - tillRepeat) % tillCycle;
        for (int i = 0; i < remaining; i++) {
            north(field);
            west(field);
            south(field);
            east(field);
        }

        System.err.println(load(field));
    }

    static void part1(char[][] field) {
        north(field);
        System.err.println(load(field));
    }

    static int load(char[][] field) {
        int totalLoad = 0;
        for (int row = 0; row < field.length; row++) {
            char[] fieldRow = field[row];
            for (int col = 0; col < fieldRow.length; col++) {
                if (fieldRow[col] == 'O') totalLoad += field.length - row;
            }
        }
        return totalLoad;
    }

    static void west(char[][] field) {
        int width = field[0].length;
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < field.length; row++) {
                char c = field[row][col];
                if (c != 'O') continue;

                int destCol = col - 1;
                while (destCol >= 0 && field[row][destCol] == '.') destCol--;

                field[row][col] = '.';
                field[row][destCol + 1] = 'O';
            }
        }
    }

    static void east(char[][] field) {
        int width = field[0].length;
        for (int col = width - 1; col >= 0; col--) {
            for (int row = 0; row < field.length; row++) {
                char c = field[row][col];
                if (c != 'O') continue;

                int destCol = col + 1;
                while (destCol < width && field[row][destCol] == '.') destCol++;

                field[row][col] = '.';
                field[row][destCol - 1] = 'O';
            }
        }
    }

    static void south(char[][] field) {
        for (int row = field.length - 1; row >= 0; row--) {
            char[] fieldRow = field[row];
            for (int col = 0; col < fieldRow.length; col++) {
                char c = fieldRow[col];
                if (c != 'O') continue;

                int destRow = row + 1;
                while (destRow < field.length && field[destRow][col] == '.') destRow++;

                fieldRow[col] = '.';
                field[destRow - 1][col] = 'O';
            }
        }
    }

    static void north(char[][] field) {
        for (int row = 0; row < field.length; row++) {
            char[] fieldRow = field[row];
            for (int col = 0; col < fieldRow.length; col++) {
                char c = fieldRow[col];
                if (c != 'O') continue;

                int destRow = row - 1;
                while (destRow >= 0 && field[destRow][col] == '.') destRow--;

                fieldRow[col] = '.';
                field[destRow + 1][col] = 'O';
            }
        }
    }

}
