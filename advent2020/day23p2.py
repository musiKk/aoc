#!/usr/bin/env python

from __future__ import annotations
from dataclasses import dataclass

# input_string = "389125467"
input_string = "327465189"

cup_list = [int(c) for c in input_string]
for i in range(10, 1000000 + 1):
    cup_list.append(i)
cup_count = len(cup_list)

@dataclass
class Node:
    value: int
    next: Node

cups = []
for i in range(0, cup_count):
    cups.append(Node(i + 1, None))

first_cup = cups[cup_list[0] - 1]
last_node = first_cup
for c in cup_list[1:]:
    new_node = cups[c-1]
    last_node.next = new_node
    last_node = new_node
last_node.next = first_cup

def remove_cups(cup_before):
    removed_cups = cup_before.next
    cup_before.next = cup_before.next.next.next.next
    return removed_cups

def find_destination_cup(current_cup, removed_cups):
    removed_cup_values = [
        removed_cups.value,
        removed_cups.next.value,
        removed_cups.next.next.value
    ]

    destination_cup_candidate_value = current_cup.value - 1
    if destination_cup_candidate_value < 1:
            destination_cup_candidate_value = cup_count
    while destination_cup_candidate_value in removed_cup_values:
        destination_cup_candidate_value -= 1
        if destination_cup_candidate_value < 1:
            destination_cup_candidate_value = cup_count

    if destination_cup_candidate_value < 1:
        destination_cup_candidate_value = cup_count

    return cups[(destination_cup_candidate_value-1)%cup_count]

def insert_removed_cups(removed_cups, destination_cup):
    current_next = destination_cup.next
    destination_cup.next = removed_cups
    removed_cups.next.next.next = current_next

def print_cups(current_cup):
    cup_strings = []
    cup = current_cup
    first = True
    while cup != current_cup or first:
        first = False
        if cup == current_cup:
            cup_strings.append(f"({cup.value})")
        else:
            cup_strings.append(str(cup.value))
        cup = cup.next
    return " ".join(cup_strings)

current_cup = first_cup
for i in range(0, 10000000):
    print(f"-- move {i+1} --")
    # print(f"cups: {print_cups(current_cup)}")
    removed_cups = remove_cups(current_cup)
    # print(f"pick up: {removed_cups.value}, {removed_cups.next.value}, {removed_cups.next.next.value}")
    destination_cup = find_destination_cup(current_cup, removed_cups)
    # print(f"destination: {destination_cup.value}\n")
    insert_removed_cups(removed_cups, destination_cup)
    current_cup = current_cup.next

# print(print_cups(current_cup))

c = first_cup
while True:
    if c.value == 1:
        print(f"{c.next.value}, {c.next.next.value}")
        break
    c = c.next

# cups = [int(c) for c in input_string]

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
