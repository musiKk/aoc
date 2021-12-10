const std = @import("std");
const data = @import("day9.input.zig").data;

pub fn main() void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();
    part1();
    part2(&arena.allocator());
}

fn part2(alloc: *std.mem.Allocator) void {
    const height = data.len;
    const width = data[0].len;

    var sumOfRiskLevels: u16 = 0;
    _ = sumOfRiskLevels;

    var y: u16 = 0;
    while (y < height) : (y += 1) {
        var x: u16 = 0;
        while (x < width) : (x += 1) {
            const size = getBasinSize(y, x, height, width, alloc);
            std.debug.print("basin size @ {}x{} -> {}\n", .{x, y, size});
        }
    }
}

fn getBasinSize(y: u16, x: u16, height: u16, width: u16, alloc: *std.mem.Allocator) u32 {
    const Coord = struct { x: u16, y: u16 };
    var coords = std.AutoHashMap(Coord, u8).init(alloc.*);
    var c = data[y][x];
    if (c == 9) {
        return 1;
    }

    coords.put(Coord { .x = x, .y = y }, c) catch unreachable;

    var addedSomething: bool = true;
    outer: while (addedSomething) {
        addedSomething = false;

        var it = coords.iterator();
        while (it.next()) |entry| {
            const coord = entry.key_ptr.*;
            const h = entry.value_ptr.*;

            for ([_][2] i2 { [_]i2 {-1, 0}, [_]i2 {1, 0}, [_]i2 {0, -1}, [_]i2 {0, 1}}) |offsets| {
                const candX = @as(i17, coord.x) + offsets[0];
                const candY = @as(i17, coord.y) + offsets[1];
                if (candX < 0 or candX >= width or candY < 0 or candY >= height) {
                    continue;
                }
                const candHeight = data[@intCast(u16, candY)][@intCast(u16, candX)];
                if (candHeight == 9 or candHeight <= h) {
                    continue;
                }
                const candCoord = Coord { .x = @intCast(u16, candX), .y = @intCast(u16, candY) };
                if (coords.get(candCoord) != null) {
                    continue;
                }
                addedSomething = true;
                coords.put(candCoord, candHeight) catch unreachable;
                continue :outer;
            }
        }
    }

    return coords.count();
}

fn part1() void {
    const height = data.len;
    const width = data[0].len;

    var sumOfRiskLevels: u16 = 0;

    var y: u16 = 0;
    while (y < height) : (y += 1) {
        var x: u16 = 0;
        while (x < width) : (x += 1) {
            const c = data[y][x];
            if (x > 0 and data[y][x-1] <= c) {
                continue;
            }
            if (x < width - 1 and data[y][x+1] <= c) {
                continue;
            }
            if (y > 0 and data[y-1][x] <= c) {
                continue;
            }
            if (y < height - 1 and data[y+1][x] <= c) {
                continue;
            }
            sumOfRiskLevels += c + 1;
        }
    }
    std.debug.print("sum of risk levels: {}\n", .{sumOfRiskLevels});
}
