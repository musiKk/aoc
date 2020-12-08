#!/usr/bin/env python

numbers = []

with open('input01') as file:
    for line in file.readlines():
        numbers.append(int(line))

orignumdict = { n: 1 for n in numbers }

new_numbers = []


for n1 in numbers:
    for n2 in numbers:
        if n1 <= n2 or n1 + n2 >= 2020:
            continue
        new_numbers.append((n1 + n2, n1, n2))

print(new_numbers)
print(len(new_numbers))

numdict = { n[0]: (n[1], n[2]) for n in new_numbers }

for n in new_numbers:
    candidate = 2020 - n[0]
    print(f"2020 - {n[0]} = {candidate}")
    if candidate in orignumdict:
        print(f"{candidate} - {n}")
        break
