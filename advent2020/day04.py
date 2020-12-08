#!/usr/bin/env python

from typing import Mapping
import re

lines = []
with open('input04') as file:
    lines = [ l.strip() for l in file.readlines() ]

batches = []
cur_batch = []
for l in lines:
    if l == "":
        batches.append(cur_batch[:])
        cur_batch = []
    else:
        cur_batch.extend(l.split(" "))

batches.append(cur_batch)

required_fields = set([ "byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid" ])

def extended_validation(field_values: Mapping[str, str]):
    invalid_value_present = False
    for k, v in field_values.items():
        if k == "byr":
            pattern = re.compile(r"\d{4}")
            if pattern.fullmatch(v) and 1920 <= int(v) <= 2002:
                pass
            else:
                invalid_value_present = True
        elif k == "iyr":
            pattern = re.compile(r"\d{4}")
            if pattern.fullmatch(v) and 2010 <= int(v) <= 2020:
                pass
            else:
                invalid_value_present = True
        elif k == "eyr":
            pattern = re.compile(r"\d{4}")
            if pattern.fullmatch(v) and 2020 <= int(v) <= 2030:
                pass
            else:
                invalid_value_present = True
        elif k == "hgt":
            pattern = re.compile(r"(\d+)(cm|in)")
            m = pattern.fullmatch(v)
            if m:
                if m.group(2) == "cm" and 150 <= int(m.group(1)) <= 193:
                    pass
                elif m.group(2) == "in" and 59 <= int(m.group(1)) <= 76:
                    pass
                else:
                    invalid_value_present = True
            else:
                invalid_value_present = True
        elif k == "hcl":
            pattern = re.compile(r"#[0-9a-f]{6}")
            if pattern.fullmatch(v):
                pass
            else:
                invalid_value_present = True
        elif k == "ecl":
            if v not in [ "amb", "blu", "brn", "gry", "grn", "hzl", "oth" ]:
                invalid_value_present = True
        elif k == "pid":
            pattern = re.compile(r"\d{9}")
            if pattern.fullmatch(v):
                pass
            else:
                invalid_value_present = True


    return not invalid_value_present

valid_passports = 0
valid_passports2 = 0
for batch in batches:
    seen_fields = set()
    field_values = {}
    for field in batch:
        parts = field.split(":")
        seen_fields.add(parts[0])
        field_values[parts[0]] = parts[1]
    if required_fields & seen_fields == required_fields:
        valid_passports += 1
        if extended_validation(field_values):
            valid_passports2 += 1

print(valid_passports)
print(valid_passports2)
