#!/usr/bin/env python

ROCK = 1
PAPER = 2
SCISSORS = 3

def calc_outcome(opp, me):
    if opp == me:
        return 3
    if opp == ROCK:
        if me == PAPER:
            return 6
        else:
            return 0
    elif opp == PAPER:
        if me == ROCK:
            return 0
        else:
            return 6
    else:
        if me == ROCK:
            return 6
        else:
            return 0

def calc_code(opp, desired_outcome):
    if desired_outcome == 2:
        return opp
    if opp == ROCK:
        if desired_outcome == 1:
            return SCISSORS
        else:
            return PAPER
    elif opp == PAPER:
        if desired_outcome == 1:
            return ROCK
        else:
            return SCISSORS
    else:
        if desired_outcome == 1:
            return PAPER
        else:
            return ROCK

if __name__ == '__main__':
    score = 0
    score2 = 0
    with open('day02.input') as f:
        for line in f:
            line = line.strip()
            if line == "":
                break

            opponent = ord(line[0])
            me = ord(line[2])

            opponent_code = opponent - ord('A') + 1
            me_code = me - ord('X') + 1

            outcome = calc_outcome(opponent_code, me_code)
            score += me_code + outcome

            # part 2
            me_code2 = calc_code(opponent_code, me_code)
            me_code2outcome = {
                1: 0,
                2: 3,
                3: 6
            }
            score2 += me_code2 + me_code2outcome[me_code]
    print(score)
    print(score2)


