package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Day12 {

    public static void main(String[] args) throws Throwable {
        Path p = Path.of("input12.txt");
        List<Spring> input;
        try (var stream = Files.lines(p)) {
            input = stream.map(Spring::of).toList();
        }

        part1(input);
        part2(input);
    }

    static void part2(List<Spring> input) {
        long total = input.stream()
            .map(Spring::quintuple)
            .map(Day12::calculateArrangements)
            .mapToLong(Long::longValue)
            .sum();
        System.err.println(total);
    }

    static void part1(List<Spring> input) {
        long total = input.stream()
                .map(Day12::calculateArrangements)
                .mapToLong(Long::longValue)
                .sum();
        System.err.println(total);
    }

    static long calculateArrangements(Spring spring) {
        return recNew(spring, 0, 0, new HashMap<>());
    }

    static long recNew(Spring spring, int cIdx, int gIdx, Map<String, Long> cache) {
        String cacheKey = cIdx + "-" + gIdx;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        String conditions = spring.conditions;
        int[] groups = spring.groups;
        int len = spring.conditions.length();
        if (cIdx >= len && gIdx != groups.length) return 0;
        if (gIdx == groups.length) {
            long rv = 1;
            for (int i = cIdx; i < len; i++) {
                if (conditions.charAt(i) == '#') {
                    rv = 0;
                }
            }
            cache.put(cacheKey, rv);
            return rv;
        }

        long arrangements = 0;

        int group = groups[gIdx];
        while (cIdx < len) {
            if (cIdx > 0 && conditions.charAt(cIdx - 1) == '#') {
                // this means we're skipping a mandatory block
                break;
            }
            // match group
            boolean match = true;
            for (int cand = cIdx; cand < cIdx + group; cand++) {
                if (cand == len) {
                    match = false;
                    break;
                }
                char ch = conditions.charAt(cand);
                if (ch == '.') {
                    match = false;
                    break;
                }
            }

            if (match) {
                Character postChar = cIdx + group >= len ? null : conditions.charAt(cIdx + group);
                if (postChar == null || postChar != '#') {
                    arrangements += recNew(spring, cIdx + group + 1, gIdx + 1, cache);
                }
            }
            cIdx++;
        }

        cache.put(cacheKey, arrangements);
        return arrangements;
    }

    record Spring(String conditions, int[] groups) {
        static Spring of(String input) {
            String[] parts = input.split(" ");
            return new Spring(
                parts[0],
                Arrays.stream(parts[1].split(",")).mapToInt(Integer::parseInt).toArray()
            );
        }
        Spring quintuple() {
            int[] newGroups = new int[groups.length * 5];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < groups.length; j++) {
                    newGroups[groups.length * i + j] = groups[j];
                }
            }
            return new Spring(
                String.format("%s?%s?%s?%s?%s", conditions, conditions, conditions, conditions, conditions),
                newGroups
            );
        }
    }

}
