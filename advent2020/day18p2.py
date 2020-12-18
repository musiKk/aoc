#!/usr/bin/env python

from dataclasses import dataclass
from typing import List, Union

with open("input18") as file:
    lines = [ l.strip() for l in file.readlines() ]

tokens = [
    "NUM", "PLUS", "MINUS", "TIMES", "OPEN", "CLOSE"
]

t_PLUS = r"\+"
t_MINUS = r"-"
t_TIMES = r"\*"
t_OPEN = r"\("
t_CLOSE = r"\)"
t_ignore = " "

def t_NUM(t):
    r"\d+"
    t.value = int(t.value)
    return t

def t_error(t):
    print("Illegal character '%s'" % t.value[0])
    raise Exception()

import ply.lex as lex
lexer = lex.lex()

precedence = (
    ("left", "TIMES"),
    ("left", "PLUS", "MINUS")
)

def p_expr_plusminus(t):
    """expr : expr PLUS expr
            | expr MINUS expr
            | expr TIMES expr"""
    # t[0] = (t[2], t[1], t[3])
    print(f"parsing {t[1]} {t[2]} {t[3]}")
    if t[2] == "+":
        t[0] = t[1] + t[3]
    elif t[2] == "-":
        t[0] = t[1] - t[3]
    elif t[2] == "*":
        t[0] = t[1] * t[3]
    else:
        raise Exception("")

def p_expr_group(t):
    "expr : OPEN expr CLOSE"
    t[0] = t[2]

def p_expr_num(t):
    "expr : NUM"
    t[0] = t[1]

import ply.yacc as yacc
parser = yacc.yacc()

sum = 0
for line in lines:
    result = parser.parse(line)
    print(result)
    sum += result

print(sum)
