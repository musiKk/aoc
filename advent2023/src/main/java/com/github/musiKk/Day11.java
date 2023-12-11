package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day11 {

    public static void main(String[] args) throws Throwable{
        Path p = Path.of("input11.txt");
        Space space;
        try (var s = Files.lines(p)) {
            space = Space.of(s.toList());
        }

        solve(space, 2);
        solve(space, 10);
        solve(space, 100);
        solve(space, 1000000);
    }

    static void solve(Space space, int expansionFactor) {
        long totalDistance = 0;
        int numGalaxies = space.galaxies.size();
        for (int g1Idx = 0; g1Idx < numGalaxies - 1; g1Idx++) {
            Coord g1 = space.galaxies.get(g1Idx);
            for (int g2Idx = g1Idx + 1; g2Idx < numGalaxies; g2Idx++) {
                Coord g2 = space.galaxies.get(g2Idx);
                int steps = 0;
                for (int r = Math.min(g1.row, g2.row); r < Math.max(g1.row, g2.row); r++) {
                    if (space.emptyRows.contains(r)) {
                        steps += expansionFactor;
                    } else {
                        steps++;
                    }
                }
                for (int c = Math.min(g1.col, g2.col); c < Math.max(g1.col, g2.col); c++) {
                    if (space.emptyCols.contains(c)) {
                        steps += expansionFactor;
                    } else {
                        steps++;
                    }
                }
                totalDistance += steps;
            }
        }
        System.err.println(totalDistance);
    }

    record Space(List<Coord> galaxies, Set<Integer> emptyRows, Set<Integer> emptyCols) {
        static Space of(List<String> input) {
            List<Coord> galaxies = new ArrayList<>();
            Set<Integer> emptyRows = new HashSet<>();
            Set<Integer> emptyCols = new HashSet<>();

            int rows = input.size();
            int cols = input.get(0).length();

            for (int i = 0; i < cols; i++) {
                emptyCols.add(i);
            }

            for (int i = 0; i < rows; i++) {
                boolean galaxyInRow = false;
                for (int j = 0; j < cols; j++) {
                    char c = input.get(i).charAt(j);
                    if (c == '#') {
                        galaxyInRow = true;
                        galaxies.add(new Coord(i, j));
                        emptyCols.remove(j);
                    }
                }
                if (!galaxyInRow) {
                    emptyRows.add(i);
                }
            }

            return new Space(galaxies, emptyRows, emptyCols);
        }
    }

    record Coord(int row, int col) {}

}
