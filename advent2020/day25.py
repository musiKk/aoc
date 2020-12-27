#!/usr/bin/env python

# public_key1, public_key2 = (5764801, 17807724)
public_key1, public_key2 = (335121, 363891)

MAGIC = 20201227

def get_all_transformations(subject, max_loop_size):
    # transformations = []
    value = 1
    for i in range(0, max_loop_size + 1):
        value *= subject
        value %= MAGIC
        # transformations.append(value)
        yield value
    # return transformations


def transform_subject(subject, loop_size):
    value = 1
    for i in range(0, loop_size):
        value *= subject
        value %= MAGIC
    return value

def find_loop_size(key):
    # for loop_size in range(1, 100000):
    #     if transform_subject(subject=7, loop_size=loop_size) == key:
    #         return loop_size
    for i, transformed_value in enumerate(get_all_transformations(7, 100000000)):
        if transformed_value == key:
            return i + 1
    raise Exception(f"loop size not found for key {key}")

loop_size1 = find_loop_size(public_key1)
loop_size2 = find_loop_size(public_key2)

print(f"loop sizes are {loop_size1} and {loop_size2}")

encryption_key1 = transform_subject(subject=public_key1, loop_size=loop_size2)
encryption_key2 = transform_subject(subject=public_key2, loop_size=loop_size1)

print(f"encryption keys are {encryption_key1} and {encryption_key2}")
