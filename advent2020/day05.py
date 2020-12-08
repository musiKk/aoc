#!/usr/bin/env python

with open("input05") as file:
    lines = [ line.strip() for line in file.readlines() if line != "" ]

seat_ids = []

for line in lines:
    row_code = line[0:7]
    col_code = line[7:]

    row_code = row_code.replace("F", "0")
    row_code = row_code.replace("B", "1")
    col_code = col_code.replace("L", "0")
    col_code = col_code.replace("R", "1")

    row = int(row_code, 2)
    col = int(col_code, 2)

    seat_id = row * 8 + col
    seat_ids.append(seat_id)

    # print(f"row: {row_code}, col: {col_code} => {row}, {col} => {seat_id}")

print(max(seat_ids))

seat_ids = sorted(seat_ids)
last_seat_id = 0
for seat_id in seat_ids:
    print(f"{seat_id} => diff = {seat_id - last_seat_id}")
    last_seat_id = seat_id
