package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class Day19 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input19.example");
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

        // part1(workflows, materials);
        part23(workflows);

        // Map<String, Workflow> testWorkflow = new HashMap<>();
        // testWorkflow.put("in", Workflow.of("x<1000:A,m<1000:R,R"));
        // part22(testWorkflow);

        // var r1 = new Restriction(Bound.unrestricted(), Bound.unrestricted(), Bound.unrestricted(), new Bound(1, 1250));
        // var r2 = Restriction.nothing();

        // System.err.println(r1);
        // System.err.println(r2);
        // System.err.println(r1.combine(r2));
    }

    static void part23(Map<String, Workflow> workflows) {
        var r = rec3(workflows, "in", Restriction.unrestricted());
        System.err.println(r);
    }

    static void part22(Map<String, Workflow> workflows) {
        Set<Integer> xs = new HashSet<>();
        Set<Integer> ms = new HashSet<>();
        Set<Integer> as = new HashSet<>();
        Set<Integer> ss = new HashSet<>();
        fill(xs);
        fill(ms);
        fill(as);
        fill(ss);

        rec2(workflows, "in", xs, ms, as, ss);

        long res = (long) xs.size() * ms.size() * as.size() * ss.size();
        System.err.println(res);
    }

    static Restriction rec3(Map<String, Workflow> workflows, String state, Restriction restriction) {
        var flow = workflows.get(state);

        var returnRestriction = Restriction.nothing();

        for (var cond : flow.conditions) {
            var rRes = calculateRestriction(cond);
            if (rRes.next.isPresent()) {
                returnRestriction.union(rec3(workflows, rRes.next.get(), restriction.intersect(rRes.r)));
            } else {
                returnRestriction.union(rRes.r);
            }
        }
        return returnRestriction;
    }

    static RestrictionResult calculateRestriction(Condition cond) {
        if (cond instanceof StaticCondition sc) {
            if (sc.next.equals("R")) {
                return new RestrictionResult(Restriction.nothing(), Optional.empty());
            }
            if (sc.next.equals("A")) {
                return new RestrictionResult(Restriction.unrestricted(), Optional.empty());
            }
            return new RestrictionResult(Restriction.unrestricted(), Optional.of(sc.next));
        } else {
            CompCondition cc = (CompCondition) cond;

            Bound lowerBound = new Bound(1, cc.value - 1);
            Bound upperBound = new Bound(cc.value + 1, 4000);

            Bound returnBound;
            if (cc.next.equals("R")) {
                returnBound = cc.lessThan ? upperBound : lowerBound;
            } else {
                returnBound = cc.lessThan ? lowerBound : upperBound;
            }

            Bound xb = cc.category == 'x' ? returnBound : Bound.unrestricted();
            Bound mb = cc.category == 'm' ? returnBound : Bound.unrestricted();
            Bound ab = cc.category == 'a' ? returnBound : Bound.unrestricted();
            Bound sb = cc.category == 's' ? returnBound : Bound.unrestricted();
            Optional<String> next = "RA".indexOf(cc.next) < 0 ? Optional.of(cc.next) : Optional.empty();

            return new RestrictionResult(new Restriction(xb, mb, ab, sb), next);

        }
    }

    record RestrictionResult(Restriction r, Optional<String> next) {}

    record Bound(int l, int u) {
        static Bound unrestricted() {
            return new Bound(1, 4000);
        }
        static Bound nothing() {
            return new Bound(4001, 0);
        }
        Bound combine(Bound other) {
            return new Bound(Math.min(l, other.l), Math.max(u, other.u));
        }
        Bound subtract(Bound other) {
            return new Bound(Math.max(l, other.l), Math.min(u, other.u));
        }
    }
    record Restriction(Bound x, Bound m, Bound a, Bound s) {
        static Restriction unrestricted() {
            return new Restriction(
                Bound.unrestricted(), Bound.unrestricted(), Bound.unrestricted(), Bound.unrestricted());
        }
        static Restriction nothing() {
            return new Restriction(Bound.nothing(), Bound.nothing(), Bound.nothing(), Bound.nothing());
        }
        Restriction union(Restriction o) {
            return new Restriction(x.combine(o.x), m.combine(o.m), a.combine(o.a), s.combine(o.s));
        }
        Restriction intersect(Restriction o) {
            return new Restriction(x.subtract(o.x), m.subtract(o.m), a.subtract(o.a), s.subtract(o.s));
        }
    }
    static void rec2(Map<String, Workflow> workflows, String state, Set<Integer> xs, Set<Integer> ms, Set<Integer> as, Set<Integer> ss) {
        Set<Integer> resultXs = new HashSet<>();
        Set<Integer> resultMs = new HashSet<>();
        Set<Integer> resultAs = new HashSet<>();
        Set<Integer> resultSs = new HashSet<>();

        var flow = workflows.get(state);
        for (var cond : flow.conditions) {
            // evaluate for cond
            HashSet<Integer> subXs = new HashSet<>(xs);
            HashSet<Integer> subMs = new HashSet<>(ms);
            HashSet<Integer> subAs = new HashSet<>(as);
            HashSet<Integer> subSs = new HashSet<>(ss);
            var nextState = evaluateCond(cond, subXs, subMs, subAs, subSs);
            nextState.stream().forEach(ns ->
                rec2(workflows, ns, subXs, subMs, subAs, subSs)
            );
            resultXs.addAll(subXs);
            resultMs.addAll(subMs);
            resultAs.addAll(subAs);
            resultSs.addAll(subSs);
            // intersect xs, ms, as, ss
        }

        // intersection
        xs.retainAll(resultXs);
        ms.retainAll(resultMs);
        as.retainAll(resultAs);
        ss.retainAll(resultSs);
    }

    static Optional<String> evaluateCond(Condition cond, Set<Integer> xs, Set<Integer> ms, Set<Integer> as, Set<Integer> ss) {
        if (cond instanceof StaticCondition sc) {
            if (sc.next.equals("R")) {
                xs.clear();
                ms.clear();
                as.clear();
                ss.clear();
            }
            return Optional.empty();
        } else {
            CompCondition cc = (CompCondition) cond;

            Set<Integer> setToRestrict = switch (cc.category) {
                case 'x' -> xs;
                case 'm' -> ms;
                case 'a' -> as;
                case 's' -> ss;
                default -> throw new RuntimeException();
            };

            int minRemove;
            int maxRemove;
            if (cc.next.equals("R")) {
                minRemove = cc.lessThan ? 1 : cc.value;
                maxRemove = cc.lessThan ? cc.value : 4000;
            } else {
                minRemove = cc.lessThan ? cc.value : 1;
                maxRemove = cc.lessThan ? 4000 : cc.value;
            }

            for (int i = minRemove; i <= maxRemove; i++) {
                setToRestrict.remove(i);
            }

            if (!cc.next.equals("R") && !cc.next.equals("A")) {
                return Optional.of(cc.next);
            }
            return Optional.empty();
        }
    }

    static void part2(Map<String, Workflow> workflows) {
        Set<Integer> possibleX = new HashSet<>();
        Set<Integer> possibleM = new HashSet<>();
        Set<Integer> possibleA = new HashSet<>();
        Set<Integer> possibleS = new HashSet<>();
        fill(possibleX);
        fill(possibleM);
        fill(possibleA);
        fill(possibleS);
        var possibles = rec("in", workflows, possibleX, possibleM, possibleA, possibleS);
        long r = 1;
        for (var p : possibles) {
            r *= p.size();
        }
        System.err.println(r);
    }

    static Set<Integer>[] rec(String state, Map<String, Workflow> workflows, Set<Integer> xs, Set<Integer> ms, Set<Integer> as, Set<Integer> ss) {
        System.err.println("looking into state " + state);
        var flow = workflows.get(state);

        // Set<Integer> xs = new HashSet<>();
        // Set<Integer> ms = new HashSet<>();
        // Set<Integer> as = new HashSet<>();
        // Set<Integer> ss = new HashSet<>();

        for (var cond : flow.conditions) {
            Set<Integer> possibleX = new HashSet<>(xs);
            Set<Integer> possibleM = new HashSet<>(ms);
            Set<Integer> possibleA = new HashSet<>(as);
            Set<Integer> possibleS = new HashSet<>(ss);
            // fill(possibleX);
            // fill(possibleM);
            // fill(possibleA);
            // fill(possibleS);

            restrictCondition(workflows, cond, possibleX, possibleM, possibleA, possibleS);

            // union across parallel conditions
            xs.addAll(possibleX);
            ms.addAll(possibleM);
            as.addAll(possibleA);
            ss.addAll(possibleS);
        }
        return new Set[] { xs, ms, as, ss };
    }

    static void restrictCondition(Map<String, Workflow> workflows, Condition cond, Set<Integer> xs, Set<Integer> ms, Set<Integer> as, Set<Integer> ss) {
        if (cond instanceof StaticCondition sc) {
            if (sc.next.equals("R")) {
                xs.clear();
                ms.clear();
                as.clear();
                ss.clear();
            }
        } else {
            CompCondition cc = (CompCondition) cond;
            if (cc.next.equals("R")) {
                Set<Integer> setToRestrict = switch (cc.category) {
                    case 'x' -> xs;
                    case 'm' -> ms;
                    case 'a' -> as;
                    case 's' -> ss;
                    default -> throw new RuntimeException();
                };
                int minRemove = cc.lessThan ? cc.value : 1;
                int maxRemove = cc.lessThan ? 4000 : cc.value;
                for (int i = minRemove; i <= maxRemove; i++) {
                    setToRestrict.remove(i);
                }
            } else if (cc.next.equals("A")) {
            } else {
                Set<Integer>[] res = rec(cc.next, workflows, xs, ms, as, ss);
                res[0].addAll(xs);
                res[1].addAll(ms);
                res[2].addAll(as);
                res[3].addAll(ss);
            }
        }
    }

    static void fill(Set<Integer> set) {
        for (int i = 1; i <= 4000; i++) {
            set.add(i);
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
