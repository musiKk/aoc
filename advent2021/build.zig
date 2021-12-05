const std = @import("std");

pub fn build(b: *std.build.Builder) void {
    // Standard target options allows the person running `zig build` to choose
    // what target to build for. Here we do not override the defaults, which
    // means any target is allowed, and the default is native. Other options
    // for restricting supported target set are available.
    const target = b.standardTargetOptions(.{});

    // Standard release options allow the person running `zig build` to select
    // between Debug, ReleaseSafe, ReleaseFast, and ReleaseSmall.
    const mode = b.standardReleaseOptions();

    // const exe = b.addExecutable("advent2021", "src/main.zig");
    // exe.setTarget(target);
    // exe.setBuildMode(mode);
    // exe.install();

    // const run_cmd = exe.run();
    // run_cmd.step.dependOn(b.getInstallStep());
    // if (b.args) |args| {
    //     run_cmd.addArgs(args);
    // }

    // const run_step = b.step("run", "Run the app");
    // run_step.dependOn(&run_cmd.step);

    var d: u5 = 0;
    while (d <= 24) {
        d += 1;

        const file_name = b.fmt("src/day{}.zig", .{ d });
        std.fs.cwd().access(file_name, .{ .read = true }) catch continue;

        const day_exe = b.addExecutable(
            b.fmt("day{}", .{ d }),
            file_name);
        day_exe.setTarget(target);
        day_exe.setBuildMode(mode);
        day_exe.install();

        const run_day_cmd = day_exe.run();
        run_day_cmd.step.dependOn(b.getInstallStep());

        const run_day_step = b.step(
            b.fmt("day{}", .{ d }),
            b.fmt("run day{}", .{ d }));
        run_day_step.dependOn(&run_day_cmd.step);
    }
}
