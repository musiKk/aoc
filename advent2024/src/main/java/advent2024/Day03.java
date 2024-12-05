package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class Day03 {
    public static void main(String[] args) throws Exception {
        var lines = Files.lines(Path.of("input03")).toList();
        part1(lines);
        part2(lines);
    }

    static void part1(List<String> lines) {
        Pattern p = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
        long sum = lines.stream()
            .flatMap(s -> p.matcher(s).results().map(r -> Mul.of(r.group(1), r.group(2))))
            .map(Mul::eval)
            .reduce(0L, Long::sum);
        System.err.println(sum);
    }

    static void part2(List<String> lines) {
        Pattern p = Pattern.compile("(mul)\\((\\d{1,3}),(\\d{1,3})\\)|(do)\\(\\)|(don't)\\(\\)");
        var instructions = lines.stream()
            .flatMap(s -> p.matcher(s).results().map(r -> {
                if (r.group(1) != null) {
                    return Mul.of(r.group(2), r.group(3));
                } else if (r.group(4) != null) {
                    return DO;
                } else if (r.group(5) != null) {
                    return DONT;
                } else {
                    throw new RuntimeException("unknown instruction " + r.group());
                }
            }))
                // switch (r.group(1)) {
                //     case "mul" -> Mul.of(r.group(2), r.group(3));
                //     case "do" -> DO;
                //     case "don't" -> DONT;
                //     default -> throw new RuntimeException("unknown instruction " + r.group(1));
                // }))
                .toList();
        long sum = 0;
        boolean enabled = true;
        for (var instruction : instructions) {
            if (instruction == DO) {
                enabled = true;
            } else if (instruction == DONT) {
                enabled = false;
            } else if (enabled && instruction instanceof Mul mul) {
                sum += mul.eval();
            }
        }

        System.err.println(sum);
    }

    interface Instruction {}

    static Instruction DO = new Instruction() { };
    static Instruction DONT = new Instruction() { };

    record Mul(int n1, int n2) implements Instruction {
        static Mul of(String s1, String s2) {
            return new Mul(Integer.parseInt(s1), Integer.parseInt(s2));
        }
        long eval() {
            return n1 * n2;
        }
    }
}
