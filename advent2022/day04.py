#!/usr/bin/env python

START = 0
END = 1

with open("day04.input") as f:
    count = 0
    count2 = 0
    for line in f:
        line = line.strip()
        if line == "":
            break

        section_descriptors = line.split(",")

        e1sections = [int(v) for v in section_descriptors[0].split("-")]
        e2sections = [int(v) for v in section_descriptors[1].split("-")]

        if ((e1sections[START] >= e2sections[START] and e1sections[END] <= e2sections[END])
            or (e2sections[START] >= e1sections[START] and e2sections[END] <= e1sections[END])):
            count += 1


        print(section_descriptors)
        if (e1sections[END] < e2sections[START] or e1sections[START] > e2sections[END]
            or e2sections[END] < e1sections[START] or e2sections[START] > e1sections[END]):
            print("no")
        else:
            print("yes")
            count2 += 1
    print(count)
    print(count2)
