#!/usr/bin/env python

import re

lines = []
with open('input02') as file:
    for line in file.readlines():
        lines.append(line.strip())

valid_pws_1 = 0
valid_pws_2 = 0

for line in lines:
    m = re.match(r"(\d+)-(\d+) (.): (\w+)", line)
    (min, max, letter, pw) = (int(m.group(1)), int(m.group(2)), m.group(3), m.group(4))

    count = 0
    for c in pw:
        if c == letter:
            count += 1
    if min <= count <= max:
        valid_pws_1 += 1

    (c1match, c2match) = ((int(pw[min-1] == letter), (int(pw[max-1] == letter))))
    if (c1match + c2match) == 1:
        # print(f"{pw} -> {min}-{max} {letter}: {c1match} {c2match}")
        valid_pws_2 += 2
    else:
        print(f"{pw} -> {min}-{max} {letter}: {c1match} {c2match}")



print(valid_pws_1)
print(valid_pws_2)
