package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Day05 {

    public static void main(String[] args) throws Throwable {
        var almanac = readAlmanac();

        // part1(almanac);
        part2(almanac);
        // System.err.println(almanac);
    }

    // static void part2(Almanac almanac) {
    //     List<Range> newRanges = new ArrayList<>();
    //     AlmanacMap map = almanac.getDestinationForSource("seed");

    //     while (true) {
    //         AlmanacMap nextMap = almanac.getDestinationForSource(map.dest);

    //         for (Range srcRange : map.ranges) {
    //             long srcDestFirst = srcRange.destStart;
    //             long srdDestLast = srcRange.destStart + srcRange.length - 1;
    //             for (Range destRange : nextMap.ranges) {
    //                 // if srcRange.dest & destRange.src overlap:
    //                 // create new range for overlap mapping src.src directly to dest.dest

    //                 long destSrcFirst = destRange.sourceStart;
    //                 long destSrcLast = destRange.sourceStart + destRange.length - 1;


    //             }
    //         }
    //     }
    // }

    static void part2(Almanac almanac) throws Throwable {
        var executor = Executors.newFixedThreadPool(8);
        List<Callable<Long>> tasks = new ArrayList<>();
        for (int i = 0; i < almanac.seeds.size(); i += 2) {
            final int idx = i;
            Callable<Long> callable = new Callable<>() {
                @Override
                public Long call() throws Exception {
                    long minLoc = Long.MAX_VALUE;
                    long start = almanac.seeds.get(idx);
                    long end = start + almanac.seeds.get(idx + 1) - 1;
                    for (long seed = start; seed < end; seed++) {
                        AlmanacMap map = almanac.getDestinationForSource("seed");
                        long valueToMap = seed;
                        while (!map.dest().equals("location")) {
                            valueToMap = map.mapValue(valueToMap);
                            map = almanac.getDestinationForSource(map.dest());
                        }
                        minLoc = Math.min(minLoc, map.mapValue(valueToMap));

                        if (seed % 50_000_000L == 0)
                            // System.err.println(idx + ": " + ((seed - start) / (end - start)) + "% " + seed);
                            System.err.printf("%02d: %.2f%% (%d)%n", idx, ((float) seed - start) / (end - start), seed);
                    }
                    return minLoc;
                }
            };
            tasks.add(callable);
        }
        var results = executor.invokeAll(tasks);
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.DAYS);

        for (var result : results) {
            System.err.println(result.get());
        }
    }

    static void part1(Almanac almanac) {
        long minLoc = Long.MAX_VALUE;
        for (long seed : almanac.seeds()) {
            AlmanacMap map = almanac.getDestinationForSource("seed");
            long valueToMap = seed;
            while (!map.dest().equals("location")) {
                valueToMap = map.mapValue(valueToMap);
                map = almanac.getDestinationForSource(map.dest());
            }
            minLoc = Math.min(minLoc, map.mapValue(valueToMap));
        }
        System.err.println(minLoc);
    }

    static Almanac readAlmanac() throws Throwable {
        Path path = Path.of("input05.txt");
        List<String> lines;
        try (var s = Files.lines(path)) {
            lines = s.toList();
        }
        List<Long> seeds = new ArrayList<>();
        List<AlmanacMap> maps = new ArrayList<>();
        String currentSource = null;
        String currentDest = null;
        List<Range> currentRanges = null;

        for (String line : lines) {
            if (line.startsWith("seeds")) {
                seeds = Arrays.stream(line.substring("seeds: ".length()).split(" "))
                    .map(Long::parseLong)
                    .toList();
            } else if (line.endsWith("map:")) {
                var pattern = Pattern.compile("(.*?)-to-(.*?) map:");
                var matcher = pattern.matcher(line);
                if (!matcher.find()) throw new RuntimeException();

                currentSource = matcher.group(1);
                currentDest = matcher.group(2);
                currentRanges = new ArrayList<>();
            } else if (!line.equals("")) {
                String[] rangeParts = line.split(" ");
                currentRanges.add(new Range(
                    Long.parseLong(rangeParts[0]),
                    Long.parseLong(rangeParts[1]),
                    Long.parseLong(rangeParts[2])
                ));
            } else if (currentSource != null) {
                long minRange = Long.MAX_VALUE;
                long maxRange = 0;
                for (Range r : currentRanges) {
                    minRange = Math.min(minRange, r.sourceStart);
                    maxRange = Math.max(maxRange, r.sourceStart + r.length - 1);
                }
                if (minRange != 0) {
                    currentRanges.add(new Range(0, 0, minRange));
                }
                currentRanges.add(new Range(maxRange + 1, maxRange + 1, Long.MAX_VALUE - maxRange - 1));
                maps.add(new AlmanacMap(currentSource, currentDest, currentRanges));
            }
        }
        maps.add(new AlmanacMap(currentSource, currentDest, currentRanges));

        return new Almanac(seeds, maps);
    }

    record Almanac(List<Long> seeds, List<AlmanacMap> maps) {
        AlmanacMap getDestinationForSource(String source) {
            return maps
                .stream()
                .filter(m -> m.source().equals(source))
                .findFirst()
                .orElseThrow();
        }
    }
    record AlmanacMap(String source, String dest, List<Range> ranges) {
        long mapValue(long value) {
            for (Range range : ranges) {
                if (value >= range.sourceStart && value < range.sourceStart + range.length) {
                    return range.destStart + (value - range.sourceStart);
                }
            }
            return value;
        }
    }
    record Range(long destStart, long sourceStart, long length) {}

}
