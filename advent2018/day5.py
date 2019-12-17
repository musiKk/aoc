#!/usr/bin/env python

from string import ascii_lowercase
from re import sub, IGNORECASE

input = ""
with open("day5.input") as file:
    input = file.readline().strip()

def react_polymer(polymer):
    while True:
        deleted_something = False
        for n in range(0, len(polymer) - 2):
            (first, second) = polymer[n:n+2]
            # print(f"{polymer} - {first} {second}")
            if first != second and first.lower() == second.lower():
                # print(" -> deleting")
                polymer = polymer[0:n] + polymer[n+2:]
                # print(f" -> {polymer}")
                deleted_something = True
                print(f"{len(polymer)}... ", end='', flush=True)
                # break
        # print(f"through, deleted: {deleted_something}")
        if not deleted_something:
            print("")
            break
    return polymer

def react_polymer_regex(polymer):
    regex_parts = []
    for c in ascii_lowercase:
        regex_parts.append(c + c.upper())
        regex_parts.append(c.upper() + c)
    regex = '|'.join(regex_parts)
    while True:
        new_polymer = sub(regex, '', polymer)
        if new_polymer is None:
            break
        if polymer != new_polymer:
            polymer = new_polymer
            print(f"{len(polymer)}", end='', flush=True)
        else:
            print("")
            break
    return polymer

def get_reduced_reactions():
    reduced_redaction_counts = {}
    for letter in ascii_lowercase:
        reduced_polymer = sub(letter, "", input, flags=IGNORECASE)
        if reduced_polymer != input:
            reacted = react_polymer_regex(reduced_polymer)
            # print(f"removed {letter}, got {reacted}")
            reduced_redaction_counts[letter] = reacted
    return reduced_redaction_counts


# reacted_polymer = react_polymer_regex(input)
# print(f"polymer: {reacted_polymer}\nlen: {len(reacted_polymer)}")

counts = get_reduced_reactions()
print(counts)
for letter in counts:
    print(f"{letter} -> {len(counts[letter])}")
