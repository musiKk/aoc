#!/usr/bin/env python

starting_numbers = [ 0, 12, 6, 13, 20, 1, 17 ]
numbers_last_said = {}
for i, num in enumerate(starting_numbers[:-1]):
    numbers_last_said[num] = i + 1
last_number = starting_numbers[-1]

round = len(numbers_last_said) + 1
while round < 30000000:
    round += 1
    # print(f"playing round {round} (last was {last_number}) - {numbers_last_said}")
    print(f"playing round {round}")

    if last_number not in numbers_last_said:
        new_number = 0
    else:
        # print(f" => {last_number} was said in {numbers_last_said[last_number]}")
        new_number = round - numbers_last_said[last_number] - 1

    print(f" => saying {new_number}")
    numbers_last_said[last_number] = round - 1
    last_number = new_number

# naive version, does not scale to large numbers
# numbers = [0,12,6,13,20,1,17]
# numbers = [2, 1, 3]
# round = len(numbers)
# while round < 2020:
#     round += 1
#     # print(f"playing round {round} - {numbers}")
#     print(f"playing round {round}")
#     found = False
#     new_num = -100
#     for round_to_check in range(round - 2, 0, -1):
#         # print(f" ==> checking if I said {numbers[round - 2]} in round {round_to_check}")
#         if numbers[round_to_check - 1] == numbers[round - 2]:
#             new_num = round - round_to_check - 1
#             # print(f" ==> yes! new is {new_num}")
#             found = True
#             break
#     if not found:
#         new_num = 0
#     print(f" => saying {new_num}")
#     numbers.append(new_num)

