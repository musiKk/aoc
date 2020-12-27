#!/usr/bin/env python

input_string = "389125467"
# input_string = "327465189"

cups = [int(c) for c in input_string]

def remove_cups(cups, cup_before):
    cup_index = 0
    for i, c in enumerate(cups):
        if c == cup_before:
            cup_index = i + 1

    removed_cups = []
    for i in range(0, 3):
        if cup_index >= len(cups):
            cup_index = 0
        removed_cups.append(cups.pop(cup_index))

    return removed_cups

def find_destination_cup(cups, current_cup):
    destination_cup_candidate = current_cup - 1
    while True:
        if destination_cup_candidate < 1:
            destination_cup_candidate = 9
        if destination_cup_candidate in cups:
            return destination_cup_candidate
        destination_cup_candidate -= 1

def insert_removed_cups(cups, removed_cups, destination_cup):
    new_cups = []
    for c in cups:
        new_cups.append(c)
        if c == destination_cup:
            new_cups.extend(removed_cups)
    return new_cups

def print_cups(cups, current_cup):
    cup_strings = []
    for c in cups:
        if c == current_cup:
            cup_strings.append(f"({c})")
        else:
            cup_strings.append(str(c))
    return " ".join(cup_strings)

def get_next_current_cup(cups, current_cup):
    index = -1
    for i, c in enumerate(cups):
        if c == current_cup:
            index = i
    return cups[(index+1) % 9]

current_cup = cups[0]
for i in range(0, 100):
    print(f"-- move {i+1} --")
    print(f"cups: {print_cups(cups, current_cup)}")
    removed_cups = remove_cups(cups, current_cup)
    print(f"pick up: {', '.join(str(c) for c in removed_cups)}")
    destination_cup = find_destination_cup(cups, current_cup)
    print(f"destination: {destination_cup}\n")
    cups = insert_removed_cups(cups, removed_cups, destination_cup)
    current_cup = get_next_current_cup(cups, current_cup)

print(cups)

# cups = [int(c) for c in input_string]
# for i in range(9, 1000000-9+1):
#     cups.append(i)

# for i in range(0, 100):
#     print(f"-- move {i+1} --")
#     # print(f"cups: {print_cups(cups, current_cup)}")
#     removed_cups = remove_cups(cups, current_cup)
#     # print(f"pick up: {', '.join(str(c) for c in removed_cups)}")
#     destination_cup = find_destination_cup(cups, current_cup)
#     # print(f"destination: {destination_cup}\n")
#     cups = insert_removed_cups(cups, removed_cups, destination_cup)
#     current_cup = get_next_current_cup(cups, current_cup)

# for i, cup in cups:
#     if cup == 1:
#         print(cups[(i + 1)%1000000])
#         print(cups[(i + 2)%1000000])
#         break
