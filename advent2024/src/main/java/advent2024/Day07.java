package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Day07 {
    public static void main(String[] args) throws Exception {
        var equations = Files.lines(Path.of("input07"))
            .map(Equation::of)
            .toList();
        part1(equations);
        part2(equations);
    }

    static void part2(List<Equation> equations) {
        var result = equations.stream()
            .filter(Day07::canBeValid2)
            .map(Equation::result)
            .reduce(0L, Long::sum);
        System.err.println(result);
    }

    static boolean canBeValid2(Equation equation) {
        var res = rec2(equation, 0, 0);
        return res;
    }

    static boolean rec2(Equation equation, int numIdx, long curResult) {
        if (curResult > equation.result) return false;
        if (numIdx == equation.numbers.size()) return curResult == equation.result;

        long curNum = equation.numbers.get(numIdx);
        if (numIdx == 0) {
            return rec2(equation, numIdx + 1, curResult + curNum);
        } else {
            long concatNum = curResult * (int) Math.pow(10, ((int) Math.log10(curNum) + 1)) + curNum;
            return rec2(equation, numIdx + 1, curResult + curNum)
                    || rec2(equation, numIdx + 1, curResult * curNum)
                    || rec2(equation, numIdx + 1, concatNum);
        }
    }

    static void part1(List<Equation> equations) {
        var result = equations.stream()
            .filter(Day07::canBeValid)
            .map(Equation::result)
            .reduce(0L, Long::sum);
        System.err.println(result);
    }

    static boolean canBeValid(Equation equation) {
        return rec(equation, 0, 0);
    }

    static boolean rec(Equation equation, int numIdx, long curResult) {
        if (curResult > equation.result) return false;
        if (numIdx == equation.numbers.size()) return curResult == equation.result;

        long curNum = equation.numbers.get(numIdx);
        if (numIdx == 0) {
            return rec(equation, numIdx + 1, curResult + curNum);
        } else {
            return rec(equation, numIdx + 1, curResult + curNum)
                    || rec(equation, numIdx + 1, curResult * curNum);
        }
    }

    record Equation(long result, List<Long> numbers) {
        static Equation of(String input) {
            var parts = input.split(": ");
            return new Equation(
                Long.parseLong(parts[0]),
                Stream.of(parts[1].split(" ")).map(Long::parseLong).toList());
        }
    }
}
