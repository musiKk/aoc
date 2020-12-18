#!/usr/bin/env python

from dataclasses import dataclass

with open("input18") as file:
    lines = [ l.strip() for l in file.readlines() ]

def tokens(line):
    i = 0
    def next_token():
        nonlocal i
        while i < len(line) and line[i] == " ":
            i += 1
        if i < len(line):
            t = line[i]
            i += 1
            return t
        else:
            return None
    return next_token

def eval_line(tokens, level=0):
    cur_val = 0
    cur_op = None

    c = tokens()
    while c:
        if c == ")":
            return cur_val
        if c in "(0123456789":
            if c == "(":
                value = eval_line(tokens, level+1)
            else:
                value = int(c)
            if cur_op:
                if cur_op == "+":
                    cur_val += value
                elif cur_op == "-":
                    cur_val -= value
                elif cur_op == "*":
                    cur_val *= value
                else:
                    raise Exception("unknown operator " + cur_op)
            else:
                cur_val = value
        elif c in "+-*":
            cur_op = c
        elif c == " ":
            pass
        c = tokens()
    return cur_val

sum = 0
for line in lines:
    val = eval_line(tokens(line))
    sum += val

print(sum)
