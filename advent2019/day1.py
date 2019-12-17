#!/usr/bin/env python

input = []
with open("day1.input") as file:
    input = [ line.strip() for line in file.readlines() if line.strip() != '' ]

def calc_fuel(mass):
    return int(mass / 3) - 2


def simple_calculation():
    fuel_requirements = []
    for mass in input:
        mass = int(mass)
        fuel = calc_fuel(mass)
        fuel_requirements.append(fuel)

    print(sum(fuel_requirements))

def calc_fuel_rec(mass, fuel_acc=0):
    fuel = calc_fuel(mass)
    if fuel < 0:
        return fuel_acc
    else:
        return calc_fuel_rec(fuel, fuel_acc + fuel)

def complex_calculation():
    fuel_requirements = []

    for mass in input:
        mass = int(mass)
        fuel = calc_fuel_rec(mass)
        print(fuel)
        fuel_requirements.append(fuel)

    print(sum(fuel_requirements))

complex_calculation()
