#!/usr/bin/env python

import itertools

# input = '1,9,10,3,2,3,11,0,99,30,40,50'
# input = '1,1,1,4,99,5,6,0,99'
input = ''
with open('day2.input') as file:
    input = file.readline().strip()

opcodes = [int(opcode) for opcode in input.split(',')]

def calc(opcodes, noun=12, verb=2):
    opcodes[1] = noun
    opcodes[2] = verb
    
    pos = 0
    pc = 0
    while True:
        current_opcode = opcodes[pc]
        # print(f"{pc} - {current_opcode}")
        if current_opcode in [1, 2]:
            addr_target = opcodes[pc + 3]
            addr_left = opcodes[pc + 1]
            addr_right = opcodes[pc + 2]

            # print(f"storing into addr {addr_target} result of {addr_left} {addr_right}")

            if current_opcode == 1:
                opcodes[addr_target] = opcodes[addr_left] + opcodes[addr_right]
                # print(f" - {opcodes[addr_target]} = {opcodes[addr_left]} + {opcodes[addr_right]}")
            elif current_opcode == 2:
                opcodes[addr_target] = opcodes[addr_left] * opcodes[addr_right]
                # print(f" - {opcodes[addr_target]} = {opcodes[addr_left]} * {opcodes[addr_right]}")
            pc += 4
        elif current_opcode == 99:
            # print(opcodes)
            # print(opcodes[0])
            # break
            return opcodes[0]
        else:
            raise Exception(f"unknown opcode {current_opcode}")

# result = calc(opcodes[:])
# print(result)

# noun = 0
# verb = 0
# inc_left = False
for (noun, verb) in itertools.product(range(1, 100), range(1, 100)):
    result = calc(opcodes[:], noun, verb)
    print(f"{noun} {verb} => {result}")
    if result == 19690720:
        print(f"noun: {noun}, verb: {verb}")
        break
    # else:
    #     if inc_left:
    #         noun += 1
    #         inc_left = False
    #     else:
    #         verb += 1
    #         inc_left = True
