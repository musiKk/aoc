const std = @import("std");
const input = @import("day8.input.zig");
const Entry = input.Entry;

pub fn main() void {
    var sum: u32 = 0;
    for (input.entries) |entry| {
        sum += processEntry(entry);
    }
    std.debug.print("sum: {}\n", .{sum});
}

fn processEntry(entry: Entry) u16 {
    var observationBitmasks: [10]u7 = undefined;
    var numberBitmasks: [4]u7 = undefined;
    var i: u4 = 0;
    while (i < 14) : (i += 1) {
        const observation = if (i < 10) entry.observations[i] else entry.numbers[i-10];
        var bitmask: u7 = 0;
        if (std.mem.indexOfScalar(u8, observation, 'a') != null) { bitmask |= 0b0000001; }
        if (std.mem.indexOfScalar(u8, observation, 'b') != null) { bitmask |= 0b0000010; }
        if (std.mem.indexOfScalar(u8, observation, 'c') != null) { bitmask |= 0b0000100; }
        if (std.mem.indexOfScalar(u8, observation, 'd') != null) { bitmask |= 0b0001000; }
        if (std.mem.indexOfScalar(u8, observation, 'e') != null) { bitmask |= 0b0010000; }
        if (std.mem.indexOfScalar(u8, observation, 'f') != null) { bitmask |= 0b0100000; }
        if (std.mem.indexOfScalar(u8, observation, 'g') != null) { bitmask |= 0b1000000; }

        if (i < 10) {
            observationBitmasks[i] = bitmask;
        } else {
            numberBitmasks[i - 10] = bitmask;
        }
    }

    const easyBitmasks: [4]u7 = getBitmasksFor1478(&observationBitmasks);

    const d1 = easyBitmasks[0];
    const d4 = easyBitmasks[1];
    const d7 = easyBitmasks[2];
    const d8 = easyBitmasks[3];

    const d3 = getD3(&observationBitmasks, d1);
    const d2 = getD2(&observationBitmasks, d3, d4);
    const d5 = getD5(&observationBitmasks, d2, d3);
    const d9 = getD9(&observationBitmasks, d4);
    const d6 = getD6(&observationBitmasks, d5, d9);

    var result: u16 = 0;
    for (numberBitmasks) |numBm| {
        result *= 10;
        if (numBm == d1) { result += 1; }
        if (numBm == d2) { result += 2; }
        if (numBm == d3) { result += 3; }
        if (numBm == d4) { result += 4; }
        if (numBm == d5) { result += 5; }
        if (numBm == d6) { result += 6; }
        if (numBm == d7) { result += 7; }
        if (numBm == d8) { result += 8; }
        if (numBm == d9) { result += 9; }
    }

    return result;
}

fn getD6(bitmasks: []u7, d5: u7, d9: u7) u7 {
    for (bitmasks) |bm| {
        if (getBitCount(bm) == 6 and bm != d9 and bm & d5 == d5) {
            return bm;
        }
    }
    unreachable;
}

fn getD9(bitmasks: []u7, d4: u7) u7 {
    for (bitmasks) |bm| {
        if (getBitCount(bm) == 6 and getBitCount(bm & ~d4) == 2) {
            return bm;
        }
    }
    unreachable;
}

fn getD5(bitmasks: []u7, d2: u7, d3: u7) u7 {
    for (bitmasks) |bm| {
        if (getBitCount(bm) == 5 and bm != d2 and bm != d3) {
            return bm;
        }
    }
    unreachable;
}

fn getD2(bitmasks: []u7, d3: u7, d4: u7) u7 {
    for (bitmasks) |bm| {
        if (getBitCount(bm) == 5 and bm != d3 and getBitCount(bm & ~d4) == 3) {
            return bm;
        }
    }
    unreachable;
}

fn getD3(bitmasks: []u7, d1: u7) u7 {
    for (bitmasks) |bitmask| {
        if (getBitCount(bitmask) == 5 and getBitCount(bitmask & ~d1) == 3) {
            return bitmask;
        }
    }
    unreachable;
}

fn getBitmasksFor1478(bitmasks: []u7) [4]u7 {
    var result: [4]u7 = undefined;
    for (bitmasks) |bitmask| {
        var count = getBitCount(bitmask);
        _ = switch(count) {
            2 => result[0] = bitmask,
            4 => result[1] = bitmask,
            3 => result[2] = bitmask,
            7 => result[3] = bitmask,
            else => null,
        };
    }
    return result;
}

fn getBitCount(bitmask: u7) u3 {
    var count: u3 = 0;
    if (bitmask & 0b0000001 != 0) { count += 1; }
    if (bitmask & 0b0000010 != 0) { count += 1; }
    if (bitmask & 0b0000100 != 0) { count += 1; }
    if (bitmask & 0b0001000 != 0) { count += 1; }
    if (bitmask & 0b0010000 != 0) { count += 1; }
    if (bitmask & 0b0100000 != 0) { count += 1; }
    if (bitmask & 0b1000000 != 0) { count += 1; }
    return count;
}
