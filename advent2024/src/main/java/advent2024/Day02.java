package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day02 {
    public static void main(String[] args) throws Exception {
        var reports = Files.lines(Path.of("input02"))
            .map(s -> s.split(" "))
            .map(ss -> Stream.of(ss).map(s -> Integer.parseInt(s)).toList()).toList();
        part1(reports);
        part2(reports);
    }

    static void part1(List<List<Integer>> reports) {
        int safe = 0;
        for (var report : reports) {
            if (safe(report)) safe++;
        }
        System.err.println(safe);
    }

    static void part2(List<List<Integer>> reports) {
        int safe = 0;
        for (var report : reports) {
            if (safe(report)) {
                safe++;
            } else {
                for (int levelToSkip = 0; levelToSkip < report.size(); levelToSkip++) {
                    List<Integer> reportCopy = new ArrayList<>();
                    for (int i = 0; i < report.size(); i++) {
                        if (i == levelToSkip) continue;
                        reportCopy.add(report.get(i));
                    }
                    if (safe(reportCopy)) {
                        safe++;
                        break;
                    }
                }
            }
        }
        System.err.println(safe);
    }

    static boolean safe(List<Integer> report) {
        if (report.get(0) == report.get(1)) return false;
        boolean up = report.get(0) < report.get(1);

        for (int i = 0; i < report.size() - 1; i++) {
            int cur = report.get(i);
            int next = report.get(i + 1);

            int diff = Math.abs(cur - next);
            if (diff < 1 || diff > 3) return false;

            if (up && (cur > next)) return false;
            if (!up && (cur < next)) return false;
        }
        return true;
    }
}
