#!/usr/bin/env python

wires = []
with open('day3.input') as file:
    wires = [ file.readline().strip(), file.readline().strip() ]

# wires = [
#     "R8,U5,L5,D3", "U7,R6,D4,L4"
# ]

def update_board(board, x, y, wire_code, step):
    current_count = board.get((x, y))
    if current_count is None:
        board[(x, y)] = [wire_code, 1, {wire_code: step}]
    else:
        if current_count[0] == wire_code:
            pass
        else:
            current_count[1] += 1
            if wire_code not in current_count[2]:
                current_count[2][wire_code] = step

board = {}
wire_code = 1
for wire in wires:
    step = 1
    (x, y) = (0, 0)
    directions = wire.split(',')
    for direction in directions:
        amount = int(direction[1:])
        dir = direction[0]
        if dir == 'R':
            for n in range(0, amount):
                x += 1
                update_board(board, x, y, wire_code, step)
                step += 1
        elif dir == 'U':
            for n in range(0, amount):
                y += 1
                update_board(board, x, y, wire_code, step)
                step += 1
        elif dir == 'L':
            for n in range(0, amount):
                x -= 1
                update_board(board, x, y, wire_code, step)
                step += 1
        elif dir == 'D':
            for n in range(0, amount):
                y -= 1
                update_board(board, x, y, wire_code, step)
                step += 1
    wire_code += 1

# print(board)
distances = []
delay_sums = []
for coordinate in board:
    piece = board[coordinate]
    crossing = piece[1]
    delays = piece[2]
    if crossing > 1:
        distance = abs(coordinate[0]) + abs(coordinate[1])
        distances.append(distance)
        delay_sums.append(sum(delays.values()))
        print(f"crossing at {coordinate} - dist {distance}, delays: {delays}")
print(f"min distance: {min(distances)}")
print(f"min delays:   {min(delay_sums)}")
