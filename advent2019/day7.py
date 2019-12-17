#!/usr/bin/env python

from itertools import permutations
import sys

input = ''
with open('day7.input') as file:
    input = file.readline().strip()
# input = '3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0'
input = '3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5'

opcodes = [int(opcode) for opcode in input.split(',')]

def get_value(opcodes, pc, pos, parameter_modes):
    parameter_mode = 0
    try:
        parameter_mode = int(parameter_modes[pos - 1])
    except:
        parameter_mode = 0
    if parameter_mode == 0:
        addr = opcodes[pc + pos]
        return opcodes[addr]
    else:
        # print(" --- direct mode")
        return opcodes[pc + pos]

def calc(opcodes, inputs):
    pos = 0
    pc = 0
    output = None
    while True:
        current_opcode = opcodes[pc] % 100
        # print(f"{pc} - {current_opcode} ({opcodes[pc]}) ({opcodes})")
        parameter_modes = str(opcodes[pc])[:-2][::-1]
        if current_opcode in [1, 2, 7, 8]:
            addr_target = opcodes[pc + 3]
            left = get_value(opcodes, pc, 1, parameter_modes)
            right = get_value(opcodes, pc, 2, parameter_modes)


            if current_opcode == 1:
                opcodes[addr_target] = left + right
            elif current_opcode == 2:
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
            print("reading")
            addr_target = opcodes[pc + 1]
            input = inputs[0]
            inputs = inputs[1:]
            opcodes[addr_target] = input
            pc += 2
        elif current_opcode == 4:
            value = get_value(opcodes, pc, 1, parameter_modes)
            print(f"producing output {value}")
            output = value
            inputs.append(value)
            pc += 2
        elif current_opcode == 5:
            if (get_value(opcodes, pc, 1, parameter_modes)) != 0:
                pc = get_value(opcodes, pc, 2, parameter_modes)
            else:
                pc += 3
        elif current_opcode == 6:
            if (get_value(opcodes, pc, 1, parameter_modes)) == 0:
                pc = get_value(opcodes, pc, 2, parameter_modes)
            else:
                pc += 3
        elif current_opcode == 99:
            # return output
            return inputs
        else:
            raise Exception(f"unknown opcode {current_opcode}")

# result = calc(opcodes[:])

def puzzle1():
    phase_settings = permutations(range(0, 5))
    max_output = -1
    for phase_setting in phase_settings:
        print(f"trying phase setting {phase_setting}")

        last_output = 0
        for setting in phase_setting:
            input = [setting, last_output]
            last_output = calc(opcodes[:], input)
        print(f" -> output {last_output}")
        if last_output > max_output:
            max_output = last_output

    print(f"max output: {max_output}")

def puzzle2():
    phase_settings = permutations(range(5, 10))


    phase_setting = [9, 8, 7, 6, 5]
    memories = [ (opcodes[:], [setting]) for setting in phase_setting ]
    last_output = [0]
    for setting in phase_setting:
        input = [setting]
        input.extend(last_output)
        print(f"running setting {setting} with input {input}")
        last_output = calc(opcodes[:], input)
        print(f" -> got output {last_output}")
    print(f"last output: {last_output}")

puzzle2()
