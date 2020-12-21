#!/usr/bin/env python

from math import sqrt

tiles = {}

with open('input20') as file:
    current_tile_num = None
    current_tile = []
    for line in file.readlines():
        line = line.strip()
        if line.startswith("Tile"):
            new_tile_num = int(line[5: -1])
            if current_tile_num is not None:
                tiles[current_tile_num] = current_tile
                current_tile = []
            current_tile_num = new_tile_num
        elif line != "":
            current_tile.append(line)
    tiles[current_tile_num] = current_tile

def get_sides(tile):
    top_side = tile[0]
    bottom_side = tile[-1]
    left_side_list = [ l[0] for l in reversed(tile) ]
    right_side_list = [ l[-1] for l in tile ]

    sides = [
        "".join(left_side_list),
        top_side,
        "".join(right_side_list),
        "".join(reversed(bottom_side))
    ]
    reverse_sides = [ s[::-1] for s in sides ]
    return sides + reverse_sides

tile_sides = { id: get_sides(tile) for id, tile in tiles.items() }
tile_matches = {}

sides_matches_count = {}
for id1, tile_sides1 in tile_sides.items():
    for id2, tile_sides2 in tile_sides.items():
        if id1 == id2:
            continue
        for side1 in tile_sides1:
            for side2 in tile_sides2:
                if side1 == side2:
                    tile_matches[id1] = tile_matches.get(id1, 0) + 1
                    sides_matches_count[side1] = sides_matches_count.get(side1, 0) + 1

# part1

prod = 1
for k, v in tile_matches.items():
    if v == 4:
        print(f"tile {k} is a corner tile")
        prod *= k

print(prod)

# part 2

def dump_tile(tile):
    print("\n".join(tile))

def rotate_tile_right(tile, count=1):
    final_tile = tile
    for i in range(0, count):
        new_tile = []
        for col in range(0, len(tile)):
            new_line = []
            for line in reversed(final_tile):
                new_line.append(line[col])
            new_tile.append("".join(new_line))
        final_tile = new_tile
    return final_tile

def flip_tile(tile):
    return [ s[::-1] for s in tile ]

def top_left_rotated_correctly(tile):
    # print("checking orientation of tile")
    # dump_tile(tile)
    sides = get_sides(tile)
    # print(f"sides are {[sides_matches_count.get(s) for s in sides[0:4]]}")
    if sides[0] in sides_matches_count or sides[1] in sides_matches_count:
        return False
    return True

def construct_row(row):
    # row has leftmost filled correctly
    for x in range(1, image_side_length_in_tiles):
        # print(f"accessing {x-1} of {row}")
        left_tile = row[x-1]
        right_side = get_sides(left_tile)[2]

        added_candidate = 0
        for id, tile_candidate in tiles.items():
            # print(f"checking sides of candidate tile {id} to match {right_side}")
            # print(f" => {get_sides(tile_candidate)}")
            candidate_sides = get_sides(tile_candidate)
            candidate_side_index = -1
            for i, candidate_side in enumerate(candidate_sides):
                if right_side == candidate_side:
                    # print(f" => {right_side} == {candidate_side} ")
                    candidate_side_index = i
                else:
                    # print(f" => {right_side} != {candidate_side} ")
                    pass
            if candidate_side_index == -1:
                continue

            # print(f" found candidate {id}, need to rotate")
            # print(f"candiate looks like this")
            # dump_tile(tile_candidate)
            # print(f"{right_side} in {candidate_sides} is at {candidate_side_index}")
            # print(f"--------------------")

            # determine how to rotate new tile candidate
            must_flip = False
            rotate_count = 0
            # print(f"side index: {candidate_side_index}")
            if candidate_side_index == 0:
                must_flip = True
                rotate_count = 2
            elif candidate_side_index == 1:
                must_flip = True
                rotate_count = 3
            elif candidate_side_index == 2:
                must_flip = True
                rotate_count = 0
            elif candidate_side_index == 3:
                must_flip = True
                rotate_count = 1
            elif candidate_side_index == 4:
                pass
            elif candidate_side_index == 5:
                rotate_count = 3
            elif candidate_side_index == 6:
                rotate_count = 2
            elif candidate_side_index == 7:
                rotate_count = 1

            correctly_orientated_candidate = tile_candidate
            if must_flip:
                correctly_orientated_candidate = flip_tile(correctly_orientated_candidate)
            correctly_orientated_candidate = rotate_tile_right(correctly_orientated_candidate, rotate_count)

            row.append(correctly_orientated_candidate)
            added_candidate = id
        del tiles[added_candidate]

def get_below(tile):
    bottom_side = get_sides(tile)[3]
    added_candidate = 0
    for id, tile_candidate in tiles.items():
        # print(f"checking sides of candidate tile {id} to match {right_side}")
        # print(f" => {get_sides(tile_candidate)}")
        candidate_sides = get_sides(tile_candidate)
        candidate_side_index = -1
        for i, candidate_side in enumerate(candidate_sides):
            if bottom_side == candidate_side:
                # print(f" => {right_side} == {candidate_side} ")
                candidate_side_index = i
            else:
                # print(f" => {right_side} != {candidate_side} ")
                pass
        if candidate_side_index == -1:
            continue

        # print(f" found candidate {id}, need to rotate")
        # print(f"candiate looks like this")
        # dump_tile(tile_candidate)
        # print(f"{right_side} in {candidate_sides} is at {candidate_side_index}")
        # print(f"--------------------")

        # determine how to rotate new tile candidate
        must_flip = False
        rotate_count = 0
        # print(f"side index: {candidate_side_index}")
        if candidate_side_index == 0:
            must_flip = True
            rotate_count = 3
        elif candidate_side_index == 1:
            must_flip = True
            rotate_count = 0
        elif candidate_side_index == 2:
            must_flip = True
            rotate_count = 1
        elif candidate_side_index == 3:
            must_flip = True
            rotate_count = 2
        elif candidate_side_index == 4:
            rotate_count = 1
        elif candidate_side_index == 5:
            rotate_count = 0
        elif candidate_side_index == 6:
            rotate_count = 3
        elif candidate_side_index == 7:
            rotate_count = 2

        correctly_orientated_candidate = tile_candidate
        if must_flip:
            correctly_orientated_candidate = flip_tile(correctly_orientated_candidate)
        correctly_orientated_candidate = rotate_tile_right(correctly_orientated_candidate, rotate_count)

        added_candidate = id
        del tiles[added_candidate]

        return correctly_orientated_candidate
    raise Exception("found nothing below")

image_side_length_in_tiles = int(sqrt(len(tiles)))
# image_tiles = [[]*image_side_length_in_tiles]*image_side_length_in_tiles

top_left = None
for k, v in tile_matches.items():
    if v == 4:
        top_left = tiles.pop(k)
        break

if not top_left:
    raise Exception()

while not top_left_rotated_correctly(top_left):
    top_left = rotate_tile_right(top_left)

rows = []
for y in range(1, image_side_length_in_tiles + 1):
    row = [top_left]
    construct_row(row)
    rows.append(row)

    if not y == image_side_length_in_tiles:
        top_left = get_below(top_left)

tile_width = len(rows[0][0])
adjusted_tile_width = tile_width - 2
reconstructed_image = [""] * (adjusted_tile_width * image_side_length_in_tiles)

for tile_row_index, tile_row in enumerate(rows):
    for tile_col_index, tile in enumerate(tile_row):
        for tile_line_index, tile_line in enumerate(tile[1:-1]):
            reconstructed_image_line = tile_row_index * adjusted_tile_width + tile_line_index
            reconstructed_image[reconstructed_image_line] += tile_line[1:-1]

# reconstructed_image = flip_tile(reconstructed_image)
# reconstructed_image = rotate_tile_right(reconstructed_image, 3)
dump_tile(reconstructed_image)

import re
def find_monster(image):
    monster_pattern1 = "..................#."
    monster_pattern2 = "#....##....##....###"
    monster_pattern3 = ".#..#..#..#..#..#..."

    monsters_found = 0

    for y in range(0, len(image) - 3):
        line = image[y]
        for x in range(0, len(line) - len(monster_pattern1)):
            part1 = line[x:x+len(monster_pattern1)]
            part2 = image[y+1][x:x+len(monster_pattern1)]
            part3 = image[y+2][x:x+len(monster_pattern1)]

            if re.match(monster_pattern1, part1)         \
                and re.match(monster_pattern2, part2)    \
                and re.match(monster_pattern3, part3):
                monsters_found += 1

    return monsters_found


for must_flip in [True, False]:
    for r in range(0, 4):
        test_image = reconstructed_image
        if must_flip:
            test_image = flip_tile(reconstructed_image)
        test_image = rotate_tile_right(test_image, r)
        found_monsters = find_monster(test_image)
        if found_monsters > 0:
            all_hash_chars = 0
            for l in test_image:
                all_hash_chars += len(re.findall("#", l))
            non_monster_chars = all_hash_chars - found_monsters * 15
            print(non_monster_chars)
