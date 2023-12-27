package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day24 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input24.example");
        List<Hailstone> hailstones;
        try (var s = Files.lines(p)) {
            hailstones = s.map(Hailstone::parse).toList();
        }

        part1(hailstones);
    }

    static void part2(List<Hailstone> hailstones) {

    }

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
        var crossing = f1.crossing(f2);
        boolean crosses = crossing.x >= MIN && crossing.x <= MAX && crossing.y >= MIN && crossing.y <= MAX;
        boolean crossesInFutureH1 = (crossing.x - h1.x) / h1.dx >= 0;
        boolean crossesInFutureH2 = (crossing.x - h2.x) / h2.dx >= 0;
        // System.err.println("Hailstone A: " + h1);
        // System.err.println("Hailstone B: " + h2);
        // System.err.println("crossing: " + crossing + " (inside: " + crosses + ", in future: " + crossesInFutureH1 + crossesInFutureH2 + ")");
        // System.err.println();
        return crosses && crossesInFutureH1 && crossesInFutureH2;
    }

    record Coord(double x, double y, double z) {}

    record Formula(double slope, double y0) {
        Coord crossing(Formula other) {
            double xCross = (other.y0 - this.y0) / (this.slope - other.slope);
            double yCross = xCross * slope + y0;
            return new Coord(xCross, yCross, 0);
        }
        static Formula of(Hailstone h) {
            double slope = (double) h.dy / h.dx;
            double y0 = (double) h.y - ((double) h.x / h.dx) * h.dy;
            return new Formula(slope, y0);
        }
    }

    record Hailstone(long x, long y, long z, long dx, long dy, long dz) {
        static Hailstone parse(String input) {
            var lr = input.split(" @ ");
            var ls = lr[0].split(", ");
            var rs = lr[1].split(", ");

            return new Hailstone(
                Long.parseLong(ls[0].strip()),
                Long.parseLong(ls[1].strip()),
                Long.parseLong(ls[2].strip()),
                Long.parseLong(rs[0].strip()),
                Long.parseLong(rs[1].strip()),
                Long.parseLong(rs[2].strip())
            );
        }
    }

}
