#!/usr/bin/env python

import re

double_matcher = re.compile(r"(.)\1")
triple_matcher = re.compile(r"(.)\1\1")


def valid_password(password):
    # print(f"checking {password}")
    last_digit = -1
    for c in password:
        if int(c) < last_digit:
            return False
        last_digit = int(c)
    double_match = double_matcher.search(password) != None
    print(f"{password} is only increasing, double match: {double_match}")
    return double_match

def valid_password2(password):
    last_digit = -1
    for c in password:
        if int(c) < last_digit:
            return False
        last_digit = int(c)
    print(f"{password} is only increasing")
    for d in range(0, 10):
        double = str(d) * 2
        if double not in password:
            continue
        print(" - contains a double")
        triple = str(d) * 3
        if triple not in password:
            print(" - contains no triple")
            return True

    return False


input = range(168630, 718099)
# input = [ 111111, 223450, 123789 ]
# input = range(168888, 168889)
# input = range(111111, 111112)

count = 0
for n in input:
    if valid_password2(str(n)):
        count += 1

print(count)
