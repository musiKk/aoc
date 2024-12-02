package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day01 {
    public static void main(String[] args) throws Exception {
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();

        Files.lines(Path.of("input01"))
            .forEach(line -> {
                var parts = line.split(" +");
                left.add(Integer.parseInt(parts[0]));
                right.add(Integer.parseInt(parts[1]));
            });

        part1(left, right);
        part2(left, right);
    }

    private static void part2(List<Integer> left, List<Integer> right) {
        var rightCounts = right.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long similarityScore = 0;
        for (int leftNum : left) {
            var rightCount = rightCounts.getOrDefault(leftNum, 0L);
            similarityScore += leftNum * rightCount;
        }
        System.err.println(similarityScore);
    }

    private static void part1(List<Integer> left, List<Integer> right) throws Exception {

        Collections.sort(left);
        Collections.sort(right);

        long totalDist = 0;
        for (int i = 0; i < left.size(); i++) {
            int dist = Math.abs(left.get(i) - right.get(i));
            totalDist += dist;
        }
        System.err.println(totalDist);
    }
}
