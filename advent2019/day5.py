#!/usr/bin/env python

import itertools
import sys

# input = '1,9,10,3,2,3,11,0,99,30,40,50'
# input = '1,1,1,4,99,5,6,0,99'
input = ''
with open('day5.input') as file:
    input = file.readline().strip()

# input = '3,9,8,9,10,9,4,9,99,-1,8'
# input = '3,9,7,9,10,9,4,9,99,-1,8'
# input = '3,3,1108,-1,8,3,4,3,99'
# input = '3,3,1107,-1,8,3,4,3,99'
# input = '3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9'
# input = '3,3,1105,-1,9,1101,0,0,12,4,12,99,1'

opcodes = [int(opcode) for opcode in input.split(',')]

def get_value(opcodes, pc, pos, parameter_modes):
    # print(f" -- getting parameter {pos} ({parameter_modes})")
    parameter_mode = 0
    try:
        parameter_mode = int(parameter_modes[pos - 1])
    except:
        parameter_mode = 0
    if parameter_mode == 0:
        # print(f" --- fetching address at {pc} + {pos}")
        addr = opcodes[pc + pos]

        # print(f" --- address mode, addr {addr}, value {opcodes[addr]}")
        return opcodes[addr]
    else:
        # print(" --- direct mode")
        return opcodes[pc + pos]

def calc(opcodes, noun=12, verb=2):
    # opcodes[1] = noun
    # opcodes[2] = verb

    pos = 0
    pc = 0
    while True:
        current_opcode = opcodes[pc] % 100
        parameter_modes = str(opcodes[pc])[:-2][::-1]
        # print(f"{pc} - {current_opcode} ({opcodes[pc]}) ({opcodes})")
        if current_opcode in [1, 2, 7, 8]:
            addr_target = opcodes[pc + 3]
            left = get_value(opcodes, pc, 1, parameter_modes)
            right = get_value(opcodes, pc, 2, parameter_modes)

            # print(f"storing into addr {addr_target} result of {addr_left} {addr_right}")

            if current_opcode == 1:
                # print(f" - {addr_target} <= {left} + {right}")
                opcodes[addr_target] = left + right
            elif current_opcode == 2:
                # print(f" - {addr_target} <= {left} * {right}")
                opcodes[addr_target] = left * right
            elif current_opcode == 7:
                if left < right:
                    opcodes[addr_target] = 1
                else:
                    opcodes[addr_target] = 0
            elif current_opcode == 8:
                if left == right:
                    opcodes[addr_target] = 1
                else:
                    opcodes[addr_target] = 0
            pc += 4
        elif current_opcode == 3:
            addr_target = opcodes[pc + 1]
            print("prompt > ", end='', flush=True)
            line = sys.stdin.readline().strip()
            opcodes[addr_target] = int(line)
            pc += 2
        elif current_opcode == 4:
            value = get_value(opcodes, pc, 1, parameter_modes)
            print(f"printing {value}")
            pc += 2
        elif current_opcode == 5:
            if (get_value(opcodes, pc, 1, parameter_modes)) != 0:
                pc = get_value(opcodes, pc, 2, parameter_modes)
                # print(f"setting pc to {pc}")
            else:
                pc += 3
        elif current_opcode == 6:
            if (get_value(opcodes, pc, 1, parameter_modes)) == 0:
                pc = get_value(opcodes, pc, 2, parameter_modes)
                # print(f"setting pc to {pc}")
            else:
                pc += 3
        elif current_opcode == 99:
            # print(opcodes)
            # print(opcodes[0])
            # break
            return opcodes[0]
        else:
            raise Exception(f"unknown opcode {current_opcode}")

result = calc(opcodes[:])
# print(result)

# for (noun, verb) in itertools.product(range(1, 100), range(1, 100)):
#     result = calc(opcodes[:], noun, verb)
#     print(f"{noun} {verb} => {result}")
#     if result == 19690720:
#         print(f"noun: {noun}, verb: {verb}")
#         break
