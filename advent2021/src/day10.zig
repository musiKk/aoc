const std = @import("std");
const input = @import("day10.input.zig").input;

pub fn main() void {
    var sum: u32 = 0;
    for (input) |line| {
        const e = error_for_line(line);
        switch(e) {
            ResultType.corrupt => |v| sum += v,
            ResultType.incomplete => |v| std.debug.print("{}\n", .{v}),
        }
    }
    std.debug.print("error sum: {}\n", .{sum});
}

const ResultType = enum { incomplete, corrupt };
const Result = union(ResultType) {
    incomplete: u64,
    corrupt: u32,
};

fn error_for_line(line: []const u8) Result {
    var stack: [100]u8 = undefined;
    var stack_size: u8 = 0;

    var err: ?u16 = null;

    for (line) |c| {
        if (c == '(' or c == '<' or c == '{' or c == '[') {
            stack[stack_size] = c;
            stack_size += 1;
        } else {
            if (stack_size == 0) {
                unreachable;
            }

            const p = stack[stack_size - 1];
            stack_size -= 1;

            var e: ?u16 = null;
            if (c == ')' and p != '(') {
                e = 3;
            } else if (c == ']' and p != '[') {
                e = 57;
            } else if (c == '}' and p != '{') {
                e = 1197;
            } else if (c == '>' and p != '<') {
                e = 25137;
            }

            if (err == null and e != null) {
                err = e;
            }
        }
    }

    if (err != null) {
        return Result { .corrupt = err.? };
    } else if (stack_size == 0) {
        return Result { .corrupt = 0 };
    } else {
        var score: u64 = 0;
        while (stack_size > 0) : (stack_size -= 1) {
            var v: u3 = 0;
            const p = stack[stack_size - 1];
            if (p == '(') {
                v = 1;
            } else if (p == '[') {
                v = 2;
            } else if (p == '{') {
                v = 3;
            } else if (p == '<') {
                v = 4;
            } else {
                unreachable;
            }
            score *= 5;
            score += v;
        }
        return Result { .incomplete = score };
    }

}
