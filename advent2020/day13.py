#!/usr/bin/env python

from math import floor

earliest = 939
bus_input = "7,13,x,x,59,x,31,19"

earliest = 1011416
bus_input = "41,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,37,x,x,x,x,x,911,x,x,x,x,x,x,x,x,x,x,x,x,13,17,x,x,x,x,x,x,x,x,23,x,x,x,x,x,29,x,827,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,19"

buses = [x if x == "x" else int(x) for x in bus_input.split(",")]

# part 1

available_buses = [x for x in buses if x != "x"]
print(f"buses: {available_buses}")

first_departure = [ b + b * floor(earliest / b) for b in available_buses ]
# print(first_departure)

diffs = [ d - earliest for d in first_departure ]
print(f"diffs: {diffs}")
print()

# # part 2

offsets = []
for i, bus in enumerate(buses):
    if bus != "x":
        offsets.append(i)

current_multiplier_rate = 1
current_multiplier = 1

for i in range(1, len(available_buses)):
    print(f"checking for i={i}")
    while True:
        dep0 = available_buses[0] * current_multiplier
        desired_bus_dep = dep0 + offsets[i]
        if desired_bus_dep % available_buses[i] == 0:
            print(f" => found multiplier {current_multiplier}")
            current_multiplier_rate *= available_buses[i]
            break
        current_multiplier += current_multiplier_rate

print(f"first offset: {current_multiplier * available_buses[0]}")
