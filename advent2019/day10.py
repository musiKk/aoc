#!/usr/bin/env python

from math import sqrt, copysign, atan2

input = []
with open("day10.input") as file:
    input = [ line.strip() for line in file.readlines() if line.strip() != '' ]

def sign(x):
    return copysign(1, x)

def is_asteroid_visible_trig(input, x, y, x_cand, y_cand):
    slope = atan2(x - x_cand, y - y_cand)

    cand_dist = sqrt((x - x_cand) * (x - x_cand) + (y - y_cand) * (y - y_cand))

    # print(f"testing {x_cand} {y_cand} from {x} {y} (slope atan2({x - x_cand}, {y - y_cand}) -> {slope}, dist {cand_dist})")

    for y_test in range(0, len(input)):
        for x_test in range(0, len(input[0])):
            # print(f" -> testing {x_test} {y_test}")
            # if x >= x_cand and y >= y_cand and x >= x_test and y >= y_test:
            #     continue
            if input[y_test][x_test] != '#':
                # print(" --> no asteroid")
                continue
            if x_test == x and y_test == y:
                # print(" --> test == source")
                continue
            if x_test == x_cand and y_test == y_cand:
                # print(" --> test == cand")
                continue
            test_slope = atan2(x - x_test, y - y_test)
            # # print(f" -> test slope atan2({x - x_cand}, {y_test - y_cand}) -> {test_slope}")
            if slope != test_slope:
                continue
            # same slope
            test_dist = sqrt((x_test - x) * (x_test - x) + (y_test - y) * (y_test - y))
            if test_dist < cand_dist:
                # print(f" --> invisible due to {x_test} {y_test} at dist {test_dist} with slope {test_slope}")
                return False
    # print(" --> visible")
    return True

def is_asteroid_visible(input, x, y, x_cand, y_cand):
    max_y = len(input) - 1
    max_x = len(input[0]) - 1
    # print(f"checking asteroid at {x_cand} {y_cand} ({x} {y})")
    if x == x_cand:
        # print(' - same col')
        if y > y_cand:
            y_range = range(y_cand + 1, y)
        else:
            y_range = range(y + 1, y_cand)
        # print(f" -- checking rows {list(y_range)}")
        for y_test in y_range:
            if input[y_test][x_cand] == '#':
                # print(" --> blocked")
                return False
        # print(" --> free")
        return True
    if y == y_cand:
        # print(" - same row")
        if x > x_cand:
            x_range = range(x_cand + 1, x)
        else:
            x_range = range(x + 1, x_cand)
        # print(f" -- checking cols {list(x_range)}")
        for x_test in x_range:
            if input[y_cand][x_test] == '#':
                # print(" --> blocked")
                return False
        # print(" --> free")
        return True

    slope = ((x + 1) - (x_cand + 1)) / ((y + 1) - (y_cand + 1))
    if y > y_cand:
        y_range = range(y_cand + 1, y)
    else:
        y_range = range(y + 1, y_cand)
    # print(f" - slope is {slope}, checking row range {list(y_range)}")
    for y_test in y_range:
        x_test = y_test * slope + x_cand - 1
        if x_test > max_x or x_test < 0:
            continue
        # print(f" -- checking row {y_test}, x would be {x_test}")
        if int(x_test) == x_test:
            # print(f" --> checking for blockage at {int(x_test)} {y_test}")
            if input[y_test][int(x_test)] == '#':
                # print(" --> blocked")
                return False
            else:
                # print(" --> free")
                pass
    # print(f" --> free")
    return True

def calc_visible_asteroids(input, x_start, y_start):
    count = 0
    for y in range(0, len(input)):
        for x in range(0, len(input[0])):
            if x == x_start and y == y_start:
                continue
            if input[y][x] != '#':
                continue
            vis = is_asteroid_visible_trig(input, x_start, y_start, x, y)
            if vis:
                count += 1
    return count

max_val = 0
for y in range(0, len(input)):
    for x in range(0, len(input[0])):
        if input[y][x] != '#':
            # not a candidate
            print('.', end='')
            continue
        count = calc_visible_asteroids(input, x, y)
        if count > max_val:
            max_val = count
        print(count, end='\n')
    print("")
print(max_val)

# vis = is_asteroid_visible_trig(input, 4, 2, 4, 0)
# print(vis)
# count = calc_visible_asteroids(input, 4, 2)
# print(count)
