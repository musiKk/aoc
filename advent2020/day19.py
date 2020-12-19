#!/usr/bin/env python

from typing import List, Dict, Union

rules_dict: Dict[str, Union[str, List[List[str]]]] = {}
lines: List[str] = []
with open('input19') as file:
    for l in file.readlines():
        l = l.strip()
        if l == "":
            continue

        if ":" in l:
            (rule, match) = l.split(": ")
            if match[0] == "\"":
                rules_dict[rule] = match[1]
            else:
                rule_options = match.split(" | ")
                rules_dict[rule] = [ rule_option.split(" ") for rule_option in rule_options ]
        else:
            lines.append(l)

# part 2
# r8s = []
# r11s = []
# for i in range(1, 10):
#     r11s.append(['42'] * i + ['31'] * i)
#     r8s.append(['42'] * i)

# rules_dict['8'] = list(r8s)
# rules_dict['11'] = list(r11s)

def expand_rule(rule_id='0'):
    rule = rules_dict[rule_id]
    if type(rule) is str:
        return rule

    branch_regexes = []
    for branch in rule:
        branch_regex = []
        for branch_symbol in branch:
            branch_regex.append(expand_rule(branch_symbol))
        branch_regexes.append(''.join(branch_regex))
    return f"({'|'.join(branch_regexes)})"

r0_regex = '^' + expand_rule() + '$'
print(r0_regex)

import re
count = 0
for line in lines:
    m = re.match(r0_regex, line)
    if m:
        print(f"{line} matches")
        count += 1
    else:
        print(f"{line} does not match")

print(count)
