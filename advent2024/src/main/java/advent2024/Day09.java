package advent2024;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day09 {
    public static void main(String[] args) throws Exception {
        var line = Files.lines(Path.of("input09")).findFirst().get();
        List<FileSystemPart> l = new ArrayList<>();
        boolean isFile = true;
        int id = 0;
        for (char c : line.toCharArray()) {
            if (isFile) {
                l.add(new File(id++, (int) (c - '0')));
            } else {
                l.add(new Space((int) (c - '0')));
            }
            isFile = !isFile;
        }
        part1(new ArrayList<>(l));
        part2(l);
    }

    static void part2(List<FileSystemPart> l) {
        for (int i = l.size() - 1; i >= 0; i--) {
            FileSystemPart part = l.get(i);
            if (part instanceof Space) continue;

            var file = (File) part;
            for (int j = 0; j < i; j++) {
                var candidateSpace = l.get(j);
                if (candidateSpace instanceof File) continue;
                var space = (Space) candidateSpace;

                if (file.length == space.length) {
                    l.set(i, space);
                    l.set(j, file);
                    break;
                }
                if (file.length < space.length) {
                    l.set(i, new Space(file.length));
                    l.set(j, file);
                    int remSpace = space.length - file.length;
                    if (l.get(j + 1) instanceof Space nextSpace) {
                        l.set(j + 1, new Space(remSpace + nextSpace.length));
                    } else {
                        l.add(j + 1, new Space(remSpace));
                    }
                    break;
                }
            }
        }

        long checksum = 0;
        int pos = 0;
        for (var part : l) {
            if (part instanceof Space(int length)) {
                pos += length;
            } else if (part instanceof File(int id, int length)) {
                for (int i = 0; i < length; i++) {
                    checksum += pos * id;
                    pos++;
                }
            }
        }

        System.err.println(checksum);
    }

    static void part1(List<FileSystemPart> l) {
        long checksum = 0;
        int pos = 0;
        int left = 0;
        while (left < l.size()) {
            if (l.getLast() instanceof Space) {
                l.removeLast();
                continue;
            }
            if (l.get(left) instanceof File(int id, int length)) {
                for (int i = 0; i < length; i++) {
                    checksum += pos * id;
                    pos++;
                }
                left++;
            } else if (l.get(left) instanceof Space(int length)) {
                var lastFile = (File) l.removeLast();
                if (lastFile.length == length) {
                    l.set(left, lastFile);
                } else if (lastFile.length > length) {
                    l.set(left, new File(lastFile.id, length));
                    l.add(new File(lastFile.id, lastFile.length - length));
                } else {
                    l.set(left, lastFile);
                    l.add(left + 1, new Space(length - lastFile.length));
                }
            }
        }
        System.err.println(checksum);
    }

    interface FileSystemPart {}
    record File(int id, int length) implements FileSystemPart {}
    record Space(int length) implements FileSystemPart {}
}
