package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class Day19 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input19.txt");
        Map<String, Workflow> workflows = new HashMap<>();
        List<Material> materials = new ArrayList<>();

        try (var stream = Files.lines(p)) {
            stream.forEach(l -> {
                if (l.startsWith("{")) {
                    materials.add(Material.of(l));
                } else if (!l.isEmpty()) {
                    var we = WorkflowEntry.of(l);
                    workflows.put(we.name, we.workflow);
                }
            });
        }

        part1(workflows, materials);
        part2(workflows);
    }

    static void part2(Map<String, Workflow> workflows) {
        long solution = rec2(workflows, workflows.get("in").conditions, 0, Ranges.full());
        System.err.println(solution);
    }

    static long rec2(Map<String, Workflow> workflows, List<Condition> conditions, int idx, Ranges ranges) {
        if (idx == conditions.size()) return 0;

        var cond = conditions.get(idx);

        if (cond instanceof StaticCondition sc) {
            if (sc.next.equals("A")) {
                // System.err.println("accepted with ranges " + ranges);
                // System.err.println(" => " + ranges.value());
                return ranges.value();
            } else if (sc.next.equals("R")) {
                return 0;
            } else {
                return rec2(workflows, workflows.get(sc.next).conditions, 0, ranges);
            }
        } else if (cond instanceof CompCondition cc) {
            var inclusiveRange = ranges.inclusiveRanges(cc);
            var exclusiveRange = ranges.exclusiveRanges(cc);

            if ("RA".contains(cc.next)) {
                return rec2(workflows, List.of(new StaticCondition(cc.next)), 0, inclusiveRange)
                    + rec2(workflows, conditions, idx + 1, exclusiveRange);
            } else {
                return rec2(workflows, workflows.get(cc.next).conditions, 0, inclusiveRange)
                    + rec2(workflows, conditions, idx + 1, exclusiveRange);
            }
        } else {
            throw new RuntimeException();
        }
    }

    record Ranges(Range xr, Range mr, Range ar, Range sr) {
        static Ranges full() {
            return new Ranges(
                new Range(1, 4000),
                new Range(1, 4000),
                new Range(1, 4000),
                new Range(1, 4000)
            );
        }

        long value() {
            return xr.value() * mr.value() * ar.value() * sr.value();
        }

        Ranges inclusiveRanges(CompCondition cc) {
            return new Ranges(
                cc.category == 'x' ? xr.inclusive(cc) : xr,
                cc.category == 'm' ? mr.inclusive(cc) : mr,
                cc.category == 'a' ? ar.inclusive(cc) : ar,
                cc.category == 's' ? sr.inclusive(cc) : sr
            );
        }
        Ranges exclusiveRanges(CompCondition cc) {
            return new Ranges(
                cc.category == 'x' ? xr.exclusive(cc) : xr,
                cc.category == 'm' ? mr.exclusive(cc) : mr,
                cc.category == 'a' ? ar.exclusive(cc) : ar,
                cc.category == 's' ? sr.exclusive(cc) : sr
            );
        }
    }
    record Range(int low, int high) {
        long value() {
            return high < low ? 0 : (high - low + 1);
        }
        Range inclusive(CompCondition cc) {
            if (cc.lessThan) {
                return new Range(low, Math.min(high, cc.value - 1));
            } else {
                return new Range(Math.max(low, cc.value + 1), high);
            }
        }
        Range exclusive(CompCondition cc) {
            if (cc.lessThan) {
                return new Range(Math.max(low, cc.value), high);
            } else {
                return new Range(low, Math.min(high, cc.value));
            }
        }
    }

    static void part1(Map<String, Workflow> workflows, List<Material> materials) {
        var sum = materials.stream()
            .filter(m -> processMaterial(workflows, m))
            .mapToInt(m -> m.x + m.m + m.a + m.s)
            .sum();
        System.err.println(sum);
    }

    static boolean processMaterial(Map<String, Workflow> workflows, Material material) {
        String state = "in";
        while (true) {
            Workflow wf = workflows.get(state);
            var next = wf.conditions.stream()
                .flatMap(c -> c.nextFlow(material).stream())
                .findFirst()
                .get();
            if (next.equals("A")) {
                return true;
            } else if (next.equals("R")) {
                return false;
            } else {
                state = next;
            }
        }
    }

    record WorkflowEntry(String name, Workflow workflow) {
        static Pattern pattern = Pattern.compile("^(.*?)\\{(.*?)\\}$");
        static WorkflowEntry of(String line) {
            var matcher = pattern.matcher(line);
            matcher.matches();

            return new WorkflowEntry(matcher.group(1), Workflow.of(matcher.group(2)));
        }
    }
    record Workflow(List<Condition> conditions) {
        static Workflow of(String line) {
            String[] condStrings = line.split(",");
            var conds = Arrays.stream(condStrings)
                .map(Condition::of)
                .toList();
            return new Workflow(conds);
        }
    }
    interface Condition {
        Optional<String> nextFlow(Material material);

        static Pattern condPattern = Pattern.compile("^(.*)([<>])(.*?):(.*?)$");
        static Condition of(String input) {
            var matcher = condPattern.matcher(input);
            if (matcher.matches()) {
                return new CompCondition(
                    matcher.group(1).charAt(0),
                    matcher.group(2).equals("<"),
                    Integer.parseInt(matcher.group(3)),
                    matcher.group(4)
                );
            } else {
                return new StaticCondition(input);
            }
        }
    }
    record StaticCondition(String next) implements Condition {
        @Override
        public Optional<String> nextFlow(Material material) {
            return Optional.of(next);
        }
    }
    record CompCondition(char category, boolean lessThan, int value, String next) implements Condition {
        @Override
        public Optional<String> nextFlow(Material material) {
            int toCompare = switch (category) {
                case 'x' -> material.x;
                case 'm' -> material.m;
                case 'a' -> material.a;
                case 's' -> material.s;
                default -> throw new RuntimeException();
            };
            boolean val = lessThan ? toCompare < value : toCompare > value;
            return val ? Optional.of(next) : Optional.empty();
        }
    }
    record Material(int x, int m, int a, int s) {
        static Pattern pattern = Pattern.compile("^\\{x=(.*?),m=(.*?),a=(.*?),s=(.*?)\\}$");
        static Material of(String line) {
            var matcher = pattern.matcher(line);
            matcher.matches();
            return new Material(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3)),
                Integer.parseInt(matcher.group(4))
            );
        }
    }

}
