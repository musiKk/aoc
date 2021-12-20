const std = @import("std");
const input = @import("day20.input.zig");

const Coord = struct { x: i16, y: i16 };

pub fn main() !void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    const boundary: i16 = 55;
    const minX: i16 = -boundary;
    const minY: i16 = -boundary;
    const maxX: i16 = input.inputImage[0].len + boundary;
    const maxY: i16 = input.inputImage.len + boundary;

    var map = try imageToMap(&input.inputImage, minX, maxX, minY, maxY, arena.allocator());
    // dumpMap(map, minX, maxX, minY, maxY);

    var i: u8 = 0;
    while (i < 50) : (i += 1) {
        var newMap = try iterate(map, minX, maxX, minY, maxY, arena.allocator());
        map.deinit();
        map = newMap;

        // std.debug.print("map after iteration {}\n", .{i + 1});
        // dumpMap(map, minX, maxX, minY, maxY);
        // std.debug.print("\n", .{});

    }

    std.debug.print("count: {}\n", .{countPixels(map)});

}

fn countPixels(map: std.AutoHashMap(Coord, bool)) u16 {
    var count: u16 = 0;
    var vIt = map.valueIterator();
    while (vIt.next()) |v| {
        if (v.*) {
            count += 1;
        }
    }
    return count;
}

fn dumpMap(map: std.AutoHashMap(Coord, bool), minX: i16, maxX: i16, minY: i16, maxY: i16) void {
    var y = minY;
    while (y < maxY) : (y += 1) {
        var x = minX;
        while (x < maxX) : (x += 1) {
            const lit = map.get(Coord { .x = x, .y = y }).?;
            if (lit) {
                std.debug.print("#", .{});
            } else {
                std.debug.print(".", .{});
            }
        }
        std.debug.print("\n", .{});
    }
}

fn iterate(map: std.AutoHashMap(Coord, bool), minX: i16, maxX: i16, minY: i16, maxY: i16, alloc: std.mem.Allocator) !std.AutoHashMap(Coord, bool) {
    var newMap = std.AutoHashMap(Coord, bool).init(alloc);
    var y: i16 = minY;
    while (y < maxY) : (y += 1) {
        var x: i16 = minX;
        while (x < maxX) : (x += 1) {
            const newPixelLit = isNewPixelLit(map, x, y);
            try newMap.put(Coord { .x = x, .y = y }, newPixelLit);
        }
    }
    return newMap;
}

fn isNewPixelLit(map: std.AutoHashMap(Coord, bool), x: i16, y: i16) bool {
    const b0: u8 = if(map.get(Coord { .x = x - 1, .y = y - 1 }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;
    const b1: u8 = if(map.get(Coord { .x = x    , .y = y - 1 }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;
    const b2: u8 = if(map.get(Coord { .x = x + 1, .y = y - 1 }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;
    const b3: u8 = if(map.get(Coord { .x = x - 1, .y = y     }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;
    const b4: u8 = if(map.get(Coord { .x = x    , .y = y     }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;
    const b5: u8 = if(map.get(Coord { .x = x + 1, .y = y     }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;
    const b6: u8 = if(map.get(Coord { .x = x - 1, .y = y + 1 }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;
    const b7: u8 = if(map.get(Coord { .x = x    , .y = y + 1 }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;
    const b8: u8 = if(map.get(Coord { .x = x + 1, .y = y + 1 }) orelse map.get(Coord { .x = x, .y = y }).?) 1 else 0;

    var index: u9 = 0;
    index |= b0;
    index <<= 1; index |= b1;
    index <<= 1; index |= b2;
    index <<= 1; index |= b3;
    index <<= 1; index |= b4;
    index <<= 1; index |= b5;
    index <<= 1; index |= b6;
    index <<= 1; index |= b7;
    index <<= 1; index |= b8;

    return input.imageEnhancement[index] == '#';
}

fn imageToMap(image: []const[]const u8, minX: i16, maxX: i16, minY: i16, maxY: i16, alloc: std.mem.Allocator) !std.AutoHashMap(Coord, bool) {
    var map = std.AutoHashMap(Coord, bool).init(alloc);

    var y: i16 = minY;
    while (y < maxY) : (y += 1) {
        var x: i16 = minX;
        while (x < maxX) : (x += 1) {
            if (x < 0 or y < 0 or x >= image[0].len or y >= image.len) {
                try map.put(Coord { .x = x, .y = y }, false);
            } else if (image[@intCast(u16, y)][@intCast(u16, x)] == '#') {
                try map.put(Coord { .x = x, .y = y }, true);
            } else {
                try map.put(Coord { .x = x, .y = y }, false);
            }
        }
    }


    return map;
}
