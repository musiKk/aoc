#!/usr/bin/env python

with open('input17') as file:
    lines = [ l.strip() for l in file.readlines() ]

original_cube_coords = {}

for y, line in enumerate(lines):
    for x, c in enumerate(line):
        if c == '#':
            original_cube_coords[(x, y, 0)] = True

(min_x, max_x) = (0, len(lines[0]) - 1)
(min_y, max_y) = (0, len(lines) - 1)
(min_z, max_z) = (0, 0)
(min_w, max_w) = (0, 0)

def should_be_on(x, y, z, coords):
    count = 0
    is_on = coords.get((x, y, z), False)
    for xdiff in [-1, 0, 1]:
        for ydiff in [-1, 0, 1]:
            for zdiff in [-1, 0, 1]:
                if xdiff == ydiff == zdiff == 0:
                    continue
                nx = x + xdiff
                ny = y + ydiff
                nz = z + zdiff
                if coords.get((nx, ny, nz), False):
                    count += 1
                if count > 3:
                    return False
    if is_on and 2 <= count <= 3:
        return True
    elif not is_on and count == 3:
        return True
    else:
        return False

def should_be_on2(x, y, z, w, coords):
    count = 0
    is_on = coords.get((x, y, z, w), False)
    for xdiff in [-1, 0, 1]:
        for ydiff in [-1, 0, 1]:
            for zdiff in [-1, 0, 1]:
                for wdiff in [-1, 0, 1]:
                    if xdiff == ydiff == zdiff == wdiff == 0:
                        continue
                    nx = x + xdiff
                    ny = y + ydiff
                    nz = z + zdiff
                    nw = w + wdiff
                    if coords.get((nx, ny, nz, nw), False):
                        count += 1
                    if count > 3:
                        return False
    if is_on and 2 <= count <= 3:
        return True
    elif not is_on and count == 3:
        return True
    else:
        return False

cube_coords = original_cube_coords

for i in range(0, 6):
    print(f"performing cycle {i+1}")
    min_x -= 1
    min_y -= 1
    min_z -= 1
    max_x += 1
    max_y += 1
    max_z += 1

    new_cube_coords = {}
    for x in range(min_x, max_x + 1):
        for y in range(min_y, max_y + 1):
            for z in range(min_z, max_z + 1):
                if should_be_on(x, y, z, cube_coords):
                    new_cube_coords[(x, y, z)] = True

    cube_coords = new_cube_coords
    print(f" => {len(cube_coords)}")

print(len(cube_coords))

# part 2

cube_coords = { (k[0], k[1], k[2], 0): v for k, v in original_cube_coords.items() }
for i in range(0, 6):
    print(f"performing cycle {i+1}")
    min_x -= 1
    min_y -= 1
    min_z -= 1
    max_x += 1
    max_y += 1
    max_z += 1
    min_w -= 1
    max_w += 1

    new_cube_coords = {}
    for x in range(min_x, max_x + 1):
        for y in range(min_y, max_y + 1):
            for z in range(min_z, max_z + 1):
                for w in range(min_w, max_w + 1):
                    if should_be_on2(x, y, z, w, cube_coords):
                        new_cube_coords[(x, y, z, w)] = True

    cube_coords = new_cube_coords
    print(f" => {len(cube_coords)}")

print(len(cube_coords))
