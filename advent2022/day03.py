#!/usr/bin/env python

with open("day03.input") as f:
    score = 0
    score2 = 0
    elves = 0
    elf_set = None
    for line in f:
        line = line.strip()
        if line == "":
            break

        l = len(line)
        lh = l // 2

        fst = set(line[:lh])
        snd = set(line[lh:])

        common = fst.intersection(snd).pop()

        if ord('a') <= ord(common) <= ord('z'):
            score += ord(common) - ord('a') + 1
        else:
            score += ord(common) - ord('A') + 1 + 26

        # part 2
        if elves == 0:
            if elf_set is not None:
                common = elf_set.pop()
                if ord('a') <= ord(common) <= ord('z'):
                    score2 += ord(common) - ord('a') + 1
                else:
                    score2 += ord(common) - ord('A') + 1 + 26
            elves = 1
            elf_set = set(line)
        else:
            elves += 1
            elves %= 3
            elf_set.intersection_update(line)

    common = elf_set.pop()
    if ord('a') <= ord(common) <= ord('z'):
        score2 += ord(common) - ord('a') + 1
    else:
        score2 += ord(common) - ord('A') + 1 + 26

    print(score)
    print(score2)
