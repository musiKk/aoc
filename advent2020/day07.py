#!/usr/bin/env python

import re

with open('input07') as file:
    lines = [ l.strip() for l in file.readlines() if l != "" ]

rules = {}
matcher = re.compile(r"(.*?) bags contain (.*?)\.")
for line in lines:
    match = matcher.match(line)

    contents = match.group(2)
    contents_dict = {}
    for content in contents.split(", "):
        if content == "no other bags":
            continue
        content_match = re.match(r"(\d+) (.*?) bags?", content)
        contents_dict[content_match.group(2)] = int(content_match.group(1))

    rules[match.group(1)] = contents_dict

# print(rules)

def rec_search(color, level=0):
    # print(f"{'  ' * level}checking out {color}")
    contents = rules[color]
    if contents == {}:
        # print(f"{'  ' * level} -> no match")
        return False
    elif "shiny gold" in contents:
        # print(f"{'  ' * level} -> direct match")
        return True
    else:
        rec_results = [ rec_search(content_color, level + 1) for content_color in contents.keys() ]
        # print(f"{'  ' * level} -> {rec_results} -> {any(rec_results)}")
        return any(rec_results)

solution_colors = []
for color in rules.keys():
    if color != "shiny gold" and rec_search(color):
        solution_colors.append(color)

# print(solution_colors)
print(len(solution_colors))

def rec_count(color, level=0):
    contents = rules[color]
    # print(f"{'  ' * level}checking out {color} ({contents})")
    if contents == {}:
        # print(f"{'  ' * level}-> contains nothing")
        total = 0
    else:
        totals =  [content_count * rec_count(content_color, level + 1) for content_color, content_count in rules[color].items()]
        total = sum(totals) + sum(contents.values())
        # print(f"{'  ' * level}-> contains {totals}")

    # print(f"{'  ' * level}-> {total}")
    return total

print(rec_count("shiny gold"))
