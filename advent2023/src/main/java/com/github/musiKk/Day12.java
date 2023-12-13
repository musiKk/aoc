package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Day12 {

    public static void main(String[] args) throws Throwable {
        Path p = Path.of("input12.example");
        List<Spring> input;
        try (var stream = Files.lines(p)) {
            input = stream.map(Spring::of).toList();
        }

        // part1(input);
        // part2(input);

        var x = calculateArrangements(Spring.of(".??..??...?##. 1,1,3"));
        // var x = calculateArrangements(Spring.of(".??..??...?##. 1,1,3").quintuple());
        System.err.println(x);
    }

    static void part2(List<Spring> input) {
        int total = input.stream()
            .map(Spring::quintuple)
            .map(Day12::calculateArrangements)
            .mapToInt(Integer::intValue)
            .sum();
        System.err.println(total);
    }

    static void part1(List<Spring> input) {
        int total = input.stream()
                .map(Day12::calculateArrangements)
                .mapToInt(Integer::intValue)
                .sum();
        System.err.println(total);
    }

    static int calculateArrangements(Spring spring) {
        // return rec(spring.conditions, spring.groups, 0, 0);
        char[] cur = new char[spring.conditions.length()];
        Arrays.fill(cur, '.');
        Map<String, Integer> cache = new HashMap<>();
        var r = rec(cur, spring, 0, 0, cache);
        System.err.println(cache);
        System.err.println(spring + " -> " + r);
        return r;
    }

    static int rec(char[] cur, Spring spring, int cIdx, int gIdx, Map<String, Integer> cache) {
        String cacheKey = cIdx + "-" + gIdx;

        System.err.print(Arrays.toString(cur) + "; ");
        System.err.println("in cache: " + cacheKey + " -> " + cache.get(cacheKey));
        try { Thread.sleep(1); } catch (Exception e) {}

        // if (cache.containsKey(cacheKey))
        //     return cache.get(cacheKey);

        int[] groups = spring.groups;
        if (cIdx >= cur.length && gIdx != groups.length) return 0;
        if (gIdx == groups.length) {
            var result = spring.match(cur) ? 1 : 0;
            cache.put(cacheKey, result);
            return result;
        }

        int arrangements = 0;
        while (cIdx < cur.length) {
            int group = groups[gIdx];
            int start = cIdx;
            int last = cIdx + group;
            if (last > cur.length) break;

            for (int i = start; i < last; i++) {
                cur[i] = '#';
            }
            arrangements += rec(cur, spring, last + 1, gIdx + 1, cache);
            for (int i = start; i < last; i++) {
                cur[i] = '.';
            }
            cIdx = start + 1;
        }
        System.err.println("arrangements: " + cacheKey + " -> " + arrangements);
        cache.put(cacheKey, arrangements);
        return arrangements;
    }

    // static int rec(String conditions, int[] groups, int cIdx, int gIdx) {
    //     if (gIdx == groups.length) return 1;
    //     int arrangements = 0;
    //     for (int i = cIdx; i < conditions.length(); i++) {
    //         char c = conditions.charAt(i);
    //         if (c == '.') continue;
    //     }
    //     return arrangements;
    // }

    record Spring(String conditions, int[] groups) {
        static Spring of(String input) {
            String[] parts = input.split(" ");
            return new Spring(
                parts[0],
                Arrays.stream(parts[1].split(",")).mapToInt(Integer::parseInt).toArray()
            );
        }
        boolean match(char[] configuration) {
            for (int i = 0; i < configuration.length; i++) {
                if (conditions.charAt(i) == '?') continue;
                if (conditions.charAt(i) == '.' && configuration[i] != '.') return false;
                if (conditions.charAt(i) == '#' && configuration[i] != '#') return false;
            }
            return true;
        }
        Spring quintuple() {
            int[] newGroups = new int[groups.length * 5];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < groups.length; j++) {
                    newGroups[groups.length * i + j] = groups[j];
                }
            }
            return new Spring(
                String.format("%s,%s,%s,%s,%s", conditions, conditions, conditions, conditions, conditions),
                newGroups
            );
        }
    }

}
