const std = @import("std");
const data = @import("day12.input.zig").data;

const EDGE_BUF_SIZE = 50;
const PATH_BUF_SIZE = 30;
const VIS_BUF_SIZE = 200;

pub fn main() !void {
    var edges = try std.BoundedArray(Edge, EDGE_BUF_SIZE).init(0);
    try makeEdgeList(&edges);

    const result = try part1(&edges);
    std.debug.print("part 1: {}\n", .{result});
    const result2 = try part2(&edges);
    std.debug.print("part 2: {}\n", .{result2});
}

fn part2(edges: *std.BoundedArray(Edge, EDGE_BUF_SIZE)) !u32 {
    var visited = try std.BoundedArray([]const u8, VIS_BUF_SIZE).init(0);
    try visited.append("start");
    var path = try std.BoundedArray([]const u8, PATH_BUF_SIZE).init(0);
    try path.append("start");
    return recurse2(edges, "start", &visited, &path);
}

fn recurse2(
    edges: *std.BoundedArray(Edge, EDGE_BUF_SIZE),
    currentNode: []const u8,
    visited: *std.BoundedArray([]const u8, VIS_BUF_SIZE),
    path: *std.BoundedArray([]const u8, PATH_BUF_SIZE)) anyerror!u32 {
    var numberOfWays: u32 = 0;
    for (edges.slice()) |edge| {
        if (!std.mem.eql(u8, edge.from, currentNode)) {
            continue;
        }
        if (std.mem.eql(u8, edge.to, "start")) {
            continue;
        }
        if (std.mem.eql(u8, edge.to, "end")) {
            // for (path.slice()) |p| {
            //     std.debug.print("{s},", .{p});
            // }
            // std.debug.print("end\n", .{});
            numberOfWays += 1;
            continue;
        }
        if (edge.to[0] >= 'a' and edge.to[0] <= 'z') {
            var buf: [500]u8 = undefined;
            var existingSmallCaves = std.StringHashMap(u2).init(std.heap.FixedBufferAllocator.init(&buf).allocator());
            for (visited.slice()) |v| {
                if (v[0] >= 'a' and v[0] <= 'z') {
                    if (existingSmallCaves.getPtr(v)) |existingValue| {
                        existingValue.* += 1;
                    } else {
                        try existingSmallCaves.put(v, 1);
                    }
                }
            }
            var doubleVisit: ?[]const u8 = null;
            var entryIt = existingSmallCaves.iterator();
            while (entryIt.next()) |e| {
                if (e.value_ptr.* > 1) {
                    doubleVisit = e.key_ptr.*;
                    break;
                }
            }
            if (doubleVisit != null and existingSmallCaves.contains(edge.to)) {
                continue;
            }
        }
        var newVisited = try std.BoundedArray([]const u8, VIS_BUF_SIZE).fromSlice(visited.slice());
        if (edge.to[0] >= 'a' and edge.to[0] <= 'z') {
            try newVisited.append(edge.to);
        }
        var newPath = try std.BoundedArray([]const u8, PATH_BUF_SIZE).fromSlice(path.slice());
        try newPath.append(edge.to);
        numberOfWays += try recurse2(edges, edge.to, &newVisited, &newPath);
    }
    return numberOfWays;
}

fn part1(edges: *std.BoundedArray(Edge, EDGE_BUF_SIZE)) !u16 {
    var visited = try std.BoundedArray([]const u8, VIS_BUF_SIZE).init(0);
    try visited.append("start");
    return recurse1(edges, "start", &visited);
}

fn recurse1(edges: *std.BoundedArray(Edge, EDGE_BUF_SIZE), currentNode: []const u8, visited: *std.BoundedArray([]const u8, VIS_BUF_SIZE)) anyerror!u16 {
    var numberOfWays: u16 = 0;
    outer: for (edges.slice()) |edge| {
        if (!std.mem.eql(u8, edge.from, currentNode)) {
            continue;
        }
        if (std.mem.eql(u8, edge.to, "start")) {
            continue;
        }
        if (std.mem.eql(u8, edge.to, "end")) {
            numberOfWays += 1;
            continue;
        }
        for (visited.slice()) |v| {
            if (std.mem.eql(u8, v, edge.to)) {
                continue :outer;
            }
        }
        var newVisited = try std.BoundedArray([]const u8, VIS_BUF_SIZE).fromSlice(visited.slice());
        if (edge.to[0] >= 'a' and edge.to[0] <= 'z') {
            try newVisited.append(edge.to);
        }
        numberOfWays += try recurse1(edges, edge.to, &newVisited);
    }
    return numberOfWays;
}

const Edge = struct {
    from: []const u8,
    to: []const u8,
};

fn makeEdgeList(edges: *std.BoundedArray(Edge, EDGE_BUF_SIZE)) !void {
    for (data) |entry| {
        var it = std.mem.split(u8, entry, "-");
        const v1 = it.next().?;
        const v2 = it.next().?;
        try edges.append(Edge { .from = v1, .to = v2 });
        try edges.append(Edge { .from = v2, .to = v1 });
    }
}
