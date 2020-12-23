#!/usr/bin/env python

# starting_deck1 = [ 9, 2, 6, 3, 1 ]
# starting_deck2 = [ 5, 8, 4, 7, 10 ]

starting_deck1 = [ 8, 19, 46, 11, 36, 10, 35, 9, 24, 22, 50, 1,
        34, 7, 18, 28, 3, 38, 43, 2, 6, 42, 23, 12, 20 ]
starting_deck2 = [ 39, 27, 44, 29, 5, 48, 30, 32, 15, 31, 14, 21, 49,
        17, 45, 47, 16, 26, 33, 25, 13, 41, 4, 40, 37 ]

deck1 = list(starting_deck1)
deck2 = list(starting_deck2)

round = 1
while len(deck1) > 0 and len(deck2) > 0:
    # print(f"-- Round {round} -- ")
    # print(f"Player 1's deck: {deck1}")
    # print(f"Player 2's deck: {deck2}")
    card1 = deck1.pop(0)
    card2 = deck2.pop(0)
    # print(f"Player 1 plays: {card1}")
    # print(f"Player 2 plays: {card2}")
    if card1 > card2:
        # print("Player 1 wins the round!\n")
        deck1.append(card1)
        deck1.append(card2)
    elif card2 > card1:
        # print("Player 2 wins the round!\n")
        deck2.append(card2)
        deck2.append(card1)
    else:
        raise Exception()

    round += 1

print(deck1)
print(deck2)

if len(deck1) > 0:
    winning_deck = deck1
else:
    winning_deck = deck2

sum = 0
for i, card in enumerate(reversed(winning_deck)):
    sum += (i + 1) * card

print(sum)

# part2

def get_configuration(deck):
    return ''.join([ str(i) for i in deck])

# seen_configurations1 = set()
# seen_configurations2 = set()

def play_game(deck1, deck2, game=1):
    print(f"=== Game {game} ===\n")

    seen_configurations1 = set()
    seen_configurations2 = set()

    round = 1

    while len(deck1) > 0 and len(deck2) > 0:
        print(f"-- Round {round} (Game {game}) --")
        print(f"Player 1's deck: {deck1}")
        print(f"Player 2's deck: {deck2}")
        configuration1, configuration2 = (get_configuration(deck1), get_configuration(deck2))
        if configuration1 in seen_configurations1 and configuration2 in seen_configurations2:
            print(f"player 1 wins by duplicate configuration {configuration1}, {configuration2}")
            return (deck1, [])

        seen_configurations1.add(configuration1)
        seen_configurations2.add(configuration2)

        card1 = deck1.pop(0)
        card2 = deck2.pop(0)

        print(f"Player 1 plays: {card1}")
        print(f"Player 2 plays: {card2}")

        if len(deck1) >= card1 and len(deck2) >= card2:
            # print(f"recursing with {deck1[:card1]}, {deck2[:card2]}")
            print("Playing a sub-game to determine the winner...\n")
            result_deck1, result_deck2 = play_game(deck1[:card1], deck2[:card2], game+1)
            if len(result_deck1) == 0 and len(result_deck2) > 0:
                print(f"The winer of game {game+1} is player 2!\n")
                winner = 2
            elif len(result_deck2) == 0 and len(result_deck1) > 0:
                print(f"The winer of game {game+1} is player 1!\n")
                winner = 1
            else:
                raise Exception(f"decks are {deck1}, {deck2}")
            print(f"...anyway, back to game {game}.")
        else:
            if card1 > card2:
                winner = 1
            elif card2 > card1:
                winner = 2
            else:
                raise Exception(f"cards are {card1}, {card2}")
        if winner == 1:
            print(f"Player 1 wins round {round} of game {game}!\n")
            deck1.append(card1)
            deck1.append(card2)
        elif winner == 2:
            print(f"Player 2 wins round {round} of game {game}!\n")
            deck2.append(card2)
            deck2.append(card1)
        else:
            raise Exception()
        round += 1
    return (deck1, deck2)

deck1 = list(starting_deck1)
deck2 = list(starting_deck2)

r1, r2 = play_game(deck1, deck2)
print(r1)
print(r2)

if len(r1) > 0:
    winning_deck = r1
else:
    winning_deck = r2

sum = 0
for i, card in enumerate(reversed(winning_deck)):
    sum += (i + 1) * card

print(sum)
