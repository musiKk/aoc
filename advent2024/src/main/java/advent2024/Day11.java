package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class Day11 {
    public static void main(String[] args) throws Exception {
        var numbers = Files.lines(Path.of("input11"))
                .map(s -> Arrays.stream(s.split(" ")).map(Long::parseLong))
                .findFirst()
                .get()
                .toList();

        part1(numbers);
    }

    static void part1(List<Long> numbers) {
        Deque<Stone> stones = numbers.stream()
                .map(n -> new Stone(n, 75))
                .collect(Collectors.toCollection(() -> new ArrayDeque<>()));

        long finalStones = 0;
        long steps = 0;
        while (!stones.isEmpty()) {
            System.err.println(stones);
            if (steps % 10000000 == 0) {
                System.err.printf("%d: %d (%d)%n", steps, stones.size(), finalStones);
            }
            steps++;

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
