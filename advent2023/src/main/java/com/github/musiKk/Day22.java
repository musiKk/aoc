package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Day22 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input22.txt");
        List<Brick> bricks;
        try (var s = Files.lines(p)) {
            bricks = new ArrayList<>(s.map(Brick::parse).toList());
        }

        var settledBricks = part1(bricks);
        part2(settledBricks);
    }

    static void part2(List<Brick> bricks) {
        int totalFallen = 0;
        for (int i = 0; i < bricks.size(); i++) {
            List<Brick> bricksWithRemoved = new ArrayList<>(bricks);
            bricksWithRemoved.remove(i);

            int fallenBricks = settleBricks(bricksWithRemoved);
            totalFallen += fallenBricks;
            System.err.printf("removing %d caused %d to fall (now %d)%n", i, fallenBricks, totalFallen);
        }
        System.err.println(totalFallen);
    }

    static int settleBricks(List<Brick> bricks) {
        int settlingBricks = 0;
        Map<XY, Brick> coordToMaxBrick = new HashMap<>();
        for (Brick brick : bricks) {
            // System.err.println("looking at brick " + brick);
            var cubes = brick.allCubes();

            Set<XY> footprint = new HashSet<>(cubes.stream().map(XY::of).toList());
            var bricksOverlappingFootprint = new HashSet<>(footprint.stream()
                    .<Brick>flatMap(xy -> Optional.ofNullable(coordToMaxBrick.get(xy)).stream())
                    .toList());

            var supportingHeight = bricksOverlappingFootprint.stream().mapToInt(Brick::maxZ).max().orElse(0);

            int sinkBy = brick.minZ() - supportingHeight - 1;
            if (sinkBy > 0) {
                settlingBricks++;
            }

            var sunkenBrick = brick.sinkBy(sinkBy);
            for (var xy : footprint) {
                coordToMaxBrick.put(xy, sunkenBrick);
            }
        }
        return settlingBricks;
    }

    static List<Brick> part1(List<Brick> bricks) {
        bricks.sort(Comparator.<Brick>comparingInt(Brick::minZ));
        Map<XY, Brick> coordToMaxBrick = new HashMap<>();

        Map<Brick, List<Brick>> supports = new HashMap<>();
        Set<Brick> removable = new HashSet<>();
        List<Brick> updatedBricks = new ArrayList<>();
        for (Brick brick : bricks) {
            // System.err.println("looking at brick " + brick);
            var cubes = brick.allCubes();

            Set<XY> footprint = new HashSet<>(cubes.stream().map(XY::of).toList());
            var bricksOverlappingFootprint = new HashSet<>(footprint.stream()
                    .<Brick>flatMap(xy -> Optional.ofNullable(coordToMaxBrick.get(xy)).stream())
                    .toList());

            var supportingHeight = bricksOverlappingFootprint.stream().mapToInt(Brick::maxZ).max().orElse(0);
            var supportingBricks = new HashSet<>(bricksOverlappingFootprint.stream().filter(b -> b.maxZ() == supportingHeight).toList());

            int sinkBy = brick.minZ() - supportingHeight - 1;
            if (supportingBricks.size() > 1) {
                removable.addAll(supportingBricks);
            }

            var sunkenBrick = brick.sinkBy(sinkBy);
            for (var supportingBrick : supportingBricks) {
                supports.computeIfAbsent(supportingBrick, __ -> new ArrayList<>()).add(sunkenBrick);
            }
            for (var xy : footprint) {
                coordToMaxBrick.put(xy, sunkenBrick);
            }
            updatedBricks.add(sunkenBrick);
        }

        for (var updatedBrick : updatedBricks) {
            if (!supports.containsKey(updatedBrick)) {
                removable.add(updatedBrick);
            }
        }

        var bricksSupportingNothing = supports.entrySet().stream().filter(e -> e.getValue().isEmpty()).map(Map.Entry::getKey).toList();
        removable.addAll(bricksSupportingNothing);

        Map<Brick, List<Brick>> supportedBy = new HashMap<>();
        for (var e : supports.entrySet()) {
            e.getValue().stream().forEach(b -> supportedBy.computeIfAbsent(b, __ -> new ArrayList<>()).add(e.getKey()));
        }
        for (var e : supportedBy.entrySet()) {
            if (e.getValue().size() == 1) {
                // remove bricks that are the only support for another brick
                removable.remove(e.getValue().get(0));
            }
        }
        // System.err.println(removable);
        System.err.println(removable.size());
        return updatedBricks;
    }

    record XY(int x, int y) {
        static XY of(Cube c) {
            return new XY(c.x, c.y);
        }
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
        Cube sinkBy(int diff) {
            return new Cube(x, y, z - diff);
        }
    }
    static char CURRENT_CUBE_NAME = 'A';
    record Brick(String name, Cube start, Cube end) {
        static Brick parse(String input) {
            var parts = input.split("~");
            return new Brick(
                Character.toString(CURRENT_CUBE_NAME++),
                Cube.parse(parts[0]),
                Cube.parse(parts[1])
            );
        }
        Brick sinkBy(int diff) {
            return new Brick(name, start.sinkBy(diff), end.sinkBy(diff));
        }
        int minZ() {
            return Math.min(start.z, end.z);
        }
        int maxZ() {
            return Math.max(start.z, end.z);
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
