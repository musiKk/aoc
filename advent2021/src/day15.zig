const std = @import("std");
const INPUT = @import("day15.input.zig").input;
const INPUT_HEIGHT = INPUT.len;
const INPUT_WIDTH = INPUT[0].len;


const DIST_TYPE = u32;
const Coord = struct { x: u16, y: u16 };

const CoordDist = struct { coord: Coord, dist: DIST_TYPE };

fn lessThan(a: CoordDist, b: CoordDist) std.math.Order {
    const da = b.dist;
    const db = a.dist;
    if (da == db) {
        return std.math.Order.eq;
    } else if (da < db) {
        return std.math.Order.gt;
    } else {
        return std.math.Order.lt;
    }
}

pub fn main() !void {
    // var input = &INPUT;

    // part 2
    var expandedInput: [INPUT_HEIGHT*5][INPUT_WIDTH*5]u8 = undefined;

    var xx: u8 = 0;
    while (xx < 5) : (xx += 1) {
        var yy: u8 = 0;
        while (yy < 5) : (yy += 1) {
            var x: u8 = 0;
            while (x < INPUT_WIDTH) : (x +=1 ) {
                var y: u8 = 0;
                while (y < INPUT_HEIGHT) : (y += 1) {
                    const realX = xx * INPUT_WIDTH + x;
                    const realY = yy * INPUT_HEIGHT + y;
                    var val = INPUT[y][x] + xx + yy;
                    while (val > 9) {
                        // std.debug.print("wrapping {} to ", .{val});
                        val = val - 9;
                        // std.debug.print("{}\n", .{val});
                    }
                    // std.debug.print("setting {} {} ({} {}) to {}\n", .{realX, realY, x, y, val});
                    expandedInput[realY][realX] = val;
                }
            }
        }
    }
    const input = &expandedInput;
    // /part2

    const WIDTH = input.*[0].len;
    const HEIGHT = input.*.len;

    // var y_: u16 = 0;
    // while (y_ < HEIGHT) : (y_ += 1) {
    //     var x_: u16 = 0;
    //     while (x_ < WIDTH) : (x_ += 1) {
    //         std.debug.print("{}", .{input.*[y_][x_]});
    //     }
    //     std.debug.print("\n", .{});
    // }

    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    var dist = std.AutoHashMap(Coord, DIST_TYPE).init(arena.allocator());
    var q = std.PriorityQueue(CoordDist, lessThan).init(arena.allocator());
    var prev = std.AutoHashMap(Coord, Coord).init(arena.allocator());
    var visited = std.AutoHashMap(Coord, bool).init(arena.allocator());

    var y: u16 = 0;
    while (y < HEIGHT): (y += 1) {
        var x: u16 = 0;
        while (x < WIDTH) : (x += 1) {
            const coord = Coord { .x = x, .y = y };
            try dist.put(coord, std.math.maxInt(DIST_TYPE));
        }
    }
    try q.add(CoordDist { .coord = Coord { .x = 0, .y = 0 }, .dist = 0 });
    try dist.put(Coord { .x = 0, .y = 0}, 0);

    while (q.len > 0) {
        const cd = q.remove();
        const u = cd.coord;
        const uDist = cd.dist;

        if (u.x == WIDTH - 1 and u.y == HEIGHT - 1) {
            std.debug.print("found solution\n", .{});

            var n: Coord = Coord { .x = WIDTH - 1, .y = HEIGHT - 1 };
            var sum: u16 = 0;
            while (true) {
                var weight = input.*[n.y][n.x];
                sum += weight;
                std.debug.print("coord {}, {}\n", .{ n, weight });
                if (prev.get(n)) |nPrev| {
                    n = nPrev;
                } else {
                    std.debug.print("sum: {}\n", .{sum});
                    return;
                }
            }
        }

        for ([_][2] i2 { [_]i2 {-1, 0}, [_]i2 {1, 0}, [_]i2 {0, -1}, [_]i2 {0, 1}}) |offsets| {
            var candX = @as(i17, u.x) + offsets[0];
            var candY = @as(i17, u.y) + offsets[1];
            if (candX < 0 or candX >= WIDTH or candY < 0 or candY >= HEIGHT) {
                continue;
            }

            const v = Coord { .x = @intCast(u16, candX), .y = @intCast(u16, candY) };
            if (visited.get(v) != null) {
                continue;
            }
            try visited.put(v, true);

            const alt = uDist + input.*[v.y][v.x];
            if (alt < dist.get(v).?) {
                try q.add(CoordDist { .coord = v, .dist = alt });
                try dist.put(v, alt);
                try prev.put(v, u);
            }
        }
    }
}
