const std = @import("std");
const input = @import("day14.input.zig");

const RuleCounts = [input.rules.len]u64;

const Rule = input.Rule;

pub fn main() !void {
    // var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    // defer arena.deinit();

    // try part1(arena.allocator());
    var ruleCounts = ruleCountsFromInput();
    std.debug.print("{any}\n", .{ruleCounts});
    ruleCounts = iterateRuleCounts(ruleCounts, 40);
    dumpRuleCounts(ruleCounts);
    evaluateRuleCounts(ruleCounts);
}

fn dumpRuleCounts(counts: RuleCounts) void {
    var i: u8 = 0;
    while (i < counts.len) : (i += 1) {
        std.debug.print("{s} -> {}\n", .{ input.rules[i].pair, counts[i] });
    }
}

fn evaluateRuleCounts(counts: RuleCounts) void {
    var letterCounts: [26]u64 = [_]u64{0} ** 26;
    var i: u8 = 0;
    while (i < counts.len) : (i += 1) {
        letterCounts[input.rules[i].pair[0] - 'A'] += counts[i];
        letterCounts[input.rules[i].pair[1] - 'A'] += counts[i];
    }
    for (letterCounts) |*count| {
        count.* = (count.* + 1) / 2;
    }
    var minCount: u64 = std.math.maxInt(u64);
    var maxCount: u64 = 0;
    for (letterCounts) |count| {
        if (count != 0) {
            minCount = std.math.min(count, minCount);
        }
        maxCount = std.math.max(count, maxCount);
    }
    std.debug.print("{} - {} = {}\n", .{maxCount, minCount, maxCount - minCount});
}

fn iterateRuleCounts(counts: RuleCounts, maxI: u8) RuleCounts {
    var currCounts = counts;
    var i: u8 = 0;
    while (i < maxI) : (i += 1) {
        currCounts = applyRuleCounts(currCounts);
        std.debug.print("{} - {any}\n", .{i, currCounts});
    }
    return currCounts;
}

fn initRuleCounts() RuleCounts {
    return [_]u64{0} ** input.rules.len;
}

fn ruleCountsFromInput() RuleCounts {
    var counts: RuleCounts = initRuleCounts();
    var i: u8 = 0;
    while (i < input.polymer.len - 1) : (i += 1) {
        const c1 = input.polymer[i];
        const c2 = input.polymer[i + 1];

        var ruleI: u8 = 0;
        while (ruleI < input.rules.len) : (ruleI += 1) {
            const rule = input.rules[ruleI];
            if (rule.pair[0] == c1 and rule.pair[1] == c2) {
                counts[ruleI] += 1;
                break;
            }
        }
    }
    return counts;
}

fn applyRuleCounts(counts: RuleCounts) RuleCounts {
    var newRuleCounts = initRuleCounts();

    var ruleCountI: u8 = 0;
    while (ruleCountI < counts.len) : (ruleCountI += 1) {
        const ruleCount = counts[ruleCountI];
        if (ruleCount == 0) {
            continue;
        }

        const betweenChar = input.rules[ruleCountI].insert;
        const fromRule = input.rules[ruleCountI].pair;
        var ruleI: u8 = 0;
        while (ruleI < input.rules.len) : (ruleI += 1) {
            const rule = input.rules[ruleI];
            if (rule.pair[0] == fromRule[0] and rule.pair[1] == betweenChar) {
                newRuleCounts[ruleI] += ruleCount;
            } else if (rule.pair[0] == betweenChar and rule.pair[1] == fromRule[1]) {
                newRuleCounts[ruleI] += ruleCount;
            }
        }
    }

    return newRuleCounts;
}

fn part1(alloc: std.mem.Allocator) !void {
    var i: u8 = 0;
    var start: []const u8 = input.polymer;
    std.debug.print("first: {}\n", .{start[0]});
    while (i < 10) : (i += 1) {
        const result = try applyRule(start, alloc);
        start = result.items;
    }

    var letter_counts: [26]u32 = [_]u32{0} ** 26;
    for (start) |c| {
        const index = c - 'A';
        letter_counts[index] += 1;
    }
    var minCount: u32 = std.math.maxInt(u32);
    var maxCount: u32 = 0;
    for (letter_counts) |count| {
        if (count != 0) {
            minCount = std.math.min(count, minCount);
        }
        maxCount = std.math.max(count, maxCount);
    }
    std.debug.print("{} - {} = {}\n", .{maxCount, minCount, maxCount - minCount});
}

fn applyRule(start: []const u8, alloc: std.mem.Allocator) !std.ArrayList(u8) {
    var list = std.ArrayList(u8).init(alloc);

    var last: u8 = 0;

    for (start) |c| {
        if (last != 0) {
            try list.append(last);
        }
        for (input.rules) |rule| {
            if (rule.pair[0] == last and rule.pair[1] == c) {
                // std.debug.print("expanding with rule {s} -> {c}\n", .{rule.pair, rule.insert});
                try list.append(rule.insert);

                break;
            }
        }
        last = c;
    }
    try list.append(last);
    return list;
}
