const std = @import("std");
const input = @import("day13.input.zig");

pub fn main() !void {
    try part1();
    try part2();
}

fn part2() !void {
    var foldedCoords = try std.BoundedArray(input.Coordinate, 1000).fromSlice(&input.coords);
    for (input.folds) |fold| {
        var foldedCoordsNew = try std.BoundedArray(input.Coordinate, 1000).init(0);
        try foldInto(&foldedCoordsNew, foldedCoords.slice(), fold);
        foldedCoords = foldedCoordsNew;
    }

    var map = std.AutoHashMap(input.Coordinate, bool).init(std.heap.page_allocator);
    var maxX: u16 = 0; var maxY: u16 = 0;
    for (foldedCoords.slice()) |c| {
        try map.put(c, true);
        if (c.x > maxX) { maxX = c.x; }
        if (c.y > maxY) { maxY = c.y; }
    }

    var y: u8 = 0;
    while (y <= maxY) : (y += 1) {
        var x: u8 = 0;
        while (x <= maxX) : (x += 1) {
            const c = input.Coordinate { .x = x, .y = y };
            if (map.get(c) != null) {
                std.debug.print("#", .{});
            } else {
                std.debug.print(" ", .{});
            }
        }
        std.debug.print("\n", .{});
    }

}

fn part1() !void {
    var foldedCoords = try std.BoundedArray(input.Coordinate, 1000).init(0);
    try foldInto(&foldedCoords, &input.coords, input.folds[0]);

    var map = std.AutoHashMap(input.Coordinate, bool).init(std.heap.page_allocator);
    for (foldedCoords.slice()) |c| {
        try map.put(c, true);
    }
    std.debug.print("coords: {}\n", .{map.count()});
}

fn foldInto(target: *std.BoundedArray(input.Coordinate, 1000), coords: []const input.Coordinate, fold: input.Fold) !void {
    for (coords) |coord| {
        const foldedCoord = foldCoord(coord, fold);
        if (foldedCoord) |c| {
            try target.append(c);
        }
    }
}

fn foldCoord(coord: input.Coordinate, fold: input.Fold) ?input.Coordinate {
    switch (fold) {
        .x => |x| {
            if (coord.x == x) {
                return null;
            }
            if (coord.x < x) {
                return coord;
            }
            return input.Coordinate { .x = 2*x - coord.x, .y = coord.y };
        },
        .y => |y| {
            if (coord.y == y) {
                return null;
            }
            if (coord.y < y) {
                return coord;
            }
            return input.Coordinate { .x = coord.x, .y = 2*y - coord.y };
        }
    }
}
