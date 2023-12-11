package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day08 {

    public static void main(String[] args) throws Throwable {
        Input input = readInput();
        System.err.println(input);

        // part1(input);
        // part2BruteForce(input);
        part2(input);
    }

    static void part2(Input input) {
        List<String> starts = new ArrayList<>(input.network.keySet().stream().filter(e -> e.endsWith("A")).toList());
        for (String start : starts) {
            int idx = 0;
            // Map<String, Integer> seen = new HashMap<>();
            String current = start;
            int goalSteps = 0;
            int cycleSteps = 0;
            while (true) {
                // if (seen.containsKey(current + "-" + (idx % input.instructions.length()))) {
                //     cycleSteps = idx - seen.get(current + "-" + (idx % input.instructions.length()));
                //     break;
                // }
                // seen.put(current + "-" + (idx % input.instructions.length()), idx);
                char instruction = input.getInstruction(idx++);
                // System.err.printf("from %s to ", current);
                current = switch (instruction) {
                    case 'L' -> input.network.get(current).left;
                    case 'R' -> input.network.get(current).right;
                    default -> throw new RuntimeException();
                };
                if (current.endsWith("Z") && goalSteps == 0) {
                    goalSteps = idx;
                    break;
                }
                // System.err.println(current);
                // try {
                //     Thread.sleep(10);
                // } catch (InterruptedException e1) {
                //     e1.printStackTrace();
                // }
            }
            // System.err.printf("start %s: goal: %d, cycle: %d%n", start, goalSteps, cycleSteps);
            System.err.printf("start %s: %d%n", start, idx);
        }
    }

    static void part2BruteForce(Input input) {
        int idx = 0;
        List<String> currents = new ArrayList<>(input.network.keySet().stream().filter(e -> e.endsWith("A")).toList());
        while (true) {
            if (idx % 1__000_000 == 0) System.err.println(idx);
            char instruction = input.getInstruction(idx++);
            int zs = 0;
            for (int currentsIdx = 0; currentsIdx < currents.size(); currentsIdx++) {
                String current = currents.get(currentsIdx);
                String next = switch (instruction) {
                    case 'L' -> input.network.get(current).left;
                    case 'R' -> input.network.get(current).right;
                    default -> throw new RuntimeException();
                };
                if (next.endsWith("Z")) {
                    zs++;
                }
                currents.set(currentsIdx, next);
            }
            if (zs == currents.size()) {
                break;
            }
        }
        System.err.println(idx);
    }

    static void part1(Input input) {
        int idx = 0;
        String current = "AAA";
        while (true) {
            char instruction = input.getInstruction(idx++);
            String next = switch (instruction) {
                case 'L' -> input.network.get(current).left;
                case 'R' -> input.network.get(current).right;
                default -> throw new RuntimeException();
            };
            if (next.equals("ZZZ")) {
                break;
            }
            current = next;
        }
        System.err.println(idx);
    }

    static Input readInput() throws Throwable {
        Path p = Path.of("input08.txt");
        try (var stream = Files.lines(p)) {
            var lines = stream.toList();

            String instructions = lines.get(0);
            Map<String, Next> network = new HashMap<>();
            lines.stream().skip(2).forEach(line -> {
                var instruction = line.split(" = ");
                var left = instruction[1].substring(1, 4);
                var right = instruction[1].substring(6, 9);
                network.put(instruction[0], new Next(left, right));
            });
            return new Input(instructions, network);
        }
    }

    record Input(String instructions, Map<String, Next> network) {
        char getInstruction(int i) {
            return instructions.charAt(i % instructions.length());
        }
    }

    record Next(String left, String right) {}

}
