package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day11 {
    public static void main(String[] args) throws Exception {
        var numbers = Files.lines(Path.of("input11"))
                .map(s -> Arrays.stream(s.split(" ")).map(Long::parseLong))
                .findFirst()
                .get()
                .toList();

        part1(numbers);
        part2(numbers);
    }

    static void part2(List<Long> numbers) {
        long total = 0;
        Map<String, Long> cache = new HashMap<>();
        for (var num : numbers) {
            var stones = rec(num, 75, cache);
            total += stones;
        }
        System.err.println(total);
    }

    static long rec(long num, int blinksRemaining, Map<String, Long> cache) {
        if (blinksRemaining == 0) return 1;

        String cacheKey = num + "," + blinksRemaining;

        if (cache.containsKey(cacheKey)) return cache.get(cacheKey);

        long result;
        if (num == 0) {
            result = rec(1, blinksRemaining - 1, cache);
        } else {
            String numString = Long.toString(num);
            if (numString.length() % 2 == 0) {
                result = rec(Long.parseLong(numString.substring(numString.length() / 2)),
                        blinksRemaining - 1, cache);
                result += rec(Long.parseLong(numString.substring(0, numString.length() / 2)),
                        blinksRemaining - 1, cache);
            } else {
                result = rec(num * 2024, blinksRemaining - 1, cache);
            }
        }

        cache.put(cacheKey, result);
        return result;
    }

    static void part1(List<Long> numbers) {
        Deque<Stone> stones = numbers.stream()
                .map(n -> new Stone(n, 25))
                .collect(Collectors.toCollection(() -> new ArrayDeque<>()));

        long finalStones = 0;
        while (!stones.isEmpty()) {

            var stone = stones.remove();

            if (stone.blinksLeft == 0) {
                finalStones++;
                continue;
            }

            if (stone.number == 0) {
                stones.push(new Stone(1, stone.blinksLeft - 1));
                continue;
            }

            String numString = Long.toString(stone.number);
            if (numString.length() % 2 == 0) {
                stones.push(new Stone(
                        Long.parseLong(numString.substring(numString.length() / 2)),
                        stone.blinksLeft - 1));
                stones.push(new Stone(
                        Long.parseLong(numString.substring(0, numString.length() / 2)),
                        stone.blinksLeft - 1));
                continue;
            }

            stones.push(new Stone(stone.number * 2024, stone.blinksLeft - 1));
        }

        System.err.println(finalStones);
    }

    record Stone(long number, int blinksLeft) {
    }
}
