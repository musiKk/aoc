#!/usr/bin/env python

tile_paths = []
with open('input24') as file:
    for line in file.readlines():
        line = line.strip()
        if line == "":
            break
        tile_path = []
        while True:
            if line == "":
                break
            for direction_candidate in [ "se", "sw", "nw", "ne", "e", "w" ]:
                if line.startswith(direction_candidate):
                    tile_path.append(direction_candidate)
                    line = line[len(direction_candidate):]
                    break
        tile_paths.append(tile_path)

direction_changes = {
    "se": ( 1, -1),
    "sw": ( 0, -1),
    "nw": (-1,  1),
    "ne": ( 0,  1),
    "e":  ( 1,  0),
    "w":  (-1,  0)
}

black_tiles = {}
for tile_path in tile_paths:
    x, y = 0, 0
    for direction in tile_path:
        x_off, y_off = direction_changes[direction]
        x += x_off
        y += y_off
    if (x, y) in black_tiles:
        del black_tiles[(x, y)]
    else:
        black_tiles[(x, y)] = True

# print(black_tiles)
print(len(black_tiles))

min_x, max_x = 0, 0
min_y, max_y = 0, 0
for x, y in black_tiles.keys():
    if x < min_x:
        min_x = x
    if x > max_x:
        max_x = x
    if y < min_y:
        min_y = y
    if y > max_y:
        max_y = y

new_tiles = {}
for day in range(0, 100):
    min_x -= 1
    max_x += 1
    min_y -= 1
    max_y += 1
    for x in range(min_x, max_x + 1):
        for y in range(min_y, max_y + 1):
            is_black = (x, y) in black_tiles
            black_neighbors = 0
            for x_off, y_off in direction_changes.values():
                x_neighbor, y_neighbor = x + x_off, y + y_off
                if (x_neighbor, y_neighbor) in black_tiles:
                    black_neighbors += 1
            if is_black and (black_neighbors == 0 or black_neighbors > 2):
                pass
            elif not is_black and black_neighbors == 2:
                new_tiles[(x, y)] = True
            else:
                if is_black:
                    new_tiles[(x, y)] = True

    print(f"Day {day + 1}: {len(new_tiles)}")
    black_tiles = new_tiles
    new_tiles = {}

