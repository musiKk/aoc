package com.github.musiKk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Day15 {

    public static void main(String[] args) throws Exception {
        Path p = Path.of("input15.txt");
        String[] input;
        try (var s = Files.lines(p)) {
            input = s.findFirst().get().split(",");
        }

        part1(input);
        part2(input);
    }

    static void part2(String[] input) {
        Box[] boxes = new Box[256];
        for (int i = 0; i < 256; i++) boxes[i] = Box.empty();

        Arrays.stream(input)
                .map(Operation::parse)
                .forEach(o -> updateBoxes(boxes, o));

        int result = 0;
        for (int boxIdx = 0; boxIdx < boxes.length; boxIdx++) {
            Box box = boxes[boxIdx];
            System.err.println(box);
            for (int lensIdx = 0; lensIdx < box.lenses.size(); lensIdx++) {
                Lens lens = box.lenses.get(lensIdx);
                result += (boxIdx + 1) * (lensIdx + 1) * lens.focalLength;
            }
        }
        System.err.println(result);
    }

    static void updateBoxes(Box[] boxes, Operation o) {
        int boxIdx = o.box();
        Box box = boxes[boxIdx];
        String label = o.label();
        System.err.println(o);
        System.err.println("box " + boxIdx + " before: " + box);
        if (o instanceof AddOperation addOp) {
            boolean updated = false;
            for (int lIdx = 0; lIdx < box.lenses.size(); lIdx++) {
                Lens lens = box.lenses.get(lIdx);
                if (lens.label.equals(label)) {
                    box.lenses.set(lIdx, new Lens(label, addOp.focalLength));
                    updated = true;
                    break;
                }
            }
            if (!updated)
            box.lenses.add(new Lens(label, addOp.focalLength));
        } else if (o instanceof RemoveOperation rmOp) {
            Iterator<Lens> lIt = box.lenses.iterator();
            while (lIt.hasNext()) {
                Lens lens = lIt.next();
                if (lens.label.equals(label)) {
                    lIt.remove();
                    break;
                }
            }
        }
        System.err.println("box " + boxIdx + " after:  " + box);
        System.err.println();
    }

    interface Operation {
        String label();
        int box();
        static Operation parse(String input) {
            if (input.endsWith("-")) {
                String label = input.substring(0, input.length() - 1);
                return new RemoveOperation(label, hash(label));
            } else {
                String[] parts = input.split("=");
                String label = parts[0];
                int focalLength = Integer.parseInt(parts[1]);
                return new AddOperation(label, hash(label), focalLength);
            }
        }
    }
    record RemoveOperation(String label, int box) implements Operation {}
    record AddOperation(String label, int box, int focalLength) implements Operation {}
    record Box(List<Lens> lenses) {
        static Box empty() {
            return new Box(new ArrayList<>());
        }
    }
    record Lens(String label, int focalLength) {}

    static void part1(String[] input) {
        int result = Arrays.stream(input)
        .map(Day15::hash)
        .mapToInt(Integer::valueOf)
        .sum();
        System.err.println(result);
    }

    static int hash(String string) {
        int hash = 0;
        for (char ch : string.toCharArray()) {
            hash += ch;
            hash *= 17;
            hash %= 256;
        }
        return hash;
    }

}
