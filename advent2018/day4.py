#!/usr/bin/env python

import re
from typing import List, Mapping
from dataclasses import dataclass, field

lines = []

with open("day4.input") as file:
    for line in file.readlines():
        if line != "\n":
            lines.append(line.strip())

lines.sort()

@dataclass
class GuardSchedule:
    sleep_minutes: List[int] = field(default_factory=lambda: [0] * 60)

def create_guard_sleep_minutes():
    guard_sleep_minutes: Mapping[int, GuardSchedule] = {}
    mode = "awake"
    last_time = -1
    active_guard = -1

    for line in lines:
        print(line)
        match = re.match(".* (\d\d):(\d\d)\] Guard #(\d+) begins shift", line)
        if match is not None:
            active_guard = int(match.group(3))
            if match.group(1) == "23":
                last_time = 0
            else:
                last_time = int(match.group(2))
            print(f"guard {active_guard} starts at min {last_time}")
        else:
            match = re.search(".*:(\d\d)\].*", line)
            current_minute = int(match.group(1))
            if mode == "awake":
                mode = "asleep"
                last_time = current_minute
            else:
                mode = "awake"
                guard_tracking = guard_sleep_minutes.get(active_guard, GuardSchedule())
                # print(f" - guard {active_guard} has {guard_tracking}")
                # print(f" guard slept from {last_time} to {current_minute}")
                for min in range(last_time - 1, current_minute - 1):
                    guard_tracking.sleep_minutes[min] += 1
                # print(f" + guard {active_guard} has {guard_tracking}")
                guard_sleep_minutes[active_guard] = guard_tracking
    return guard_sleep_minutes

def calc_max_sleeping_guard(guard_sleep_minutes):
    max_num = -1
    max_guard = -1
    for guard_num in guard_sleep_minutes:
        guard_sum = sum(guard_sleep_minutes[guard_num].sleep_minutes)
        print(f"guard #{guard_num} slept {guard_sum}")

        if guard_sum > max_num:
            max_num = guard_sum
            max_guard = guard_num

    max_minutes = guard_sleep_minutes[max_guard].sleep_minutes
    max_minute = max_minutes.index(max(max_minutes)) + 1

    print(f" => max: guard #{max_guard} slept {max_num} minutes (max minute is {max_minute})")

def calc_max_minute(guard_sleep_minutes):
    for guard_num in guard_sleep_minutes:
        sleep_minutes = guard_sleep_minutes[guard_num].sleep_minutes
        max_sleep = -1
        max_minute = -1
        for minute in range(0, 60):
            if sleep_minutes[minute] > max_sleep:
                max_sleep = sleep_minutes[minute]
                max_minute = minute + 1
        print(f"guard #{guard_num} slept most at minute {max_minute}: {max_sleep}")


guard_sleep_minutes = create_guard_sleep_minutes()
# calc_max_sleeping_guard(guard_sleep_minutes)
calc_max_minute(guard_sleep_minutes)
