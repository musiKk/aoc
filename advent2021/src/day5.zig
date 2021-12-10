const std = @import("std");

const Coordinate = struct { x: u16, y: u16 };
const Coordinates = struct { start: Coordinate, end: Coordinate };


pub fn main() !void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    const coordinates = try read_input(&arena.allocator());
    defer coordinates.deinit();
    try solve(false, coordinates, &arena.allocator());
}

fn solve(part1: bool, coordinatesList: std.ArrayList(Coordinates), alloc: *std.mem.Allocator) !void {
    var map = std.AutoHashMap(Coordinate, u8).init(alloc.*);
    for (coordinatesList.items) |coordinates| {
        if (part1 and coordinates.start.x != coordinates.end.x and coordinates.start.y != coordinates.end.y) {
            continue;
        }

        const startX = coordinates.start.x;
        const startY = coordinates.start.y;
        const endX = coordinates.end.x;
        const endY = coordinates.end.y;
        var curX = startX;
        var curY = startY;
        while (true) {
            const currCor = Coordinate { .x = curX, .y = curY };
            if (map.get(currCor)) |cnt| {
                try map.put(currCor, cnt + 1);
            } else {
                try map.put(currCor, 1);
            }
            if (curX == endX and curY == endY) {
                break;
            }
            if (curX < endX) { curX += 1; } else if (curX > endX) { curX -= 1; }
            if (curY < endY) { curY += 1; } else if (curY > endY) { curY -= 1; }
        }
    }
    var mapIt = map.valueIterator();
    var overlaps: u16 = 0;
    while (mapIt.next()) |val| {
        if (val.* > 1) {
            overlaps += 1;
        }
    }
    std.debug.print("overlaps: {}\n", .{ overlaps });
}

fn read_input(alloc: *std.mem.Allocator) !std.ArrayList(Coordinates) {
    var file = try std.fs.cwd().openFile("src/day5.input", .{});
    defer file.close();

    var list = std.ArrayList(Coordinates).init(alloc.*);

    var buf_reader = std.io.bufferedReader(file.reader());
    var in_stream = buf_reader.reader();

    var buf: [1024]u8 = undefined;
    while (try in_stream.readUntilDelimiterOrEof(&buf, '\n')) |line| {
        var splitterator = std.mem.split(u8, line, " -> ");
        const firstCoord = splitterator.next().?;
        const secondCoord = splitterator.next().?;

        var splitteratorCoord1 = std.mem.split(u8, firstCoord, ",");
        var splitteratorCoord2 = std.mem.split(u8, secondCoord, ",");

        const c1x = splitteratorCoord1.next().?;
        const c1y = splitteratorCoord1.next().?;
        const c2x = splitteratorCoord2.next().?;
        const c2y = splitteratorCoord2.next().?;

        const c = Coordinates {
            .start = Coordinate {
                .x = try std.fmt.parseInt(u16, c1x, 10),
                .y = try std.fmt.parseInt(u16, c1y, 10)
            },
            .end = Coordinate {
                .x = try std.fmt.parseInt(u16, c2x, 10),
                .y = try std.fmt.parseInt(u16, c2y, 10)
            }
        };
        try list.append(c);
    }

    return list;
}
