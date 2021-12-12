const std = @import("std");
const data = @import("day11.input.zig").data;

pub fn main() !void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    try solve(true, arena.allocator());
    try solve(false, arena.allocator());
}

fn solve(part1: bool, alloc: std.mem.Allocator) !void {
    var currData: [10][10]u8 = undefined;
    var i: u32 = 0;
    while (i < data.len) : (i += 1) {
        std.mem.copy(u8, &currData[i], &data[i]);
    }

    const Coord = struct { x: u8, y: u8 };

    var totalFlashes: u16 = 0;

    i = 0;
    var limit: u32 = if (part1) 100 else 100000;
    while (i < limit) : (i += 1) {
        var alreadyFlashed = std.AutoHashMap(Coord, bool).init(alloc);
        defer alreadyFlashed.deinit();
        var y: u8 = 0;
        while (y < 10) : (y += 1) {
            var x: u8 = 0;
            while (x < 10) : (x += 1) {
                currData[y][x] += 1;
            }
        }
        var someoneFlashed = true;
        while (someoneFlashed) {
            someoneFlashed = false;
            y = 0;
            while (y < 10) : (y += 1) {
                var x: u8 = 0;
                while (x < 10) : (x += 1) {
                    const coord = Coord { .x = x, .y = y };
                    if (currData[y][x] > 9 and alreadyFlashed.get(coord) == null) {
                        try alreadyFlashed.put(coord, true);
                        someoneFlashed = true;
                        totalFlashes += 1;

                        for ([_][2] i2 {
                            [_]i2 {-1, 0}, [_]i2 {1, 0}, [_]i2 {0, -1}, [_]i2 {0, 1},
                            [_]i2 {-1, -1}, [_]i2 {1, 1}, [_]i2 {1, -1}, [_]i2 {-1, 1},
                            }) |offsets| {
                            const candX = @as(i9, x) + offsets[0];
                            const candY = @as(i9, y) + offsets[1];
                            if (candX < 0 or candY < 0 or candX > 9 or candY > 9) {
                                continue;
                            }
                            currData[@intCast(u8, candY)][@intCast(u8, candX)] += 1;
                        }
                    }
                }
            }
        }
        y = 0;
        while (y < 10) : (y += 1) {
            var x: u8 = 0;
            while (x < 10) : (x += 1) {
                if (currData[y][x] > 9) {
                    currData[y][x] = 0;
                }
            }
        }
        std.debug.print("{d: >4} -> {} flashes\n", .{i, alreadyFlashed.count()});
        if (!part1) {
            if (alreadyFlashed.count() == 10*10) {
                std.debug.print("all flashed after step {}\n", .{i});
                return;
            }
        }
    }
    std.debug.print("total flashes: {}\n", .{totalFlashes});
}
