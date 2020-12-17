#!/usr/bin/env python

from typing import List, Dict, Tuple
import re
from functools import lru_cache

with open('input16') as file:
    lines = [ s.strip() for s in file.readlines() if s.strip() != "" ]

field_ranges: Dict[str, Tuple[Tuple[int, int], Tuple[int, int]]] = {}
my_ticket: List[int] = []
nearby_tickets: List[List[int]] = []

for line in lines:
    m = re.match(r"(.+?): ([0-9]+)-(\d+) or ([0-9]+)-(\d+)", line)
    if m:
        field_ranges[m.group(1)] = (
            (int(m.group(2)), int(m.group(3))), (int(m.group(4)), int(m.group(5)))
        )
    elif (line[-1] != ":"):
        values = [int(n) for n in line.split(",")]
        if len(my_ticket) == 0:
            my_ticket = values
        else:
            nearby_tickets.append(values)

valid_tickets = []

error_rate = 0
for ticket in nearby_tickets:
    ticket_valid = True
    for value in ticket:
        valid = False
        for ranges in field_ranges.values():
            # print(f"checking if {value} in {ranges}")
            if ((ranges[0][0] <= value <= ranges[0][1]) or
                (ranges[1][0] <= value <= ranges[1][1])):
                # print(" => yes")
                valid = True
                break
            else:
                # print(" => no")
                pass
        if not valid:
            error_rate += value
            ticket_valid = False
            # print(f" => error rate increases to {error_rate}")
    if ticket_valid:
        valid_tickets.append(ticket)


print(error_rate)
print()

@lru_cache(maxsize=10000)
def ranges_work_for_all(i, ranges):
    value = my_ticket[i]
    if ((ranges[0][0] <= value <= ranges[0][1]) or
            (ranges[1][0] <= value <= ranges[1][1])):
        pass
    else:
        return False

    for ticket in valid_tickets:
        value = ticket[i]
        if ((ranges[0][0] <= value <= ranges[0][1]) or
                (ranges[1][0] <= value <= ranges[1][1])):
            pass
        else:
            return False
    return True

def prn(s, depth):
    print(' ' * depth + s)

def permute(left_ranges: List[str], used_ranges: List[str], depth=0):
    # prn(f"still need to check {len(left_ranges)}", depth)
    if len(left_ranges) == 0:
        print(used_ranges)
        return True
    for i, left_range in enumerate(left_ranges):
        # prn(f"trying {left_range}", depth)
        if ranges_work_for_all(len(my_ticket) - len(left_ranges), field_ranges[left_range]):
            # prn("looks promising", depth)
            new_left_ranges = list(left_ranges)
            del new_left_ranges[i]
            new_used_ranges = list(used_ranges)
            new_used_ranges.append(left_range)
            if permute(new_left_ranges, new_used_ranges, depth+1):
                return True
        else:
            # prn(f"it's not {left_range}", depth)
            if depth < 4:
                prn(f"discarded {left_range} w/ {used_ranges}", depth)
            pass
    return False

result = permute(field_ranges.keys(), [])
print(result)

#  113,     53,     97,                 59,    139,     73,              89,               109,67,71,79,127,149,107,137,83,131,101,61,103
# ['class', 'seat', 'arrival platform', 'row', 'price', 'arrival track', 'departure time', 'type', 'arrival location', 'train', 'departure platform', 'departure date', 'duration', 'departure track', 'route', 'arrival station', 'departure station', 'wagon', 'zone', 'departure location']
