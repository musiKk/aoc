#!/usr/bin/env python

import itertools
import sys

# input = '1,9,10,3,2,3,11,0,99,30,40,50'
# input = '1,1,1,4,99,5,6,0,99'
input = ''
with open('day9.input') as file:
    input = file.readline().strip()

# input = '109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99'
# input = '1102,34915192,34915192,7,4,7,99,0'
# input = '204,1125899906842624,99'

opcodes = [int(opcode) for opcode in input.split(',')]
opcodes.extend([0] * 10000)

def get_addr(opcodes, pc, pos, parameter_modes, relative_base):
    parameter_mode = 0
    try:
        parameter_mode = int(parameter_modes[pos - 1])
    except:
        parameter_mode = 0
    if parameter_mode == 0:
        # position mode
        # print(f" - position mode (pc: {pc}, pos: {pos}) -> {opcodes[pc + pos]}")
        addr = opcodes[pc + pos]
        return addr
    elif parameter_mode == 1:
        # immediate mode
        # print(f" - immediate mode (pc: {pc}, pos: {pos})")
        return pc + pos
    elif parameter_mode == 2:
        # relative mode
        # print(f" - relative mode (base: {relative_base}, pc: {pc}, pos: {pos}) -> {relative_base + opcodes[pc + pos]}")
        return relative_base + opcodes[pc + pos]

def calc(opcodes):
    pos = 0
    pc = 0
    relative_base = 0
    while True:
        current_opcode = opcodes[pc] % 100
        parameter_modes = str(opcodes[pc])[:-2][::-1]
        # print(f"{pc} - {current_opcode} ({opcodes[pc]}) (base: {relative_base}) ({opcodes})")
        if current_opcode in [1, 2, 7, 8]:
            addr_target = get_addr(opcodes, pc, 3, parameter_modes, relative_base)
            right = opcodes[get_addr(opcodes, pc, 2, parameter_modes, relative_base)]
            left = opcodes[get_addr(opcodes, pc, 1, parameter_modes, relative_base)]

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
            addr_target = get_addr(opcodes, pc, 1, parameter_modes, relative_base)
            print("prompt > ", end='', flush=True)
            line = sys.stdin.readline().strip()
            opcodes[addr_target] = int(line)
            pc += 2
        elif current_opcode == 4:
            value = opcodes[get_addr(opcodes, pc, 1, parameter_modes, relative_base)]
            print(f"printing {value}")
            pc += 2
        elif current_opcode == 5:
            if opcodes[get_addr(opcodes, pc, 1, parameter_modes, relative_base)] != 0:
                pc = opcodes[get_addr(opcodes, pc, 2, parameter_modes, relative_base)]
                # print(f"setting pc to {pc}")
            else:
                pc += 3
        elif current_opcode == 6:
            if opcodes[get_addr(opcodes, pc, 1, parameter_modes, relative_base)] == 0:
                pc = opcodes[get_addr(opcodes, pc, 2, parameter_modes, relative_base)]
                # print(f"setting pc to {pc}")
            else:
                pc += 3
        elif current_opcode == 9:
            adjust = opcodes[get_addr(opcodes, pc, 1, parameter_modes, relative_base)]
            relative_base += adjust
            pc += 2
        elif current_opcode == 99:
            # print(opcodes)
            # print(opcodes[0])
            # break
            return opcodes[0]
        else:
            raise Exception(f"unknown opcode {current_opcode}")

result = calc(opcodes[:])
