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

    // try part1(arena.allocator());
    try part2(arena.allocator());
}

fn part2(alloc: std.mem.Allocator) !void {
    const Coord = struct { x: u32, y: u32, z: u32 };

    var xCoordsMap = std.AutoHashMap(i32, bool).init(alloc);
    var yCoordsMap = std.AutoHashMap(i32, bool).init(alloc);
    var zCoordsMap = std.AutoHashMap(i32, bool).init(alloc);

    for (input.steps) |step| {
        try xCoordsMap.put(step.block.x.from, true);
        try xCoordsMap.put(step.block.x.to, true);
        try xCoordsMap.put(step.block.x.to + 1, true);
        try yCoordsMap.put(step.block.y.from, true);
        try yCoordsMap.put(step.block.y.to, true);
        try yCoordsMap.put(step.block.y.to + 1, true);
        try zCoordsMap.put(step.block.z.from, true);
        try zCoordsMap.put(step.block.z.to, true);
        try zCoordsMap.put(step.block.z.to + 1, true);
    }
    var xCoords = std.BoundedArray(i32, 5000).init(0) catch unreachable;
    var yCoords = std.BoundedArray(i32, 5000).init(0) catch unreachable;
    var zCoords = std.BoundedArray(i32, 5000).init(0) catch unreachable;
    {
        var xKeyIt = xCoordsMap.keyIterator();
        while (xKeyIt.next()) |x| {
            try xCoords.append(x.*);
        }
        var yKeyIt = yCoordsMap.keyIterator();
        while (yKeyIt.next()) |y| {
            try yCoords.append(y.*);
        }
        var zKeyIt = zCoordsMap.keyIterator();
        while (zKeyIt.next()) |x| {
            try zCoords.append(x.*);
        }
    }

    std.sort.sort(i32, xCoords.slice(), {}, comptime std.sort.asc(i32));
    std.sort.sort(i32, yCoords.slice(), {}, comptime std.sort.asc(i32));
    std.sort.sort(i32, zCoords.slice(), {}, comptime std.sort.asc(i32));

    std.debug.print("x coords: {any}\n", .{xCoords.constSlice()});
    std.debug.print("y coords: {any}\n", .{yCoords.constSlice()});
    std.debug.print("z coords: {any}\n", .{zCoords.constSlice()});

    std.debug.print("{} {} {}\n", .{xCoords.len, yCoords.len, zCoords.len});

    // var xToIt: u32 = 0;
    // for (xCoords.constSlice()) |xVal| {
    //     try xToMap.put(xVal, xToIt);
    //     try xBackMap.put(xToIt, xVal);
    //     xToIt += 1;
    // }
    // var yToIt: u32 = 0;
    // for (yCoords.constSlice()) |yVal| {
    //     try yToMap.put(yVal, yToIt);
    //     try yBackMap.put(yToIt, yVal);
    //     yToIt += 1;
    // }
    // var zToIt: u32 = 0;
    // for (zCoords.constSlice()) |zVal| {
    //     try zToMap.put(zVal, zToIt);
    //     try zBackMap.put(zToIt, zVal);
    //     zToIt += 1;
    // }

    var stepIdx: u16 = 0;
    var map = std.AutoHashMap(Coord, bool).init(alloc);
    for (input.steps) |step| {
        var mappedXFrom: u32 = @intCast(u32, std.mem.indexOfScalar(i32, xCoords.constSlice(), step.block.x.from).?);
        var mappedXTo: u32 = @intCast(u32, std.mem.indexOfScalar(i32, xCoords.constSlice(), step.block.x.to).?);
        var mappedYFrom: u32 = @intCast(u32, std.mem.indexOfScalar(i32, yCoords.constSlice(), step.block.y.from).?);
        var mappedYTo: u32 = @intCast(u32, std.mem.indexOfScalar(i32, yCoords.constSlice(), step.block.y.to).?);
        var mappedZFrom: u32 = @intCast(u32, std.mem.indexOfScalar(i32, zCoords.constSlice(), step.block.z.from).?);
        var mappedZTo: u32 = @intCast(u32, std.mem.indexOfScalar(i32, zCoords.constSlice(), step.block.z.to).?);

        std.debug.print("{} - turning {} cube {} {}, {} {}, {} {}\n", .{stepIdx, step.state,
            mappedXFrom, mappedXTo,
            mappedYFrom, mappedYTo,
            mappedZFrom, mappedZTo,
        });
        stepIdx += 1;

        var x: u32 = mappedXFrom;
        while (x <= mappedXTo) : (x += 1) {
            var y: u32 = mappedYFrom;
            while (y <= mappedYTo) : (y += 1) {
                var z: u32 = mappedZFrom;
                while (z <= mappedZTo) : (z += 1) {
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
        // var c: u32 = 0;
        // var valueIt = map.valueIterator();
        // while (valueIt.next() != null) {
        //     c += 1;
        // }
        // std.debug.print("step {} - {}\n", .{step.block, c});
    }
    var c: u64 = 0;
    var keyIt = map.keyIterator();
    while (keyIt.next()) |coord| {
        // const xOrig = xCoords.constSlice()[coord.*.x..coord.*.x + 2];
        // const yOrig = yCoords.constSlice()[coord.*.y..coord.*.y + 2];
        // const zOrig = zCoords.constSlice()[coord.*.z..coord.*.z + 2];

        const x1: i32 = xCoords.constSlice()[coord.*.x];
        const x2: i32 = xCoords.constSlice()[coord.*.x + 1];
        const y1: i32 = yCoords.constSlice()[coord.*.y];
        const y2: i32 = yCoords.constSlice()[coord.*.y + 1];
        const z1: i32 = zCoords.constSlice()[coord.*.z];
        const z2: i32 = zCoords.constSlice()[coord.*.z + 1];

        const xd: u64 = std.math.absCast(x2 - x1);
        const yd: u64 = std.math.absCast(y2 - y1);
        const zd: u64 = std.math.absCast(z2 - z1);

        // std.debug.print("on: {} -> translated back into {any} {any} {any}\n", .{coord.*, xOrig, yOrig, zOrig});
        // c += std.math.absCast(xOrig[1] - xOrig[0]) * std.math.absCast(yOrig[1] - yOrig[0]) * std.math.absCast(zOrig[1] - zOrig[0]);
        c += xd * yd * zd;
    }
    std.debug.print("total count: {}\n", .{c});
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
