#!/usr/bin/env python

lines = []
with open('input06') as file:
    lines = [ l.strip() for l in file.readlines() ]

batches = []
cur_batch = []
for l in lines:
    if l == "":
        batches.append(cur_batch[:])
        cur_batch = []
    else:
        cur_batch.extend(l.split(" "))

batches.append(cur_batch)

unique_answers = []
any_answers = []
for batch in batches:
    unique_batch_answers = set()
    any_batch_answers = set(batch[0])
    for line in batch:
        unique_batch_answers.update(set(line))
        any_batch_answers.intersection_update(set(line))
    unique_answers.append(len(unique_batch_answers))
    any_answers.append(len(any_batch_answers))

    print(f"batch {batch} => unique: {len(unique_batch_answers)}, any: {len(any_batch_answers)}")

print(sum(unique_answers))
print(sum(any_answers))
