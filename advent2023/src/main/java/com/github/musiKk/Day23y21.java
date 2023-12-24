package com.github.musiKk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import lombok.RequiredArgsConstructor;

public class Day23y21 {

    public static void main(String[] args) throws Exception {
        part1(myPart1(true));
    }

    static void part1(State state) {
        state.dump();
        // var result = it(state);
        var result = rec(state);
        System.err.println(result);
    }

    static Map<Integer, Integer> callsPerLevel = new TreeMap<>();

    static int it(State state) {
        Map<Integer, Integer> costPerState = new HashMap<>();

        Queue<StateCost> q = new PriorityQueue<>(Comparator.comparingInt(StateCost::cost));
        q.add(new StateCost(state, 0));
        while (!q.isEmpty()) {
            StateCost sc = q.remove();
            sc.state.dump();
            System.err.println();
            try { Thread.sleep(10); } catch (Exception e) {}

            if (sc.state.solved()) {
                return sc.cost;
            }

            var moves = calculateMoves(sc.state);
            for (var move : moves) {
                var newState = new State(new HashMap<>(sc.state.state));
                newState.move(move);
                int costIncurred = sc.cost + newState.get(move.to).multiplier() * move.cost;

                if (costIncurred < costPerState.getOrDefault(newState.hashCode(), Integer.MAX_VALUE)) {
                    costPerState.put(newState.hashCode(), costIncurred);
                    q.add(new StateCost(newState, costIncurred));
                }
            }
        }

        return -1;
    }

    record StateCost(State state, int cost) {}

    static Integer rec(State state) {
        // if (!callsPerLevel.containsKey(level)) {
        //     callsPerLevel.put(level, 1);
        // } else {
        //     callsPerLevel.put(level, callsPerLevel.get(level) + 1);
        // }
        // System.err.println(callsPerLevel);
        // try { Thread.sleep(1); } catch (Exception e) {}
        if (state.solved()) {
            return 0;
        }

        Integer cost = null;
        List<Move> moves = calculateMoves(state);
        for (Move move : moves) {
            var moved = state.move(move);
            if (!moved) continue;

            // System.err.println("performed move " + move);
            // if (level == 16) {
            //     state.dump();
            //     System.err.println("at level " + level);
            //     try { Thread.sleep(1); } catch (Exception e) {}
            // }

            Integer subCost = rec(state);
            if (subCost != null) {
                if (cost == null) {
                    cost = subCost + move.cost * state.get(move.to).multiplier();
                } else {
                    cost = Math.min(cost, subCost + move.cost * state.get(move.to).multiplier());
                }
            }

            state.undo(move);
        }

        return cost;
    }

    record Move(Type from, Type to, int cost) {
        static Move of(Type from, Type to, int cost) {
            return new Move(from, to, cost);
        }
    }

    static List<Move> calculateMoves(State state) {
        List<Move> moves = new ArrayList<>();

        for (Pod pod : Pod.values()) {
            if (!state.hasForeignersInHome(pod)) continue;

            for (Type home : HOMES.get(pod)) {
                if (state.occupied(home)) {
                    var destGroups = DestGroups.get(pod, home.slot());
                    for (var destGroup : destGroups) {
                        for (var dest : destGroup.dests) {
                            if (state.occupied(dest.type)) {
                                // check next dest group
                                break;
                            }
                            moves.add(Move.of(home, dest.type, dest.cost));
                        }
                    }
                    // next home spots will be blocked by the one we just calculated moves for
                    break;
                }
            }

        }

        if (state.occupied(Type.REST_L2)) {
            var occupant = state.get(Type.REST_L2);
            if (!state.hasForeignersInHome(occupant)) {
                var slot = state.freeSlot(occupant);
                var homeSlot = HOMES.get(occupant).get(slot);
                switch (occupant) {
                    case A -> {
                        moves.add(Move.of(Type.REST_L2, homeSlot, 2 + slot));
                    }
                    case B -> {
                        if (!state.occupied(Type.REST_M1)) {
                            moves.add(Move.of(Type.REST_L2, homeSlot, 4 + slot));
                        }
                    }
                    case C -> {
                        if (!state.occupied(Type.REST_M1) && !state.occupied(Type.REST_M2)) {
                            moves.add(Move.of(Type.REST_L2, homeSlot, 6 + slot));
                        }
                    }
                    case D -> {
                        if (!state.occupied(Type.REST_M1) && !state.occupied(Type.REST_M2) && !state.occupied(Type.REST_M3)) {
                            moves.add(Move.of(Type.REST_L2, homeSlot, 8 + slot));
                        }
                    }
                }
            }
        } else if (state.occupied(Type.REST_L1)) {
            var occupant = state.get(Type.REST_L1);
            if (!state.hasForeignersInHome(occupant)) {
                var slot = state.freeSlot(occupant);
                var homeSlot = HOMES.get(occupant).get(slot);
                switch (occupant) {
                    case A -> {
                        moves.add(Move.of(Type.REST_L1, homeSlot, 3 + slot));
                    }
                    case B -> {
                        if (!state.occupied(Type.REST_M1)) {
                            moves.add(Move.of(Type.REST_L1, homeSlot, 5 + slot));
                        }
                    }
                    case C -> {
                        if (!state.occupied(Type.REST_M1) && !state.occupied(Type.REST_M2)) {
                            moves.add(Move.of(Type.REST_L1, homeSlot, 7 + slot));
                        }
                    }
                    case D -> {
                        if (!state.occupied(Type.REST_M1) && !state.occupied(Type.REST_M2) && !state.occupied(Type.REST_M3)) {
                            moves.add(Move.of(Type.REST_L1, homeSlot, 9 + slot));
                        }
                    }
                }
            }
        }

        if (state.occupied(Type.REST_R1)) {
            var occupant = state.get(Type.REST_R1);
            if (!state.hasForeignersInHome(occupant)) {
                var slot = state.freeSlot(occupant);
                var homeSlot = HOMES.get(occupant).get(slot);
                switch (occupant) {
                    case D -> {
                        moves.add(Move.of(Type.REST_R1, homeSlot, 2 + slot));
                    }
                    case C -> {
                        if (!state.occupied(Type.REST_M3)) {
                            moves.add(Move.of(Type.REST_R1, homeSlot, 4 + slot));
                        }
                    }
                    case B -> {
                        if (!state.occupied(Type.REST_M3) && !state.occupied(Type.REST_M2)) {
                            moves.add(Move.of(Type.REST_R1, homeSlot, 6 + slot));
                        }
                    }
                    case A -> {
                        if (!state.occupied(Type.REST_M3) && !state.occupied(Type.REST_M2) && !state.occupied(Type.REST_M1)) {
                            moves.add(Move.of(Type.REST_R1, homeSlot, 8 + slot));
                        }
                    }
                }
            }
        } else if (state.occupied(Type.REST_R2)) {
            var occupant = state.get(Type.REST_R2);
            if (!state.hasForeignersInHome(occupant)) {
                var slot = state.freeSlot(occupant);
                var homeSlot = HOMES.get(occupant).get(slot);
                switch (occupant) {
                    case D -> {
                        moves.add(Move.of(Type.REST_R2, homeSlot, 3 + slot));
                    }
                    case C -> {
                        if (!state.occupied(Type.REST_M3)) {
                            moves.add(Move.of(Type.REST_R2, homeSlot, 5 + slot));
                        }
                    }
                    case B -> {
                        if (!state.occupied(Type.REST_M3) && !state.occupied(Type.REST_M2)) {
                            moves.add(Move.of(Type.REST_R2, homeSlot, 7 + slot));
                        }
                    }
                    case A -> {
                        if (!state.occupied(Type.REST_M3) && !state.occupied(Type.REST_M2) && !state.occupied(Type.REST_M1)) {
                            moves.add(Move.of(Type.REST_R2, homeSlot, 9 + slot));
                        }
                    }
                }
            }
        }

        if (state.occupied(Type.REST_M1)) {
            var occupant = state.get(Type.REST_M1);
            if (!state.hasForeignersInHome(occupant)) {
                var slot = state.freeSlot(occupant);
                var homeSlot = HOMES.get(occupant).get(slot);
                switch (occupant) {
                    case A -> {
                        moves.add(Move.of(Type.REST_M1, homeSlot, 2 + slot));
                    }
                    case B -> {
                        moves.add(Move.of(Type.REST_M1, homeSlot, 2 + slot));
                    }
                    case C -> {
                        if (!state.occupied(Type.REST_M2)) {
                            moves.add(Move.of(Type.REST_M1, homeSlot, 4 + slot));
                        }
                    }
                    case D -> {
                        if (!state.occupied(Type.REST_M2) && !state.occupied(Type.REST_M3)) {
                            moves.add(Move.of(Type.REST_M1, homeSlot, 6 + slot));
                        }
                    }
                }
            }
        }
        if (state.occupied(Type.REST_M2)) {
            var occupant = state.get(Type.REST_M2);
            if (!state.hasForeignersInHome(occupant)) {
                var slot = state.freeSlot(occupant);
                var homeSlot = HOMES.get(occupant).get(slot);
                switch (occupant) {
                    case B -> {
                        moves.add(Move.of(Type.REST_M2, homeSlot, 2 + slot));
                    }
                    case C -> {
                        moves.add(Move.of(Type.REST_M2, homeSlot, 2 + slot));
                    }
                    case A -> {
                        if (!state.occupied(Type.REST_M1)) {
                            moves.add(Move.of(Type.REST_M2, homeSlot, 4 + slot));
                        }
                    }
                    case D -> {
                        if (!state.occupied(Type.REST_M3)) {
                            moves.add(Move.of(Type.REST_M2, homeSlot, 4 + slot));
                        }
                    }
                }
            }
        }
        if (state.occupied(Type.REST_M3)) {
            var occupant = state.get(Type.REST_M3);
            if (!state.hasForeignersInHome(occupant)) {
                var slot = state.freeSlot(occupant);
                var homeSlot = HOMES.get(occupant).get(slot);
                switch (occupant) {
                    case D -> {
                        moves.add(Move.of(Type.REST_M3, homeSlot, 2 + slot));
                    }
                    case C -> {
                        moves.add(Move.of(Type.REST_M3, homeSlot, 2 + slot));
                    }
                    case B -> {
                        if (!state.occupied(Type.REST_M2)) {
                            moves.add(Move.of(Type.REST_M3, homeSlot, 4 + slot));
                        }
                    }
                    case A -> {
                        if (!state.occupied(Type.REST_M2) && !state.occupied(Type.REST_M1)) {
                            moves.add(Move.of(Type.REST_M3, homeSlot, 6 + slot));
                        }
                    }
                }
            }
        }

        return moves;
    }

    // static Map<Pod, List<Type>> HOMES = Map.of(
    //     Pod.A, List.of(Type.HOME_A1, Type.HOME_A2),
    //     Pod.B, List.of(Type.HOME_B1, Type.HOME_B2),
    //     Pod.C, List.of(Type.HOME_C1, Type.HOME_C2),
    //     Pod.D, List.of(Type.HOME_D1, Type.HOME_D2));
    static Map<Pod, List<Type>> HOMES = Map.of(
        Pod.A, List.of(Type.HOME_A1, Type.HOME_A2, Type.HOME_A3, Type.HOME_A4),
        Pod.B, List.of(Type.HOME_B1, Type.HOME_B2, Type.HOME_B3, Type.HOME_B4),
        Pod.C, List.of(Type.HOME_C1, Type.HOME_C2, Type.HOME_C3, Type.HOME_C4),
        Pod.D, List.of(Type.HOME_D1, Type.HOME_D2, Type.HOME_D3, Type.HOME_D4));

    @RequiredArgsConstructor
    static class State {
        final Map<Type, Pod> state;
        final Set<Integer> seen = new HashSet<>();

        int freeSlot(Pod pod) {
            var homes = HOMES.get(pod);
            int i = 0;
            for (; i < homes.size() && state.get(homes.get(i)) != pod; i++) {
            }
            return i - 1;
        }

        Pod get(Type type) {
            return state.get(type);
        }

        void undo(Move move) {
            // System.err.println("undid " + move);
            state.put(move.from, state.remove(move.to));
        }

        boolean move(Move move) {
            // System.err.println("attempting " + move);
            var from = move.from;
            var to = move.to;
            if (!state.containsKey(from)) throw new RuntimeException(from + " is not occupied");
            if (state.containsKey(to)) throw new RuntimeException(to + " already occupied");

            state.put(to, state.remove(from));
            // if (seen.contains(state.hashCode())) {
            //     // System.err.println("already seen");
            //     state.put(from, state.remove(to));
            //     return false;
            // }
            // seen.add(state.hashCode());
            // System.err.println("did " + move + " (" + seen + ")");
            return true;
        }

        boolean occupied(Type type) {
            return state.containsKey(type);
        }

        boolean hasForeignersInHome(Pod pod) {
            var homes = HOMES.get(pod);
            for (var home : homes) {
                var occupant = state.get(home);
                if (occupant != null && occupant != pod) {
                    return true;
                }
            }
            return false;
        }

        boolean solved() {
            // return
            //     state.get(Type.HOME_A1) == Pod.A && state.get(Type.HOME_A2) == Pod.A &&
            //     state.get(Type.HOME_B1) == Pod.B && state.get(Type.HOME_B2) == Pod.B &&
            //     state.get(Type.HOME_C1) == Pod.C && state.get(Type.HOME_C2) == Pod.C &&
            //     state.get(Type.HOME_D1) == Pod.D && state.get(Type.HOME_D2) == Pod.D;
            return
                state.get(Type.HOME_A1) == Pod.A && state.get(Type.HOME_A2) == Pod.A &&
                state.get(Type.HOME_B1) == Pod.B && state.get(Type.HOME_B2) == Pod.B &&
                state.get(Type.HOME_C1) == Pod.C && state.get(Type.HOME_C2) == Pod.C &&
                state.get(Type.HOME_D1) == Pod.D && state.get(Type.HOME_D2) == Pod.D &&
                state.get(Type.HOME_A3) == Pod.A && state.get(Type.HOME_A4) == Pod.A &&
                state.get(Type.HOME_B3) == Pod.B && state.get(Type.HOME_B4) == Pod.B &&
                state.get(Type.HOME_C3) == Pod.C && state.get(Type.HOME_C4) == Pod.C &&
                state.get(Type.HOME_D3) == Pod.D && state.get(Type.HOME_D4) == Pod.D;
        }

        static State makeState(String l1, String l2) {
            Map<Type, Pod> state = new HashMap<>();
            state.put(Type.HOME_A1, Pod.valueOf(String.valueOf(l1.charAt(0))));
            state.put(Type.HOME_B1, Pod.valueOf(String.valueOf(l1.charAt(1))));
            state.put(Type.HOME_C1, Pod.valueOf(String.valueOf(l1.charAt(2))));
            state.put(Type.HOME_D1, Pod.valueOf(String.valueOf(l1.charAt(3))));

            state.put(Type.HOME_A2, Pod.valueOf(String.valueOf(l2.charAt(0))));
            state.put(Type.HOME_B2, Pod.valueOf(String.valueOf(l2.charAt(1))));
            state.put(Type.HOME_C2, Pod.valueOf(String.valueOf(l2.charAt(2))));
            state.put(Type.HOME_D2, Pod.valueOf(String.valueOf(l2.charAt(3))));

            return new State(state);
        }

        static State makePart2State(String l1, String l2) {
            Map<Type, Pod> state = new HashMap<>();
            state.put(Type.HOME_A1, Pod.valueOf(String.valueOf(l1.charAt(0))));
            state.put(Type.HOME_B1, Pod.valueOf(String.valueOf(l1.charAt(1))));
            state.put(Type.HOME_C1, Pod.valueOf(String.valueOf(l1.charAt(2))));
            state.put(Type.HOME_D1, Pod.valueOf(String.valueOf(l1.charAt(3))));

            state.put(Type.HOME_A2, Pod.D);
            state.put(Type.HOME_B2, Pod.C);
            state.put(Type.HOME_C2, Pod.B);
            state.put(Type.HOME_D2, Pod.A);
            state.put(Type.HOME_A3, Pod.D);
            state.put(Type.HOME_B3, Pod.B);
            state.put(Type.HOME_C3, Pod.A);
            state.put(Type.HOME_D3, Pod.C);

            state.put(Type.HOME_A4, Pod.valueOf(String.valueOf(l2.charAt(0))));
            state.put(Type.HOME_B4, Pod.valueOf(String.valueOf(l2.charAt(1))));
            state.put(Type.HOME_C4, Pod.valueOf(String.valueOf(l2.charAt(2))));
            state.put(Type.HOME_D4, Pod.valueOf(String.valueOf(l2.charAt(3))));

            return new State(state);

        }

        void dump() {
            System.err.println("#############");
            System.err.printf("#%s%s.%s.%s.%s.%s%s#%n",
                state.get(Type.REST_L1) != null ? state.get(Type.REST_L1) : ".",
                state.get(Type.REST_L2) != null ? state.get(Type.REST_L2) : ".",
                state.get(Type.REST_M1) != null ? state.get(Type.REST_M1) : ".",
                state.get(Type.REST_M2) != null ? state.get(Type.REST_M2) : ".",
                state.get(Type.REST_M3) != null ? state.get(Type.REST_M3) : ".",
                state.get(Type.REST_R1) != null ? state.get(Type.REST_R1) : ".",
                state.get(Type.REST_R2) != null ? state.get(Type.REST_R2) : "."
            );
            System.err.printf("###%s#%s#%s#%s###%n",
                state.get(Type.HOME_A1) != null ? state.get(Type.HOME_A1) : ".",
                state.get(Type.HOME_B1) != null ? state.get(Type.HOME_B1) : ".",
                state.get(Type.HOME_C1) != null ? state.get(Type.HOME_C1) : ".",
                state.get(Type.HOME_D1) != null ? state.get(Type.HOME_D1) : "."
            );
            System.err.printf("  #%s#%s#%s#%s#%n",
                state.get(Type.HOME_A2) != null ? state.get(Type.HOME_A2) : ".",
                state.get(Type.HOME_B2) != null ? state.get(Type.HOME_B2) : ".",
                state.get(Type.HOME_C2) != null ? state.get(Type.HOME_C2) : ".",
                state.get(Type.HOME_D2) != null ? state.get(Type.HOME_D2) : "."
            );
            System.err.println("  #########");
        }
    }

    static class DestGroups {
        static Map<Pod, Map<Integer, List<DestGroup>>> PRECOMPUTED = new HashMap<>();

        static {
            for (Pod pod : Pod.values()) {
                // for (int i = 0; i < 2; i++) {
                for (int i = 0; i < 4; i++) {
                    PRECOMPUTED.computeIfAbsent(pod, __ -> new HashMap<>()).put(i, make(pod, i));
                }
            }
        }

        static List<DestGroup> get(Pod pod, int slot) {
            return PRECOMPUTED.get(pod).get(slot);
        }

        static List<DestGroup> make(Pod pod, int slot) {
            return switch(pod) {
                case A -> List.of(
                    // left
                    DestGroup.of(
                        Dest.of(Type.REST_L2, 2 + slot),
                        Dest.of(Type.REST_L1, 3 + slot)),
                    // right
                    DestGroup.of(
                        Dest.of(Type.REST_M1, 2 + slot),
                        Dest.of(Type.REST_M2, 4 + slot),
                        Dest.of(Type.REST_M3, 6 + slot),
                        Dest.of(Type.REST_R1, 8 + slot),
                        Dest.of(Type.REST_R2, 9 + slot)));
                case B -> List.of(
                    // left
                    DestGroup.of(
                        Dest.of(Type.REST_M1, 2 + slot),
                        Dest.of(Type.REST_L2, 4 + slot),
                        Dest.of(Type.REST_L1, 5 + slot)),
                    // right
                    DestGroup.of(
                        Dest.of(Type.REST_M2, 2 + slot),
                        Dest.of(Type.REST_M3, 4 + slot),
                        Dest.of(Type.REST_R1, 6 + slot),
                        Dest.of(Type.REST_R2, 7 + slot)
                    )
                );
                case C -> List.of(
                    // left
                    DestGroup.of(
                        Dest.of(Type.REST_M2, 2 + slot),
                        Dest.of(Type.REST_M1, 4 + slot),
                        Dest.of(Type.REST_L2, 6 + slot),
                        Dest.of(Type.REST_L1, 7 + slot)
                    ),
                    //right
                    DestGroup.of(
                        Dest.of(Type.REST_M3, 2 + slot),
                        Dest.of(Type.REST_R1, 4 + slot),
                        Dest.of(Type.REST_R2, 5 + slot)
                    )
                );
                case D -> List.of(
                    DestGroup.of(
                        Dest.of(Type.REST_M3, 2 + slot),
                        Dest.of(Type.REST_M2, 4 + slot),
                        Dest.of(Type.REST_M1, 6 + slot),
                        Dest.of(Type.REST_L2, 8 + slot),
                        Dest.of(Type.REST_L1, 9 + slot)
                        ),
                    DestGroup.of(
                        Dest.of(Type.REST_R1, 2 + slot),
                        Dest.of(Type.REST_R2, 3 + slot)
                    )
                );
            };
        }
    }

    record DestGroup(List<Dest> dests) {
        static DestGroup of(Dest... dests) {
            return new DestGroup(Arrays.asList(dests));
        }
    }

    static State examplePart1(boolean part2) {
        if (part2) return State.makePart2State("BCBD", "ADCA");
        return State.makeState("BCBD", "ADCA");
    }

    static State myPart1(boolean part2) {
        if (part2) return State.makePart2State("CBAD", "BCDA");
        return State.makeState("CBAD", "BCDA");
    }

    enum Pod {
        A, B, C, D;
        int multiplier() {
            return switch (this) {
                case A -> 1;
                case B -> 10;
                case C -> 100;
                case D -> 1000;
            };
        }
    }

    record Dest(Type type, int cost) {
        static Dest of(Type type, int cost) {
            return new Dest(type, cost);
        }
    }

    enum Type {
        HOME_A1, HOME_A2, HOME_A3, HOME_A4,
        HOME_B1, HOME_B2, HOME_B3, HOME_B4,
        HOME_C1, HOME_C2, HOME_C3, HOME_C4,
        HOME_D1, HOME_D2, HOME_D3, HOME_D4,

        REST_L1, REST_L2,

        REST_M1, REST_M2, REST_M3,

        REST_R1, REST_R2;
        int slot() {
            return switch (this) {
                case HOME_A1, HOME_B1, HOME_C1, HOME_D1 -> 0;
                case HOME_A2, HOME_B2, HOME_C2, HOME_D2 -> 1;
                case HOME_A3, HOME_B3, HOME_C3, HOME_D3 -> 2;
                case HOME_A4, HOME_B4, HOME_C4, HOME_D4 -> 3;
                default -> throw new RuntimeException();
            };
        }
    }

}
