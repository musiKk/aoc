const std = @import("std");

pub fn main() void {
    part1();
    part2();
}

fn part2() void {
    // var pos1: u8 = 4; var pos2: u8 = 8;
    var pos1: u8 = 5; var pos2: u8 = 8;

    const r = rec(pos1, pos2, 0, 0, true);
    std.debug.print("{}\n", .{r});
}

const Pair = struct { a: u64, b: u64 };

fn rec(pos1: u8, pos2: u8, score1: u8, score2: u8, p1: bool) Pair {
    var wins1: u64 = 0;
    var wins2: u64 = 0;

    if (p1) {
        var pos1_3 = pos1 + 3; if (pos1_3 > 10) { pos1_3 -= 10; }
        if (score1 + pos1_3 >= 21) {
            wins1 += 1;
        } else {
            const r = rec(pos1_3, pos2, score1 + pos1_3, score2, !p1);
            wins1 += r.a;
            wins2 += r.b;
        }

        var pos1_4 = pos1 + 4; if (pos1_4 > 10) { pos1_4 -= 10; }
        if (score1 + pos1_4 >= 21) {
            wins1 += 3;
        } else {
            const r = rec(pos1_4, pos2, score1 + pos1_4, score2, !p1);
            wins1 += 3 * r.a;
            wins2 += 3 * r.b;
        }

        var pos1_5 = pos1 + 5; if (pos1_5 > 10) { pos1_5 -= 10; }
        if (score1 + pos1_5 >= 21) {
            wins1 += 6;
        } else {
            const r = rec(pos1_5, pos2, score1 + pos1_5, score2, !p1);
            wins1 += 6 * r.a;
            wins2 += 6 * r.b;
        }
        var pos1_6 = pos1 + 6; if (pos1_6 > 10) { pos1_6 -= 10; }
        if (score1 + pos1_6 >= 21) {
            wins1 += 7;
        } else {
            const r = rec(pos1_6, pos2, score1 + pos1_6, score2, !p1);
            wins1 += 7 * r.a;
            wins2 += 7 * r.b;
        }
        var pos1_7 = pos1 + 7; if (pos1_7 > 10) { pos1_7 -= 10; }
        if (score1 + pos1_7 >= 21) {
            wins1 += 6;
        } else {
            const r = rec(pos1_7, pos2, score1 + pos1_7, score2, !p1);
            wins1 += 6 * r.a;
            wins2 += 6 * r.b;
        }
        var pos1_8 = pos1 + 8; if (pos1_8 > 10) { pos1_8 -= 10; }
        if (score1 + pos1_8 >= 21) {
            wins1 += 3;
        } else {
            const r = rec(pos1_8, pos2, score1 + pos1_8, score2, !p1);
            wins1 += 3 * r.a;
            wins2 += 3 * r.b;
        }
        var pos1_9 = pos1 + 9; if (pos1_9 > 10) { pos1_9 -= 10; }
        if (score1 + pos1_9 >= 21) {
            wins1 += 1;
        } else {
            const r = rec(pos1_9, pos2, score1 + pos1_9, score2, !p1);
            wins1 += r.a;
            wins2 += r.b;
        }
    } else {
        var pos2_3 = pos2 + 3; if (pos2_3 > 10) { pos2_3 -= 10; }
        if (score2 + pos2_3 >= 21) {
            wins2 += 1;
        } else {
            const r = rec(pos1, pos2_3, score1, score2 + pos2_3, !p1);
            wins1 += r.a;
            wins2 += r.b;
        }

        var pos2_4 = pos2 + 4; if (pos2_4 > 10) { pos2_4 -= 10; }
        if (score2 + pos2_4 >= 21) {
            wins2 += 3;
        } else {
            const r = rec(pos1, pos2_4, score1, score2 + pos2_4, !p1);
            wins1 += 3 * r.a;
            wins2 += 3 * r.b;
        }

        var pos2_5 = pos2 + 5; if (pos2_5 > 10) { pos2_5 -= 10; }
        if (score2 + pos2_5 >= 21) {
            wins2 += 6;
        } else {
            const r = rec(pos1, pos2_5, score1, score2 + pos2_5, !p1);
            wins1 += 6 * r.a;
            wins2 += 6 * r.b;
        }
        var pos2_6 = pos2 + 6; if (pos2_6 > 10) { pos2_6 -= 10; }
        if (score2 + pos2_6 >= 21) {
            wins2 += 7;
        } else {
            const r = rec(pos1, pos2_6, score1, score2 + pos2_6, !p1);
            wins1 += 7 * r.a;
            wins2 += 7 * r.b;
        }
        var pos2_7 = pos2 + 7; if (pos2_7 > 10) { pos2_7 -= 10; }
        if (score2 + pos2_7 >= 21) {
            wins2 += 6;
        } else {
            const r = rec(pos1, pos2_7, score1, score2 + pos2_7, !p1);
            wins1 += 6 * r.a;
            wins2 += 6 * r.b;
        }
        var pos2_8 = pos2 + 8; if (pos2_8 > 10) { pos2_8 -= 10; }
        if (score2 + pos2_8 >= 21) {
            wins2 += 3;
        } else {
            const r = rec(pos1, pos2_8, score1, score2 + pos2_8, !p1);
            wins1 += 3 * r.a;
            wins2 += 3 * r.b;
        }
        var pos2_9 = pos2 + 9; if (pos2_9 > 10) { pos2_9 -= 10; }
        if (score2 + pos2_9 >= 21) {
            wins2 += 1;
        } else {
            const r = rec(pos1, pos2_9, score1, score2 + pos2_9, !p1);
            wins1 += r.a;
            wins2 += r.b;
        }
    }

    return Pair { .a = wins1, .b = wins2 };
}

fn part1() void {
    // var pos1: u16 = 4;   var pos2: u16 = 8;
    var pos1: u16 = 5;   var pos2: u16 = 8;
    var score1: u16 = 0; var score2: u16 = 0;

    var diePos: u16 = 1;

    var i: u16 = 0;
    var rolls: u16 = 0;
    while (true) : (i += 1) {
        // std.debug.print("move {}\n", .{i});
        pos1 += detDie(&diePos);
        pos1 += detDie(&diePos);
        pos1 += detDie(&diePos);
        // std.debug.print("p1 moves to {} ", .{pos1});
        rolls += 3;
        while (pos1 > 10) { pos1 -= 10; }
        score1 += pos1;
        // std.debug.print("and ends up at {} => score {}\n", .{pos1, score1});
        if (score1 >= 1000) {
            break;
        }

        pos2 += detDie(&diePos) + detDie(&diePos) + detDie(&diePos);
        // std.debug.print("p2 moves to {} ", .{pos2});
        rolls += 3;
        while (pos2 > 10) { pos2 -= 10; }
        score2 += pos2;
        // std.debug.print("and ends up at {} => score {}\n", .{pos1, score2});
        if (score2 >= 1000) {
            break;
        }
    }

    std.debug.print("losing score: {} after {} rolls\n", .{ std.math.min(score1, score2), rolls });
}

pub fn detDie(die: *u16) u16 {
    if (die.* == 100) {
        die.* = 1;
        return 100;
    } else {
        die.* += 1;
        return die.* - 1;
    }
}
