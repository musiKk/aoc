const std = @import("std");

const Cell = enum { right, down, empty, };

pub fn main() !void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    const input = try readInput(arena.allocator());
    dumpGrid(input);

    var step: u16 = 0;
    while (true) {
        step += 1;

        const count = evolveGrid(input);
        dumpGrid(input);
        std.debug.print("evolved, {} moves\n\n", .{count});

        if (count == 0) {
            break;
        }
    }

    std.debug.print("took {} steps\n", .{step});
}

fn evolveGrid(grid: [][]Cell) u16 {
    var moves: u16 = 0;

    const Coord = struct { x: u16, y: u16, xTo: u16, yTo: u16, };
    var moving_coords = std.BoundedArray(Coord, 10000).init(0) catch unreachable;

    var y: u16 = 0;
    while (y < grid.len) : (y += 1) {
        var x: u16 = 0;
        const row = grid[y];
        while (x < row.len) : (x += 1) {
            switch(row[x]) {
                .right => {
                    const xCand = (x + 1) % @intCast(u16, row.len);
                    if (grid[y][xCand] == .empty) {
                        moving_coords.append(Coord { .x = x, .y = y, .xTo = xCand, .yTo = y }) catch unreachable;
                    }
                },
                else => undefined,
            }
        }
    }

    moves += @intCast(u16, moving_coords.len);

    for (moving_coords.constSlice()) |moving_coord| {

        grid[moving_coord.yTo][moving_coord.xTo] = .right;
        grid[moving_coord.y][moving_coord.x] = .empty;
    }

    moving_coords.resize(0) catch unreachable;
    y = 0;
    while (y < grid.len) : (y += 1) {
        var x: u16 = 0;
        const row = grid[y];
        while (x < row.len) : (x += 1) {
            switch(row[x]) {
                .down => {
                    const yCand = (y + 1) % @intCast(u16, grid.len);
                    if (grid[yCand][x] == .empty) {
                        moving_coords.append(Coord { .x = x, .y = y, .xTo = x, .yTo = yCand }) catch unreachable;
                    }
                },
                else => undefined,
            }
        }
    }

    moves += @intCast(u16, moving_coords.len);

    for (moving_coords.constSlice()) |moving_coord| {
        grid[moving_coord.yTo][moving_coord.xTo] = .down;
        grid[moving_coord.y][moving_coord.x] = .empty;
    }

    return moves;
}

fn dumpGrid(grid: [][]Cell) void {
    var y: u16 = 0;
    while (y < grid.len) : (y += 1) {
        var x: u16 = 0;
        const row = grid[y];
        while (x < row.len) : (x += 1) {
            const c: u8 = switch(row[x]) {
                .right => '>',
                .down => 'v',
                .empty => '.',
            };
            std.debug.print("{c}", .{c});
        }
        std.debug.print("\n", .{});
    }
}

fn readInput(alloc: std.mem.Allocator) ![][]Cell {
    var file = try std.fs.cwd().openFile("src/day25.input", .{});
    defer file.close();

    var rows = std.ArrayList([]Cell).init(alloc);

    var buf_reader = std.io.bufferedReader(file.reader());
    var in_stream = buf_reader.reader();

    var buf: [1024]u8 = undefined;
    while (try in_stream.readUntilDelimiterOrEof(&buf, '\n')) |line| {
        var row = try alloc.alloc(Cell, line.len);
        var i: u16 = 0;
        while (i < line.len) : (i += 1) {
            const cell: Cell = switch(line[i]) {
                '>' => .right,
                'v' => .down,
                '.' => .empty,
                else => unreachable,
            };
            row[i] = cell;
        }
        try rows.append(row);
    }
    return rows.toOwnedSlice();
}
