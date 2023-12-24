package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day22 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input22.example");
        List<Brick> bricks;
        try (var s = Files.lines(p)) {
            bricks = new ArrayList<>(s.map(Brick::parse).toList());
        }

        part1(bricks);
    }

    static void part1(List<Brick> bricks) {
        bricks.sort(Comparator.<Brick>comparingInt(c -> Math.max(c.start.z, c.end.z)).reversed());
        Set<Cube> supportingCubes = new HashSet<>();
        int safeToDisintegrate = 0;
        for (Brick brick : bricks) {
            var brickSupportsSomething = false;
            var brickCubes = brick.allCubes();

            Set<Cube> xyCubes = new HashSet<>();
            for (var brickCube : brickCubes) {
                var xyCube = new Cube(brickCube.x, brickCube.y, 0);
                xyCubes.add(xyCube);
            }

            for (var xyCube : xyCubes) {
                System.err.println("checking " + xyCube);
                if (supportingCubes.contains(xyCube)) {
                    brickSupportsSomething = true;
                }
                supportingCubes.add(xyCube);
            }

            if (!brickSupportsSomething) {
                System.err.println(brick + " is safe to disintegrate");
                safeToDisintegrate++;
            } else {
                System.err.println(brick + " is NOT safe to disintegrate");
            }
        }
        System.err.println(safeToDisintegrate);
    }

    record Cube(int x, int y, int z) {
        static Cube parse(String input) {
            var parts = input.split(",");
            return new Cube(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
            );
        }
    }
    record Brick(Cube start, Cube end) {
        static Brick parse(String input) {
            var parts = input.split("~");
            return new Brick(
                Cube.parse(parts[0]),
                Cube.parse(parts[1])
            );
        }
        List<Cube> allCubes() {
            int xMin = Math.min(start.x, end.x);
            int xMax = Math.max(start.x, end.x);
            int yMin = Math.min(start.y, end.y);
            int yMax = Math.max(start.y, end.y);
            int zMin = Math.min(start.z, end.z);
            int zMax = Math.max(start.z, end.z);
            List<Cube> result = new ArrayList<>();
            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        result.add(new Cube(x, y, z));
                    }
                }
            }
            return result;
        }
    }

}
