const std = @import("std");

const Neighbors = std.BoundedArray(*Node, 3);

const Node = struct {
    neighbors: Neighbors = undefined,
    name: []const u8,
    houseOf: ?u8 = null,
    restAllowed: bool = true,
    index: u8,

    fn init(name: []const u8, i: u8) !Node {
        return Node { .name = name, .neighbors = try Neighbors.init(0), .index = i };
    }

    pub fn format(
        self: Node,
        comptime fmt: []const u8,
        options: std.fmt.FormatOptions,
        writer: anytype,
    ) !void {
        _ = fmt;
        _ = options;

        try writer.print("Node({s}, index={})", .{ self.name, self.index });
    }
};

// const State = struct {
//     l1: ?u8 = null,
//     l2: ?u8 = null,
//     m1: ?u8 = null,
//     m2: ?u8 = null,
//     m3: ?u8 = null,
//     r1: ?u8 = null,
//     r2: ?u8 = null,

//     a1: ?u8 = null, a2: ?u8 = null,
//     b1: ?u8 = null, b2: ?u8 = null,
//     c1: ?u8 = null, c2: ?u8 = null,
//     d1: ?u8 = null, d2: ?u8 = null,
// };

const A: u8 = 'A';
const B: u8 = 'B';
const C: u8 = 'C';
const D: u8 = 'D';

pub fn main() !void {
    // var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    // defer arena.deinit();

    // const input = [_][]const u8 {
    //     "CBAD", "BCDA"
    // };
    // _ = input;

    var l1 = try Node.init("l1", 0);
    var l2 = try Node.init("l2", 1);
    var m1 = try Node.init("m1", 3);
    var m2 = try Node.init("m2", 5);
    var m3 = try Node.init("m3", 7);
    var r1 = try Node.init("r1", 8);
    var r2 = try Node.init("r2", 9);

    var fa = try Node.init("fa", 2); fa.restAllowed = false;
    var fb = try Node.init("fb", 4); fb.restAllowed = false;
    var fc = try Node.init("fc", 6); fc.restAllowed = false;
    var fd = try Node.init("fd", 8); fd.restAllowed = false;

    var a1 = try Node.init("a1", 11); a1.houseOf = A;
    var a2 = try Node.init("a1", 15); a2.houseOf = A;
    var b1 = try Node.init("b1", 12); b1.houseOf = B;
    var b2 = try Node.init("b2", 16); b2.houseOf = B;
    var c1 = try Node.init("c1", 13); c1.houseOf = C;
    var c2 = try Node.init("c2", 17); c2.houseOf = C;
    var d1 = try Node.init("d1", 14); d1.houseOf = D;
    var d2 = try Node.init("d2", 18); d2.houseOf = D;

    // left 2
    try neighborize(&l1, &l2);

    // right 2
    try neighborize(&r1, &r2);

    // middle row
    try neighborize(&l2, &fa);
    try neighborize(&fa, &m1);
    try neighborize(&m1, &fb);
    try neighborize(&fb, &m2);
    try neighborize(&m2, &fc);
    try neighborize(&fc, &m3);
    try neighborize(&m3, &fd);
    try neighborize(&fd, &r1);

    // homes
    try neighborize(&a1, &a2);
    try neighborize(&b1, &b2);
    try neighborize(&c1, &c2);
    try neighborize(&d1, &d2);

    // homes to middle
    try neighborize(&a1, &fa);
    try neighborize(&b1, &fb);
    try neighborize(&c1, &fc);
    try neighborize(&d1, &fd);

    // test input:
    // BCBD ADCA

    const spots = Spots {
        .l1 = &l1,
        .l2 = &l2,
        .r1 = &r1,
        .r2 = &r2,
        .m1 = &m1,
        .m2 = &m2,
        .m3 = &m3,
        .a1 = &a1,
        .a2 = &a2,
        .b1 = &b1,
        .b2 = &b2,
        .c1 = &c1,
        .c2 = &c2,
        .d1 = &d1,
        .d2 = &d2,
    };

    var state: State = .{};
    state.spots[a1.index] = B; state.amphiB1 = &a1;
    state.spots[b1.index] = C; state.amphiC1 = &b1;
    state.spots[c1.index] = B; state.amphiB2 = &c1;
    state.spots[d1.index] = D; state.amphiD1 = &d1;
    state.spots[a2.index] = A; state.amphiA1 = &a2;
    state.spots[b2.index] = D; state.amphiD2 = &b2;
    state.spots[c2.index] = C; state.amphiC2 = &c2;
    state.spots[d2.index] = A; state.amphiA2 = &d2;

    // recurse(state);
    const targetList = getPossibleTargets(state, spots, state.amphiB2);
    for (targetList.constSlice()) |target| {
        std.debug.print("{s}\n", .{target});
    }
}

// fn recurse(state: State) void {
//     const aTargets = getPossibleTargets(state, state.a1);
// }

fn getPossibleTargets(state: State, spots: Spots, node: *Node) TargetList {
    var result = TargetList.init(0) catch unreachable;

    var seen = std.BoundedArray(*Node, 19).init(0) catch unreachable;

    const nodeIsA = node == spots.a1 or node == spots.a2;
    const nodeIsB = node == spots.b1 or node == spots.b2;
    const nodeIsC = node == spots.c1 or node == spots.c2;
    const nodeIsD = node == spots.d1 or node == spots.d2;
    const nodeIsHome = nodeIsA or nodeIsB or nodeIsC or nodeIsD;

    var allowedTargets = std.BoundedArray(*Node, 10).init(0) catch unreachable;
    if (state.spots[node.index] == A) {
        // looking at A amphi
        if (node == spots.a2) {
            // already home
            return result;
        } else if (node == spots.a1) {
            if(state.spots[spots.a2.index] == A) {
                // both home
                return result;
            } else {
                // home but someone must get out first
                allowedTargets.append(spots.l1) catch unreachable;
                allowedTargets.append(spots.l2) catch unreachable;
                allowedTargets.append(spots.r1) catch unreachable;
                allowedTargets.append(spots.r2) catch unreachable;
                allowedTargets.append(spots.m1) catch unreachable;
                allowedTargets.append(spots.m2) catch unreachable;
                allowedTargets.append(spots.m3) catch unreachable;
            }
        } else {
            // currently out
            if (state.spots[spots.a2.index] == A) {
                // can only get second spot now
                allowedTargets.append(spots.a1) catch unreachable;
            } else {
                // must get first spot first
                allowedTargets.append(spots.a2) catch unreachable;
            }
        }
    }
    if (state.spots[node.index] == B) {
        // looking at B amphi
        if (node == spots.b2) {
            // already home
            return result;
        } else if (node == spots.b1) {
            if(state.spots[spots.b2.index] == B) {
                // both home
                return result;
            } else {
                // home but someone must get out first
                allowedTargets.append(spots.l1) catch unreachable;
                allowedTargets.append(spots.l2) catch unreachable;
                allowedTargets.append(spots.r1) catch unreachable;
                allowedTargets.append(spots.r2) catch unreachable;
                allowedTargets.append(spots.m1) catch unreachable;
                allowedTargets.append(spots.m2) catch unreachable;
                allowedTargets.append(spots.m3) catch unreachable;
            }
        } else {
            // currently out
            if (state.spots[spots.b2.index] == B) {
                // can only get second spot now
                allowedTargets.append(spots.b1) catch unreachable;
            } else {
                // must get first spot first
                allowedTargets.append(spots.b2) catch unreachable;
            }
        }
    }
    if (state.spots[node.index] == A) {
        // looking at A amphi
        if (node == spots.a2) {
            // already home
            return result;
        } else if (node == spots.a1) {
            if(state.spots[spots.a2.index] == A) {
                // both home
                return result;
            } else {
                // home but someone must get out first
                allowedTargets.append(spots.l1) catch unreachable;
                allowedTargets.append(spots.l2) catch unreachable;
                allowedTargets.append(spots.r1) catch unreachable;
                allowedTargets.append(spots.r2) catch unreachable;
                allowedTargets.append(spots.m1) catch unreachable;
                allowedTargets.append(spots.m2) catch unreachable;
                allowedTargets.append(spots.m3) catch unreachable;
            }
        } else {
            // currently out
            if (state.spots[spots.a2.index] == A) {
                // can only get second spot now
                allowedTargets.append(spots.a1) catch unreachable;
            } else {
                // must get first spot first
                allowedTargets.append(spots.a2) catch unreachable;
            }
        }
    }
    if (state.spots[node.index] == A) {
        // looking at A amphi
        if (node == spots.a2) {
            // already home
            return result;
        } else if (node == spots.a1) {
            if(state.spots[spots.a2.index] == A) {
                // both home
                return result;
            } else {
                // home but someone must get out first
                allowedTargets.append(spots.l1) catch unreachable;
                allowedTargets.append(spots.l2) catch unreachable;
                allowedTargets.append(spots.r1) catch unreachable;
                allowedTargets.append(spots.r2) catch unreachable;
                allowedTargets.append(spots.m1) catch unreachable;
                allowedTargets.append(spots.m2) catch unreachable;
                allowedTargets.append(spots.m3) catch unreachable;
            }
        } else {
            // currently out
            if (state.spots[spots.a2.index] == A) {
                // can only get second spot now
                allowedTargets.append(spots.a1) catch unreachable;
            } else {
                // must get first spot first
                allowedTargets.append(spots.a2) catch unreachable;
            }
        }
    }

    recursePossibleTargets(&state, .{ .node = node, .distance = 0 }, &result, allowedTargets.constSlice(), &seen);

    return result;
}

const PAD = "                         ";

fn recursePossibleTargets(
        state: *const State,
        current: NodeWithDistance,
        result: *TargetList,
        allowedTargets: []const Node,
        seen: *std.BoundedArray(*Node, 19)) void {
    // std.debug.print("{s}currently at {s}\n", .{PAD[0..current.distance * 2], current});
    for (current.node.neighbors.constSlice()) |neighbor| {
        if (std.mem.indexOfScalar(*Node, seen.constSlice(), neighbor) != null) {
            // already seen
            continue;
        }
        seen.append(neighbor) catch unreachable;

        // std.debug.print("{s} - looking at neighbor {s} => {s}\n", .{PAD[0..current.distance * 2], current.node, neighbor});
        if (state.spots[neighbor.index] != 0) {
            // neighbor already occupied
            continue;
        }

        // std.debug.print("{s} => neighbor of {s} is not occupied so move to it\n", .{ PAD[0..current.distance * 2], current });
        var newNode = NodeWithDistance { .node = neighbor, .distance = current.distance + 1 };
        if (neighbor.restAllowed) {
            result.append(newNode) catch unreachable;
        }
        recursePossibleTargets(state, newNode, result, seen);
    }
}

const TargetList = std.BoundedArray(NodeWithDistance, 13);

const NodeWithDistance = struct {
    node: *Node,
    distance: u8,
};

const State = struct {
    spots: [19]u8 = [_]u8 {0} ** 19,
    amphiA1: *Node = undefined,
    amphiA2: *Node = undefined,
    amphiB1: *Node = undefined,
    amphiB2: *Node = undefined,
    amphiC1: *Node = undefined,
    amphiC2: *Node = undefined,
    amphiD1: *Node = undefined,
    amphiD2: *Node = undefined,
};

const Spots = struct {
    l1: *Node,
    l2: *Node,
    r1: *Node,
    r2: *Node,
    m1: *Node,
    m2: *Node,
    m3: *Node,
    a1: *Node,
    a2: *Node,
    b1: *Node,
    b2: *Node,
    c1: *Node,
    c2: *Node,
    d1: *Node,
    d2: *Node,
}

fn neighborize(n1: *Node, n2: *Node) !void {
    try n1.*.neighbors.append(n2);
    try n2.*.neighbors.append(n1);
}
