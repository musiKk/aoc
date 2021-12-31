const std = @import("std");

const ItemType = enum { open, close, number };
const Item = union(ItemType) {
    open: void,
    close: void,
    number: u8
};
const SfNum = []Item;

pub fn main() !void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    const numbers = try readInput(arena.allocator());
    try part1(numbers, arena.allocator());
    try part2(numbers, arena.allocator());
}

fn part2(numbers: []SfNum, alloc: std.mem.Allocator) !void {
    var maxMagnitude: u16 = 0;
    var i_1: u8 = 0;
    while (i_1 < numbers.len) : (i_1 += 1) {
        var i_2: u8 = 0;
        while (i_2 < numbers.len) : (i_2 += 1) {
            if (i_1 == i_2) {
                continue;
            }
            const sum = try addSfNums(numbers[i_1], numbers[i_2], alloc);
            const reducedSum = try reduceSf(sum, alloc);
            const mag = magnitude(reducedSum);
            maxMagnitude = std.math.max(maxMagnitude, mag);
        }
    }
    std.debug.print("max magnitude: {}\n", .{maxMagnitude});
}

fn part1(numbers: []SfNum, alloc: std.mem.Allocator) !void {
    var result = numbers[0];
    for(numbers[1..]) |num| {
        const sum = try addSfNums(result, num, alloc);
        result = try reduceSf(sum, alloc);
    }
    std.debug.print("magnitude of sum: {}\n", .{magnitude(result)});
}

fn magnitude(num: SfNum) u16 {
    var i: u16 = 0;
    return _magnitude(num, &i);
}

fn _magnitude(num: SfNum, i: *u16) u16 {
    while (i.* < num.len) {
        const item = num[i.*];
        switch (item) {
            .open => {
                i.* += 1;
                const left = _magnitude(num, i);
                const right = _magnitude(num, i);
                return left * 3 + right * 2;
            },
            .number => {
                i.* += 1;
                return item.number;
            },
            else => {}
        }
        i.* += 1;
    }
    unreachable;
}

fn explode(num: SfNum, alloc: std.mem.Allocator) !?SfNum {
    var i: u16 = 0;
    var nesting: u8 = 0;
    var lastNumIndex: ?u16 = null;
    while (i < num.len) : (i += 1) {
        const item = num[i];
        if (item == .open) {
            if (nesting == 4) {
                const explodingIndex = i;
                const left = num[i + 1];
                const right = num[i + 2];
                std.debug.assert(left == .number);
                std.debug.assert(right == .number);
                if (lastNumIndex) |lni| {
                    num[lni].number += left.number;
                }
                i += 3;
                while (i < num.len) : (i += 1) {
                    if (num[i] == .number) {
                        num[i].number += right.number;
                        break;
                    }
                }
                var newNum = std.ArrayList(Item).init(alloc);
                try newNum.appendSlice(num[0..explodingIndex]);
                try newNum.append(Item { .number = 0 });
                try newNum.appendSlice(num[explodingIndex + 4..]);
                alloc.free(num);
                return newNum.toOwnedSlice();
            } else {
                nesting += 1;
            }
        } else if (item == .close) {
            nesting -= 1;
        } else {
            lastNumIndex = i;
        }
    }
    return null;
}

fn split(num: SfNum, alloc: std.mem.Allocator) !?SfNum {
    var i: u16 = 0;
    while (i < num.len) : (i += 1) {
        const item = num[i];
        if (item == .number and item.number >= 10) {
            var newNum = std.ArrayList(Item).init(alloc);
            try newNum.appendSlice(num[0..i]);
            try newNum.append(Item { .open = {}});
            try newNum.append(Item { .number = item.number / 2});
            try newNum.append(Item { .number = item.number - (item.number / 2)});
            try newNum.append(Item { .close = {}});
            try newNum.appendSlice(num[i + 1..]);
            alloc.free(num);
            return newNum.toOwnedSlice();
        }
    }
    return null;
}

fn reduceSf(n: SfNum, alloc: std.mem.Allocator) !SfNum {
    var didReduce = true;
    var num = n;
    while (didReduce) {
        didReduce = false;
        if (try explode(num, alloc)) |nExploded| {
            // std.debug.print("did explode into\n{any}\n\n", .{nExploded});
            num = nExploded;
            didReduce = true;
        } else if (try split(num, alloc)) |nSplit| {
            // std.debug.print("did split into\n{any}\n\n", .{nSplit});
            num = nSplit;
            didReduce = true;
        }
    }
    return num;
}

fn addSfNums(n1: SfNum, n2: SfNum, alloc: std.mem.Allocator) !SfNum {
    var result = std.ArrayList(Item).init(alloc);
    try result.append(Item { .open = {}});
    try result.appendSlice(n1);
    try result.appendSlice(n2);
    try result.append(Item { .close = {}});
    return result.toOwnedSlice();
}

fn readLine(line: []const u8, alloc: std.mem.Allocator) !SfNum {
    var lineItems = std.ArrayList(Item).init(alloc);

    for (line) |c| {
        if (c == '[') {
            try lineItems.append(Item { .open = {} });
        } else if (c == ']') {
            try lineItems.append(Item { .close = {} });
        } else if (c >= '0' and c <= '9') {
            try lineItems.append(Item { .number = c - '0'});
        }
    }

    return lineItems.toOwnedSlice();
}

fn readInput(alloc: std.mem.Allocator) ![]SfNum {
    var file = try std.fs.cwd().openFile("src/day18.input", .{});
    defer file.close();

    var lines = std.ArrayList(SfNum).init(alloc);

    var buf_reader = std.io.bufferedReader(file.reader());
    var in_stream = buf_reader.reader();

    var buf: [1024]u8 = undefined;
    while (try in_stream.readUntilDelimiterOrEof(&buf, '\n')) |line| {
        try lines.append(try readLine(line, alloc));
    }
    return lines.toOwnedSlice();
}
