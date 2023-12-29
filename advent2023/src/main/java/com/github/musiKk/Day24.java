package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Day24 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input24.txt");
        List<Hailstone> hailstones;
        try (var s = Files.lines(p)) {
            hailstones = s.map(Hailstone::parse).toList();
        }

        // part1(hailstones);
        // part2(hailstones);
        // part22(hailstones);
        part2(hailstones.stream().limit(10).toList());
        // var h1 = new Hailstone(new Coord(19, 13, 30), 1, 0, -4);
        // var h2 = new Hailstone(new Coord(18, 19, 22), 2, -2, -4);
        // var h3 = new Hailstone(new Coord(20, 25, 34), 1, -3, -6);
        // System.err.println(Formula.of(h1).crossingXY(Formula.of(h2)));
        // System.err.println(Formula.of(h1).crossingXY(Formula.of(h3)));
        // System.err.println(Formula.of(h2).crossingXY(Formula.of(h3)));
    }

    static void part22(List<Hailstone> hailstones) {
        Map<Double, List<Hailstone>> occursX = new HashMap<>();
        // Map<Double, Integer> occursY = new HashMap<>();
        // Map<Double, Integer> occursZ = new HashMap<>();

        for (var h : hailstones) {
            occursX.computeIfAbsent(h.dx, __ -> new ArrayList<>()).add(h);
        }
        removeOnes(occursX);

        System.err.println(occursX);

        for (var hs : occursX.values()) {
            int len = hs.size();
            for (int i = 0; i < len - 1; i++) {
                for (int j = i + 1; j < len; j++) {
                    var h1 = hs.get(i);
                    var h2 = hs.get(j);
                    long diff = (long) Math.abs(h1.coord.x - h2.coord.x);
                    System.err.println(" -> " + diff);

                    List<Integer> factors = new ArrayList<>();
                    for(int factor = 2; factor < diff; factor++) {
                        while(diff % factor == 0) {
                            factors.add(factor);
                            diff /= factor;
                        }
                    }
                    factors.add((int) diff);

                    System.err.println(" -> factors: " + factors);
                }
            }
        }
    }

    static <T> void removeOnes(Map<Double, List<T>> m) {
        Set<Double> toRemove = new HashSet<>();
        for (var e : m.entrySet()) {
            if (e.getValue().size() == 1) {
                toRemove.add(e.getKey());
            }
        }
        for (var k : toRemove) {
            m.remove(k);
        }
    }

    static void part2(List<Hailstone> hailstones) {
        // int min = -10; int max = 10;
        int min = -500; int max = 500;
        List<CandSolution> xySolutions = new ArrayList<>();
        for (int candDx = min; candDx <= max; candDx++) {
            for (int candDy = min; candDy <= max; candDy++) {
                int dx = candDx;
                int dy = candDy;

                var adjustedHailstones = hailstones.stream()
                        .map(h -> new Hailstone(h.coord, h.dx - dx, h.dy - dy, h.dz))
                        .toList();

                var collisionPoint = getCollision(adjustedHailstones);
                if (collisionPoint.isPresent())
                    xySolutions.add(new CandSolution(collisionPoint.get(), dx));
            }
        }
        System.err.println(xySolutions);
        for (int candDz = min; candDz <= max; candDz++) {
            for (var xySolution : xySolutions) {
                int dx = xySolution.dx;
                int dz = candDz;
                var adjustedHailstones = hailstones.stream()
                        .map(h ->  new Hailstone(new Coord(h.coord.x, h.coord.z, 0), h.dx - dx, h.dz - dz, 0))
                        .toList();
                var collisionPoint = getCollision(adjustedHailstones);
                if (!collisionPoint.isPresent()) continue;

                System.err.println("solution:");
                System.err.println(xySolution);
                System.err.println(collisionPoint);
            }
        }
    }

    static Optional<Coord> getCollision(List<Hailstone> hailstones) {
        var formulas = hailstones.stream().map(Formula::of).toList();

        // Coord collisionPoint = null;
        // var f1 = formulas.get(0);
        // for (int i = 1; i < hailstones.size(); i++) {
        //     var f2 = formulas.get(i);

        //     if (f1.slope == f2.slope || Double.isInfinite(f1.slope) || Double.isInfinite(f2.slope)) continue;

        //     var newCollision = f1.crossingXY(f2);
        //     if (Double.isNaN(newCollision.x) || Double.isNaN(newCollision.y)) {
        //         continue;
        //     }
        //     if (collisionPoint == null) {
        //         collisionPoint = newCollision;
        //         continue;
        //     }
        //     if (!newCollision.almostEqual(collisionPoint)) {
        //         return Optional.empty();
        //     }
        // }

        Coord collisionPoint = null;
        for (int i = 0; i < hailstones.size() - 1; i++) {
            var f1 = formulas.get(i);
            for (int j = i + 1; j < hailstones.size(); j++) {
                var f2 = formulas.get(j);
                // if (f1.slope == f2.slope) {
                //     continue;
                // }
                var newCollision = f1.crossingXY(f2);
                if (Double.isNaN(newCollision.x) && Double.isNaN(newCollision.y)) {
                    continue;
                }
                if (collisionPoint == null) {
                    collisionPoint = newCollision;
                    continue;
                }
                if (!newCollision.almostEqual(collisionPoint)) {
                    // System.err.println("next collision is " + newCollision);
                    return Optional.empty();
                }
            }
        }
        return Optional.ofNullable(collisionPoint);
    }

    record CandSolution(Coord collision, int dx) {}

    static void part1(List<Hailstone> hailstones) {
        Map<Hailstone, Formula> formulaMap = new HashMap<>();
        for (var h : hailstones) {
            formulaMap.put(h, Formula.of(h));
        }

        int len = hailstones.size();
        int collisions = 0;
        for (int i = 0; i < len - 1; i++) {
            var h1 = hailstones.get(i);
            for (int j = i + 1; j < len; j++) {
                var h2 = hailstones.get(j);
                if (collidesXYInWindow(h1, h2, formulaMap)) collisions++;
            }
        }
        System.err.println(collisions);
    }

    static final long MIN = 200000000000000L;
    static final long MAX = 400000000000000L;

    // static final long MIN = 7;
    // static final long MAX = 27;

    static boolean collidesXYInWindow(Hailstone h1, Hailstone h2, Map<Hailstone, Formula> m) {
        var f1 = m.get(h1);
        var f2 = m.get(h2);
        var crossing = f1.crossingXY(f2);
        boolean crosses = crossing.x >= MIN && crossing.x <= MAX && crossing.y >= MIN && crossing.y <= MAX;
        boolean crossesInFutureH1 = (crossing.x - h1.coord.x) / h1.dx >= 0;
        boolean crossesInFutureH2 = (crossing.x - h2.coord.x) / h2.dx >= 0;
        // System.err.println("Hailstone A: " + h1);
        // System.err.println("Hailstone B: " + h2);
        // System.err.println("crossing: " + crossing + " (inside: " + crosses + ", in future: " + crossesInFutureH1 + crossesInFutureH2 + ")");
        // System.err.println();
        return crosses && crossesInFutureH1 && crossesInFutureH2;
    }

    // static final double EPS = 0.000001;
    static final double EPS = 10;

    record Coord(double x, double y, double z) {
        static Coord parse(String line) {
            var parts = line.split(",");
            return new Coord(
                Double.parseDouble(parts[0].strip()),
                Double.parseDouble(parts[1].strip()),
                Double.parseDouble(parts[2].strip())
            );
        }
        boolean almostEqual(Coord other) {
            return Math.abs(x - other.x) < EPS
                && Math.abs(y - other.y) < EPS
                && Math.abs(z - other.z) < EPS;
        }
    }

    record Formula(double slope, double y0) {
        Coord crossingXY(Formula other) {
            double xCross = (other.y0 - this.y0) / (this.slope - other.slope);
            double yCross = xCross * slope + y0;
            return new Coord(xCross, yCross, 0);
        }
        static Formula of(Hailstone h) {
            double slope = h.dy / h.dx;
            double y0 = h.coord.y - (h.coord.x / h.dx) * h.dy;
            return new Formula(slope, y0);
        }
        boolean almostEqual(Formula other) {
            return Math.abs(slope - other.slope) < EPS
                && Math.abs(y0 - other.y0) < EPS;
        }
    }

    record Hailstone(Coord coord, double dx, double dy, double dz) {
        static Hailstone parse(String input) {
            var lr = input.split(" @ ");
            var rs = lr[1].split(", ");

            return new Hailstone(
                Coord.parse(lr[0]),
                Double.parseDouble(rs[0].strip()),
                Double.parseDouble(rs[1].strip()),
                Double.parseDouble(rs[2].strip())
            );
        }
    }

}
