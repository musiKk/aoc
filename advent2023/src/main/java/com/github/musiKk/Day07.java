package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;

public class Day07 {

    public static void main(String[] args) throws Throwable {
        List<Hand> hands = readInput();

        part1(new ArrayList<>(hands), false);
        part1(new ArrayList<>(hands), true);
    }

    static void part1(List<Hand> hands, boolean jokers) {
        hands.sort(new HandSorter(jokers));
        // hands.stream().forEach(System.err::println);

        long result = 0;
        for (int i = 0; i < hands.size(); i++) {
            result += hands.get(i).bid * (i + 1);
        }
        System.err.println(result);
    }

    @AllArgsConstructor
    static class HandSorter implements Comparator<Hand> {
        boolean jokers;
        @Override
        public int compare(Hand o1, Hand o2) {
            int typeDiff = jokers
                ? o1.jType - o2.jType
                : o1.type - o2.type;
            if (typeDiff != 0) return typeDiff;

            for (int i = 0; i < 5; i++) {
                int cardDiff = jokers
                ? Hand.jCardIdx[o1.cards.charAt(i)] - Hand.jCardIdx[o2.cards.charAt(i)]
                : Hand.cardIdx[o1.cards.charAt(i)] - Hand.cardIdx[o2.cards.charAt(i)];
                if (cardDiff != 0) return cardDiff;
            }
            throw new RuntimeException();
        }
    }

    static List<Hand> readInput() throws Throwable {
        var p = Path.of("input07.txt");
        try (var s = Files.lines(p)) {
            return s.map(l -> {
                var parts = l.split(" ");
                return Hand.of(Integer.parseInt(parts[1]), parts[0]);
            }).toList();
        }
    }

    record Hand(int bid, String cards, int type, int jType) {
        static int[] cardIdx = new int[256];
        static int[] jCardIdx = new int[256];
        static {
            cardIdx['2'] = 0;
            cardIdx['3'] = 1;
            cardIdx['4'] = 2;
            cardIdx['5'] = 3;
            cardIdx['6'] = 4;
            cardIdx['7'] = 5;
            cardIdx['8'] = 6;
            cardIdx['9'] = 7;
            cardIdx['T'] = 8;
            cardIdx['J'] = 9;
            cardIdx['Q'] = 10;
            cardIdx['K'] = 11;
            cardIdx['A'] = 12;

            jCardIdx['J'] = 0;
            jCardIdx['2'] = 1;
            jCardIdx['3'] = 2;
            jCardIdx['4'] = 3;
            jCardIdx['5'] = 4;
            jCardIdx['6'] = 5;
            jCardIdx['7'] = 6;
            jCardIdx['8'] = 7;
            jCardIdx['9'] = 8;
            jCardIdx['T'] = 9;
            jCardIdx['Q'] = 10;
            jCardIdx['K'] = 11;
            jCardIdx['A'] = 12;
        }
        // static int calculateJType(String cards) {
        //     int[] counts = new int[13];
        //     int jokers = 0;
        //     for (char c : cards.toCharArray()) {
        //         if (c == 'J') {
        //             jokers++;
        //             continue;
        //         }
        //         counts[cardIdx[c]]++;
        //     }
        //     int pairs = 0;
        //     int triples = 0;
        //     int quadruples = 0;
        //     int quintuples = 0;
        //     for (int count : counts) {
        //         if (count == 2) pairs++;
        //         if (count == 3) triples++;
        //         if (count == 4) quadruples++;
        //         if (count == 5) quintuples++;
        //     }
        //     // 5 of a kind
        //     if (quintuples == 1) return 6;
        //     if (quadruples == 1 && jokers == 1) return 6;
        //     if (triples == 1 && jokers == 2) return 6;
        //     if (pairs == 1 && jokers == 3) return 6;
        //     if (jokers == 4) return 6;
        //     if (jokers == 5) return 6;
        //     // 4 of a kind
        //     if (quadruples == 1) return 5;
        //     if (triples == 1 && jokers == 1) return 5;
        //     if (pairs == 1 && jokers == 2) return 5;
        //     if (jokers == 3) return 5;
        //     // full house
        //     if (pairs == 1 && triples == 1) return 4;
        //     if (pairs == 2 && jokers == 1) return 4;
        //     // 3 of a kind
        //     if (triples == 1) return 3;
        //     if (pairs == 1 && jokers == 1) return 3;
        //     if (jokers == 2) return 3;
        //     // 2 pair
        //     if (pairs == 2) return 2;
        //     // 1 pair
        //     if (pairs == 1) return 1;
        //     if (jokers == 1) return 1;
        //     // high card
        //     return 0;
        // }

        static int calculateJType(String cards) {
            int[] counts = new int[13];
            int jokers = 0;
            for (char c : cards.toCharArray()) {
                if (c == 'J') {
                    jokers++;
                    continue;
                }
                counts[jCardIdx[c]]++;
            }
            Arrays.sort(counts);
            counts[12] += jokers;
            int pairs = 0;
            int triples = 0;
            int quadruples = 0;
            int quintuples = 0;
            for (int count : counts) {
                if (count == 2) pairs++;
                if (count == 3) triples++;
                if (count == 4) quadruples++;
                if (count == 5) quintuples++;
            }
            // 5 of a kind
            if (quintuples == 1) return 6;
            // 4 of a kind
            if (quadruples == 1) return 5;
            // full house
            if (pairs == 1 && triples == 1) return 4;
            // 3 of a kind
            if (triples == 1) return 3;
            // 2 pair
            if (pairs == 2) return 2;
            // 1 pair
            if (pairs == 1) return 1;
            // high card
            return 0;
        }
        static int calculateType(String cards) {
            int[] counts = new int[13];
            for (char c : cards.toCharArray()) {
                counts[cardIdx[c]]++;
            }
            int pairs = 0;
            int triples = 0;
            int quadruples = 0;
            int quintuples = 0;
            for (int count : counts) {
                if (count == 2) pairs++;
                if (count == 3) triples++;
                if (count == 4) quadruples++;
                if (count == 5) quintuples++;
            }
            // 5 of a kind
            if (quintuples == 1) return 6;
            // 4 of a kind
            if (quadruples == 1) return 5;
            // full house
            if (pairs == 1 && triples == 1) return 4;
            // 3 of a kind
            if (triples == 1) return 3;
            // 2 pair
            if (pairs == 2) return 2;
            // 1 pair
            if (pairs == 1) return 1;
            // high card
            return 0;
        }
        static Hand of(int bid, String cards) {
            return new Hand(bid, cards, calculateType(cards), calculateJType(cards));
        }
    }

}
