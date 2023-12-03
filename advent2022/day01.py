#!/usr/bin/env python

import pprint

elves = []
current_elf = []

with open("day01.input") as f:
    for line in f:
        line = line.strip()
        if (line == ""):
            elves.append(current_elf)
            current_elf = []
        else:
            current_elf.append(int(line))

sums = []

for elf in elves:
    sums.append(sum(elf))

sums.sort()

print(sums[-1])
print(sum(sums[-3:]))
