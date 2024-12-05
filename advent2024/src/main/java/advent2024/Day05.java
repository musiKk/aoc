package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Day05 {
    public static void main(String[] args) throws Exception {
        var lines = Files.lines(Path.of("input05")).toList();
        List<Rule> rules = new ArrayList<>();
        List<Update> updates = new ArrayList<>();

        for (var line : lines) {
            if (line.contains("|")) {
                rules.add(Rule.of(line));
            } else if (line.contains(",")) {
                updates.add(Update.of(line));
            }
        }

        var input = new Input(rules, updates);

        part1(input);
        part2(input);
    }

    static void part2(Input input) {
        long sum = input.updates.stream()
                .filter(u -> !isValidUpdate(u, input.rules))
                .map(u -> fixedUpdate(u, input.rules))
                .map(u -> u.pages.get(u.pages.size() / 2))
                .mapToLong(Long::valueOf)
                .sum();
        System.err.println(sum);
    }

    static Update fixedUpdate(Update update, List<Rule> rules) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> remaining = new HashSet<>(update.pages);
        while (!remaining.isEmpty()) {
            Integer chosen = null;
            candidateLoop: for (var candidate : remaining) {
                for (var shouldBeAfter : remaining) {
                    if (shouldBeAfter == candidate) continue;
                    var violatedRule = rules.stream()
                            .filter(p -> p.before == shouldBeAfter && p.after == candidate)
                            .findAny();
                    if (violatedRule.isPresent()) continue candidateLoop;
                }
                chosen = candidate;
                break;
            }
            result.add(chosen);
            remaining.remove(chosen);
        }
        return new Update(result);
    }

    static void part1(Input input) {
        long sum = input.updates.stream()
                .filter(u -> isValidUpdate(u, input.rules))
                .map(u -> u.pages.get(u.pages.size() / 2))
                .mapToLong(Long::valueOf)
                .sum();
        System.err.println(sum);
    }

    static boolean isValidUpdate(Update update, List<Rule> rules) {
        List<Integer> pages = update.pages;
        for (int pageIdx = 0; pageIdx < pages.size(); pageIdx++) {
            int page = pages.get(pageIdx);
            for (int followingPageIdx = pageIdx + 1; followingPageIdx < pages.size(); followingPageIdx++) {
                int followingPage = pages.get(followingPageIdx);
                for (var rule : rules) {
                    if (rule.before == followingPage && rule.after == page) return false;
                }
            }
        }
        return true;
    }

    record Update(List<Integer> pages) {
        static Update of(String input) {
            return new Update(Stream.of(input.split(",")).map(Integer::parseInt).toList());
        }
    }
    record Rule(int before, int after) {
        static Rule of(String input) {
            var parts = input.split("\\|");
            return new Rule(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]));
        }
    }
    record Input(List<Rule> rules, List<Update> updates) { }
}
