package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.ToString;

public class Day20 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input20.txt");
        Map<String, Module> plan = new HashMap<>();
        try (var s = Files.lines(p)) {
            s.forEach(line -> Module.insert(plan, line));
        }
        mapConjunctionInputs(plan);

        // plan.forEach((s, m) -> System.err.println(s + " -> " + m));

        part1(plan);
        part2(plan);
        // dumpPlantUml(plan);
    }

    static void dumpPlantUml(Map<String, Module> plan) {
        System.err.println("@startuml");
        System.err.println("[*] -> broadcaster");
        System.err.println("rx -> [*]");

        for (var e : plan.entrySet()) {
            var name = e.getKey();
            var module = e.getValue();

            for (var modOutput : module.outputs()) {
                System.err.printf("%s -> %s%n",
                    name(module, name),
                    name(plan.get(modOutput), modOutput));
            }
        }
        System.err.println("@enduml");
    }

    static String name(Module module, String name) {
        if (module instanceof Conjunction) {
            return "AMP" + name;
        } else if (module instanceof FlipFlop) {
            return "FF" + name;
        } else {
            return name;
        }
    }

    static void part2(Map<String, Module> plan) {
        long pulsesForLastAnd = 1;
        // found by visual inspection of the graph, the rest is guessing ¯\_(ツ)_/¯
        var lastAndInputs = List.of("zf", "qx", "rk", "cd");
        for (var lastAndInput : lastAndInputs) {
            reset(plan);
            for (int i = 1;;i++) {
                var r = trigger(plan, Optional.of(lastAndInput));
                if (r[2] == 1) {
                    System.err.println(lastAndInput + " -> " + i);
                    pulsesForLastAnd *= i;
                    break;
                }
            }
        }
        System.err.println(pulsesForLastAnd);
    }

    static void part1(Map<String, Module> plan) {
        long low = 0;
        long high = 0;
        for (int i = 0; i < 1000; i++) {
            var r = trigger(plan, Optional.empty());
            low += r[0];
            high += r[1];
        }
        System.err.println(low * high);
    }

    static int[] trigger(Map<String, Module> plan, Optional<String> target) {
        Deque<SignalPropagation> q = new ArrayDeque<>();
        q.add(new SignalPropagation(null, new Signal(Pulse.LOW, "broadcaster")));

        int low = 0;
        int high = 0;
        while (!q.isEmpty()) {
            int len = q.size();
            for (int i = 0; i < len; i++) {
                var signalPropagation = q.remove();

                switch (signalPropagation.signal.pulse) {
                    case LOW -> low++;
                    case HIGH -> high++;
                }

                var targetModule = plan.get(signalPropagation.signal.target);
                if (targetModule == null) continue;

                if (target.orElse("xxxxxx").equals(signalPropagation.signal.target) && signalPropagation.signal.pulse == Pulse.LOW) {
                    return new int[] { 0, 0, 1 };
                }

                var outputSignals = targetModule.pulse(signalPropagation.source, signalPropagation.signal.pulse);
                outputSignals.forEach(outputSignal -> {
                    q.add(new SignalPropagation(signalPropagation.signal.target, outputSignal));
                });
            }
        }
        return new int[] { low, high, 0 };
    }

    record SignalPropagation(String source, Signal signal) {}

    static void mapConjunctionInputs(Map<String, Module> plan) {
        for (var e : plan.entrySet()) {
            var targets = e.getValue().outputs();
            targets.stream().forEach(t -> {
                var target = plan.get(t);
                if (target instanceof Conjunction conj) {
                    conj.addInput(e.getKey());
                }
            });
        }
    }

    static void reset(Map<String, Module> plan) {
        // System.err.println("before reset: " + plan);
        plan.values().forEach(Module::reset);
        // System.err.println("after reset: " + plan);
    }

    enum Pulse { LOW, HIGH }
    record Signal(Pulse pulse, String target) {}
    interface Module {
        List<Signal> pulse(String source, Pulse pulse);
        List<String> outputs();
        void reset();

        static void insert(Map<String, Module> plan, String line) {
            String[] parts = line.split(" -> ");
            String name = parts[0];
            String[] targets = parts[1].split(", ");
            List<String> targetList = Arrays.asList(targets);

            String ch = name.substring(1);
            if (name.charAt(0) == '%') {
                plan.put(ch, new FlipFlop(targetList));
            } else if (name.charAt(0) == '&') {
                plan.put(ch, new Conjunction(targetList));
            } else {
                plan.put(name, new Broadcaster(targetList));
            }
        }
    }
    record Broadcaster(List<String> outputs) implements Module {
        @Override
        public List<Signal> pulse(String source, Pulse pulse) {
            return outputs.stream().map(o -> new Signal(pulse, o)).toList();
        }

        @Override
        public void reset() {
        }
    }
    @ToString
    static class Conjunction implements Module {
        List<String> outputs;
        Map<String, Pulse> inputs = new HashMap<>();
        int highPulses = 0;
        Conjunction(List<String> outputs) {
            this.outputs = outputs;
        }
        void addInput(String input) {
            inputs.put(input, Pulse.LOW);
        }
        @Override
        public List<String> outputs() {
            return outputs;
        }
        @Override
        public List<Signal> pulse(String source, Pulse pulse) {
            if (inputs.get(source) != pulse) {
                inputs.put(source, pulse);
                highPulses += pulse == Pulse.HIGH ? 1 : -1;
            }
            return outputs.stream()
                .map(o ->
                    new Signal(highPulses == inputs.size() ? Pulse.LOW : Pulse.HIGH, o))
                .toList();
        }
        @Override
        public void reset() {
            // inputs.entrySet().stream().forEach(e -> e.setValue(Pulse.LOW));
            for (var k : inputs.keySet()) {
                inputs.put(k, Pulse.LOW);
            }
            highPulses = 0;
        }
    }
    @ToString
    static class FlipFlop implements Module {
        boolean on = false;
        List<String> outputs;
        FlipFlop(List<String> outputs) {
            this.outputs = outputs;
        }
        @Override
        public List<String> outputs() {
            return outputs;
        }
        @Override
        public List<Signal> pulse(String source, Pulse pulse) {
            if (pulse == Pulse.HIGH) {
                return List.of();
            }
            on = !on;
            var outPulse = on ? Pulse.HIGH : Pulse.LOW;
            return outputs.stream().map(o -> new Signal(outPulse, o)).toList();
        }
        @Override
        public void reset() {
            on = false;
        }

    }

}
