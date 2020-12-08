#!/usr/bin/env python

lines = []
with open('input03') as file:
    lines = [line.strip() for line in file.readlines() if line != ""]

print(lines)

def traverse(slope_x, slope_y):
    (max_x, max_y) = (len(lines[0]) - 1, len(lines) - 1)
    (cur_x, cur_y) = (0, 0)

    print(f"{max_x}, {max_y}")
    print()

    trees = 0
    while cur_y <= max_y:
        spot = lines[cur_y][cur_x]
        print(f"{cur_x}, {cur_y} => {spot}")
        if spot == '#':
            # print(" -> tree")
            trees += 1
        cur_x += slope_x
        if cur_x >= max_x:
            print(f" -> adjusting x from {cur_x} to {cur_x - max_x - 1}")
            cur_x -= (max_x + 1)
        cur_y += slope_y

    print(trees)
    return trees

results = [
    traverse(sx, sy) for (sx, sy) in [ (1, 1), (3, 1), (5, 1), (7, 1), (1, 2) ]
]
print(results) # first guess: [53, 282, 54, 54, 66]
prod = 1
for x in results:
    prod *= x
print(prod)
