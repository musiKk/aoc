const std = @import("std");

const testFishes = [_]u4{3,4,3,1,2};
const myFishes = [_]u4{1,3,4,1,1,1,1,1,1,1,1,2,2,1,4,2,4,1,1,1,1,1,5,4,1,1,2,1,1,1,1,4,1,1,1,4,4,1,1,1,1,1,1,1,2,4,1,3,1,1,2,1,2,1,1,4,1,1,1,4,3,1,3,1,5,1,1,3,4,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,5,2,5,5,3,2,1,5,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,5,1,1,1,1,5,1,1,1,1,1,4,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,3,1,2,4,1,5,5,1,1,5,3,4,4,4,1,1,1,2,1,1,1,1,1,1,2,1,1,1,1,1,1,5,3,1,4,1,1,2,2,1,2,2,5,1,1,1,2,1,1,1,1,3,4,5,1,2,1,1,1,1,1,5,2,1,1,1,1,1,1,5,1,1,1,1,1,1,1,5,1,4,1,5,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,5,4,5,1,1,1,1,1,1,1,5,1,1,3,1,1,1,3,1,4,2,1,5,1,3,5,5,2,1,3,1,1,1,1,1,3,1,3,1,1,2,4,3,1,4,2,2,1,1,1,1,1,1,1,5,2,1,1,1,2};

pub fn main() void {
    const start = std.time.nanoTimestamp();
    //                       0  1  2  3  4  5  6  7  8
    // var currFishes = [_]u64{ 0, 1, 1, 2, 1, 0, 0, 0, 0 };
    var currFishes = getFishCounts(&myFishes);

    var i: u9 = 0;
    while (i < 256) {
        var newFishes = [_]u64{
            currFishes[1],                 // 0
            currFishes[2],                 // 1
            currFishes[3],                 // 2
            currFishes[4],                 // 3
            currFishes[5],                 // 4
            currFishes[6],                 // 5
            currFishes[7] + currFishes[0], // 6
            currFishes[8],                 // 7
            currFishes[0],                 // 8
        };
        currFishes = newFishes;

        i += 1;
    }
    var sum: u64 = 0;
    for (currFishes) |cnt| { sum += cnt; }
    std.debug.print("{any} (total: {})\n", .{currFishes, sum});
    const nanos = std.time.nanoTimestamp() - start;
    std.debug.print("took {}ns, {d:.2}ms\n", .{nanos, @intToFloat(f64, nanos) / 1000000});
}

fn getFishCounts(fishes: []const u4) [9]u64 {
    var counts = [_]u64{ 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    for (fishes) |fish| { counts[fish] += 1; }
    return counts;
}
