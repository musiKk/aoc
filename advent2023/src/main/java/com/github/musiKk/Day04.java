package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day04 {

    public static void main(String[] args) throws Throwable {
        Path p = Path.of("input04.txt");
        List<Card> cards;
        try (var s = Files.lines(p)) {
            cards = s.map(Card::of).toList();
        }

        part1(cards);
        part2(cards);
    }

    static void part2(List<Card> cards) {
        long[] counts = new long[cards.size()];
        Arrays.fill(counts, 1);

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            var winners = card.winningNumbers;
            var tips = card.numbers;
            int wins = 0;
            for (int winner : winners) {
                if (tips.contains(winner)) {
                    wins++;
                }
            }

            for (int j = 0; j < wins && i + j + 1 < cards.size(); j++) {
                counts[i + j + 1] += counts[i];
            }
        }

        long result = Arrays.stream(counts).sum();
        System.err.println(result);
    }

    static void part1(List<Card> cards) {
        int score = 0;
        for (Card card : cards) {
            var winners = card.winningNumbers;
            var tips = card.numbers;
            int wins = 0;
            for (int winner : winners) {
                if (tips.contains(winner)) {
                    wins++;
                }
            }
            if (wins > 0) {
                score += 1 << (wins - 1);
            }
        }
        System.err.println(score);
    }

    record Card(int id, Set<Integer> winningNumbers, Set<Integer> numbers) {
        static Pattern linePattern = Pattern.compile("^Card +?(\\d+): (.*) \\| (.*)$");
        static Card of(String input) {
            Matcher m = linePattern.matcher(input);
            if (!m.find()) throw new RuntimeException();

            int id = Integer.parseInt(m.group(1));

            String[] winningNumberStrings = m.group(2).split(" +");
            String[] numberStrings = m.group(3).split(" +");

            return new Card(
                id,
                Arrays.stream(winningNumberStrings).filter(s -> s.length() > 0).map(Integer::parseInt).collect(Collectors.toSet()),
                Arrays.stream(numberStrings).filter(s -> s.length() > 0).map(Integer::parseInt).collect(Collectors.toSet())
            );
        }
    }

}
