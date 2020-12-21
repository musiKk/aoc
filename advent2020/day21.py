#!/usr/bin/env python

from dataclasses import dataclass
from typing import List
import re

@dataclass
class Recipe:
    ingredients: List[str]
    allergens: List[str]

recipes = []

with open('input21') as file:
    for line in file.readlines():
        line = line.strip()
        if line == "":
            continue
        match = re.match(r"(.*?) \(contains (.*)\)", line)
        recipes.append(Recipe(match.group(1).split(" "), match.group(2).split(", ")))

allergens_potentially_in = {}
for recipe in recipes:
    for allergen in recipe.allergens:
        if allergen not in allergens_potentially_in:
            allergens_potentially_in[allergen] = set(recipe.ingredients)
        else:
            allergens_potentially_in[allergen].intersection_update(recipe.ingredients)

print(allergens_potentially_in)
all_potential_allergens = set()
for a in allergens_potentially_in.values():
    all_potential_allergens.update(a)

print(f"potential allergens: {all_potential_allergens}")

non_allergen_count = 0
for recipe in recipes:
    for ingredient in recipe.ingredients:
        if ingredient not in all_potential_allergens:
            non_allergen_count += 1

print(non_allergen_count)
