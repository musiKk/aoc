#!/usr/bin/env python

import re

button_regex = r"Button .: X.(\d+), Y.(\d+)"
prize_regex = r"Prize: X=(\d+), Y=(\d+)"
prize_shift = 10000000000000

def part2():
    machines = []
    with open('input13') as f:
        lines = [l.strip() for l in f]
        # print(lines)

        i = 0
        while i < len(lines):
            machine = []
            m = re.match(button_regex, lines[i])
            machine.append(int(m.group(1)))
            machine.append(int(m.group(2)))
            i += 1

            m =re.match(button_regex, lines[i])
            machine.append(int(m.group(1)))
            machine.append(int(m.group(2)))
            i += 1

            m =re.match(prize_regex, lines[i])
            machine.append(int(m.group(1)) + prize_shift)
            machine.append(int(m.group(2)) + prize_shift)
            i += 2

            machines.append(machine)

    tokens_used = 0
    for machine in machines:
        tokens_used += solve_machine(machine)
    print(tokens_used)

def solve_machine(machine):
    from sympy import solve_poly_system, symbols

    ax, ay, bx, by, px, py = machine
    sa, sb = symbols("sa sb")

    eqns = [
        sa * ax + sb * bx - px,
        sa * ay + sb * by - py,
    ]
    result = solve_poly_system(eqns, [sa, sb])
    try:
        # print(result)
        sar, sbr = result[0]
        if int(sar) != sar:
            return 0
        if int(sbr) != sbr:
            return 0

        return sar * 3 + sbr
    except Exception as e:
        print(e)
        return 0

if __name__ == '__main__':
    part2()
