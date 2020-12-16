#!/usr/bin/env python

from typing import List, Dict, Tuple
import re

with open('input16.example2') as file:
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

def collect_field_assignments(remaining_fields: List[str], current_assignments: List[str]) -> List[List[str]]:
    print(f"still need to assign {remaining_fields} - currently have {current_assignments}")
    # print(remaining_fields)
    if len(remaining_fields) == 0:
        return [current_assignments]
    assignments_to_return: List[List[str]] = []
    # value_to_assign = my_ticket[len(my_ticket) - len(remaining_fields)]
    for i, field in enumerate(remaining_fields):
        ranges = field_ranges[field]
        print(f" => checking that all at {i} fit into {field} {ranges}")
        if ranges_work_for_all(i, ranges):
            # found a match
            new_current_assignments = list(current_assignments)
            new_current_assignments.append(field)
            new_remaining_fields = list(remaining_fields)
            del new_remaining_fields[i]
            assignments_to_return += collect_field_assignments(new_remaining_fields, new_current_assignments)
        else:
            print(f" => {field} does not match")
            pass
    return assignments_to_return

assignments = collect_field_assignments(list(field_ranges.keys()), [])
print(assignments)
