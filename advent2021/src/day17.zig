const std = @import("std");

const Target = struct {
    minX: i32,
    maxX: i32,
    minY: i32,
    maxY: i32,
};

const Velocity = struct { x: i32, y: i32 };

pub fn main() void {
    // test
    // const target = Target { .minX = 20, .maxX = 30, .minY = -10, .maxY = -5};
    // input
    const target = Target { .minX = 34, .maxX = 67, .minY = -215, .maxY = -186};

    part1And2(target);
}

fn part1And2(target: Target) void {
    var maxHeight: i32 = 0;
    var x: i32 = 0;
    var hits: u32 = 0;
    while (x < 10000) : (x += 1) {
        var y: i32 = -1000;
        std.debug.print("x: {}\n", .{x});
        while (y < 10000) : (y += 1) {
            if (shootProbe(target, Velocity { .x = x, .y = y })) |v| {
                maxHeight = std.math.max(maxHeight, v);
                hits += 1;
            }
        }
    }
    std.debug.print("max height: {}, hits: {}\n", .{maxHeight, hits});
}

fn shootProbe(target: Target, initialVelocity: Velocity) ?i32 {
    var x: i32 = 0;
    var y: i32 = 0;
    var maxY: i32 = 0;
    var velocity = initialVelocity;
    var didHitTarget = false;
    while (x <= target.maxX and y >= target.minY) {
        x += velocity.x;
        y += velocity.y;

        // std.debug.print("i'm at {} {}\n", .{x, y});

        if (velocity.x > 0) { velocity.x -= 1; } else if (velocity.x < 0) { velocity.x += 1; }
        velocity.y -= 1;

        if (x >= target.minX and x <= target.maxX and y >= target.minY and y <= target.maxY) {
            didHitTarget = true;
            break;
        }
        maxY = std.math.max(maxY, y);
    }
    if (didHitTarget) {
        return maxY;
    } else {
        return null;
    }
}
