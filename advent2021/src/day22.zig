const std = @import("std");
const input = @import("day22.input.zig");

const State = input.State;
const Range = input.Range;
const Step = input.Step;
const Block = input.Block;

const Blocks = std.ArrayList(Block);

pub fn main() !void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    try part1(arena.allocator());
    try part2(arena.allocator());
}

fn part2(alloc: std.mem.Allocator) !void {
    var cubes = std.ArrayList(Step).init(alloc);

    var stepCount: u16 = 0;
    for (input.steps) |step| {
        stepCount += 1;
        const previousCubeCount = cubes.items.len;

        if (step.state == .on) {
            try cubes.append(step);
        }
        var i: u16 = 0;
        while (i < previousCubeCount) : (i += 1) {
            // const previousStep = cubes.constSlice()[i];
            const previousStep = cubes.items[i];
            const overlappingBlock = getBlockOverlap(previousStep.block, step.block);
            if (overlappingBlock) |ob| {
                if (step.state == .on and previousStep.state == .on) {
                    // remove overlap
                    try cubes.append(Step { .state = .off, .block = ob });
                } else if (step.state == .off and previousStep.state == .off) {
                    // account for previous double removal
                    try cubes.append(Step { .state = .on, .block = ob });
                } else if (step.state == .off and previousStep.state == .on) {
                    try cubes.append(Step { .state = .off, .block = ob });
                } else if (step.state == .on and previousStep.state == .off) {
                    try cubes.append(Step { .state = .on, .block = ob });
                }
            }
        }
    }

    var sum: u64 = 0;
    for (cubes.items) |step| {
        const xd: u64 = @intCast(u64, step.block.x.to - step.block.x.from) + 1;
        const yd: u64 = @intCast(u64, step.block.y.to - step.block.y.from) + 1;
        const zd: u64 = @intCast(u64, step.block.z.to - step.block.z.from) + 1;
        if (step.state == .on) {
            sum += xd * yd * zd;
        } else {
            sum -= xd * yd * zd;
        }
    }
    std.debug.print("sum: {}\n", .{sum});
}

fn getBlockOverlap(block1: Block, block2: Block) ?Block {
    const xOverlap = getRangeOverlap(block1.x, block2.x);
    const yOverlap = getRangeOverlap(block1.y, block2.y);
    const zOverlap = getRangeOverlap(block1.z, block2.z);
    if (xOverlap) |xo| {
        if (yOverlap) |yo| {
            if (zOverlap) |zo| {
                return Block { .x = xo, .y = yo, .z = zo };
            }
        }
    }
    return null;
}

fn getRangeOverlap(range1: Range, range2: Range) ?Range {
    if (range1.from >= range2.from and range1.from <= range2.to) {
        return Range { .from = range1.from, .to = std.math.min(range1.to, range2.to) };
    } else if (range2.from >= range1.from and range2.from <= range1.to) {
        return Range { .from = range2.from, .to = std.math.min(range1.to, range2.to) };
    } else {
        return null;
    }
}

fn part1(alloc: std.mem.Allocator) !void {
    const Coord = struct { x: i32, y: i32, z: i32 };

    var map = std.AutoHashMap(Coord, bool).init(alloc);
    for (input.steps) |step| {
        if ((try std.math.absInt(step.block.x.from)) > 50 or try std.math.absInt(step.block.x.to) > 50
                or try std.math.absInt(step.block.y.from) > 50 or try std.math.absInt(step.block.y.to) > 50
                or try std.math.absInt(step.block.z.from) > 50 or try std.math.absInt(step.block.z.to) > 50) {
            continue;
        }
        var x: i32 = step.block.x.from;
        while (x <= step.block.x.to) : (x += 1) {
            var y: i32 = step.block.y.from;
            while (y <= step.block.y.to) : (y += 1) {
                var z: i32 = step.block.z.from;
                while (z <= step.block.z.to) : (z += 1) {
                    const c = Coord { .x = x, .y = y, .z = z };
                    // std.debug.print("turning {} coord {}\n", .{step.state, c});
                    if (step.state == .on) {
                        try map.put(c, true);
                    } else {
                        _ = map.remove(c);
                    }
                }
            }
        }
        var c: u32 = 0;
        var valueIt = map.valueIterator();
        while (valueIt.next() != null) {
            c += 1;
        }
        std.debug.print("step {} - {}\n", .{step.block, c});
    }

}
