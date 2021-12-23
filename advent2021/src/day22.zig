const std = @import("std");
const input = @import("day22.input.zig");

const State = input.State;
const Range = input.Range;
const Step = input.Step;
const Block = input.Block;

const Blocks = std.ArrayList(Block);

const Coord = struct { x: i32, y: i32, z: i32 };

pub fn main() !void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    var map = std.AutoHashMap(Coord, bool).init(arena.allocator());
    for (input.steps) |step| {
        if ((try std.math.absInt(step.block.x.from)) > 50 or try std.math.absInt(step.block.x.to) > 50
                or try std.math.absInt(step.block.y.from) > 50 or try std.math.absInt(step.block.y.to) > 50
                or try std.math.absInt(step.block.z.from) > 50 or try std.math.absInt(step.block.z.to) > 50) {
            continue;
        }
        var x: i32 = step.block.x.from;
        while (x <= step.block.x.to) : (x += 1) {
            var y: i32 = step.block.y.from;
            while (y <= step.block.y.to) : (y += 1) {
                var z: i32 = step.block.z.from;
                while (z <= step.block.z.to) : (z += 1) {
                    const c = Coord { .x = x, .y = y, .z = z };
                    // std.debug.print("turning {} coord {}\n", .{step.state, c});
                    if (step.state == .on) {
                        try map.put(c, true);
                    } else {
                        _ = map.remove(c);
                    }
                }
            }
        }
        var c: u32 = 0;
        var valueIt = map.valueIterator();
        while (valueIt.next() != null) {
            c += 1;
        }
        std.debug.print("step {} - {}\n", .{step.block, c});
    }

}

// pub fn main() !void {
//     var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
//     defer arena.deinit();

//     var blocks = Blocks.init(arena.allocator());

//     for (input.steps) |step| {
//         var newBlocks = Blocks.init(arena.allocator());

//         addNewStep(blocks, newBlocks, step);

//         blocks = newBlocks;
//     }

// }

// fn addNewStep(existingBlocks: Blocks, newBlocks: Blocks, step: Step) !void {
//     for (existingBlocks) |existingBlock| {
//         if (!blocksOverlap(existingBlock, step.block)) {
//             try newBlocks.append(existingBlock);
//         } else {
//             try explodeExistingBlockInto(existingBlock, newBlocks, step);
//         }
//     }
// }

// fn explodeExistingBlockInto(existingBlock: Block, newBlocks: Blocks, step: Step) !void {
//     // splits
//     // A   A    A   A     A   A     A   A      A    A
//     // B  B      B  B    B   B       B   B      B  B

//     const blockA = existingBlock;
//     const blockB = step.block;

//     var xSplits = try std.BoundedArray(Range, 2).init(0);

//     if (blockA.x.from == blockB.x.from) {
//         if (blockA.x.to == blockB.x.to) {
//             // no range
//         } else if (blockA.x.to > blockB.x.to) {
//             try xSplits.append(Range(blockB.x.to + 1, blockA.x.to));
//         } else if (blockA.x.to < blockB.x.to)
//     }

//     if (blockA.x.from == blockB.x.from and blockA.x.to > blockB.x.to) {

//     }

// }

// fn blocksOverlap(blockA: Block, blockB: Block) bool {
//     return rangesOverlap(blockA.x, blockB.x)
//         and rangesOverlap(blockA.y, blockB.y)
//         and rangesOverlap(blockA.z, blockB.z);
// }

// fn rangesOverlap(rangeA: Range, rangeB: Range) bool {
//     return !((rangeA.to < rangeB.from) or (rangeA.from > rangeB.to));
// }
