#!/usr/bin/env python

import re

lines = []
with open('input14') as file:
    lines = [ l.strip() for l in file.readlines() ]

memory = {}
mask = ""

for line in lines:
    (left, right) = re.match(r"(.+?) = (.+)", line).groups()
    if left == "mask":
        mask = right
    else:
        mem_target = int(re.match(r"mem\[(\d+)]", left).group(1))
        bit_string = bin(int(right))[2:]
        bit_string = '0' * (36 - len(bit_string)) + bit_string

        updated_bit_string = list()
        for i in range(0, 36):
            mask_char = mask[i]
            if mask_char == 'X':
                new_char = bit_string[i]
            else:
                new_char = mask_char
            updated_bit_string.append(new_char)
        memory[mem_target] = int(''.join(updated_bit_string), base=2)

print(sum(memory.values()))

def get_all_addresses(mask, address):
    address_bits = bin(address)[2:]
    address_bits = '0' * (36 - len(address_bits)) + address_bits
    address_with_floats_list = list()

    floating_bit_indexes = list()
    for i in range(0, 36):
        mask_char = mask[i]
        if mask_char == '0':
            address_with_floats_list.append(address_bits[i])
        else:
            address_with_floats_list.append(mask_char)
            if mask_char == 'X':
                floating_bit_indexes.append(i)


    addresses = []
    for i in range(0, 2**len(floating_bit_indexes)):
        float_settings = bin(i)[2:]
        float_settings = '0' * (len(floating_bit_indexes) - len(float_settings)) + float_settings
        concrete_address = list(address_with_floats_list)

        for idx, floating_bit_index in enumerate(float_settings):
            concrete_address[floating_bit_indexes[idx]] = floating_bit_index
        addresses.append(int(''.join(concrete_address), base=2))

    return addresses


mem2 = {}
for line in lines:
    (left, right) = re.match(r"(.+?) = (.+)", line).groups()
    if left == "mask":
        mask = right
    else:
        mem_target = int(re.match(r"mem\[(\d+)]", left).group(1))
        addresses = get_all_addresses(mask, mem_target)
        for address in addresses:
            mem2[address] = int(right)

print(sum(mem2.values()))
