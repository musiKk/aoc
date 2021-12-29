pub const State = enum { on, off };
pub const Range = struct { from: i32, to: i32 };
pub const Step = struct { state: State, block: Block };
pub const Block = struct { x: Range, y: Range, z: Range };

pub const steps = [_]Step {
    Step { .state = .on, .block = Block { .x = Range { .from = -20, .to = 26 }, .y = Range { .from = -36, .to = 17 }, .z = Range { .from = -47, .to = 7 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -20, .to = 33 }, .y = Range { .from = -21, .to = 23 }, .z = Range { .from = -26, .to = 28 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -22, .to = 28 }, .y = Range { .from = -29, .to = 23 }, .z = Range { .from = -38, .to = 16 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -46, .to = 7 }, .y = Range { .from = -6, .to = 46 }, .z = Range { .from = -50, .to = -1 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -49, .to = 1 }, .y = Range { .from = -3, .to = 46 }, .z = Range { .from = -24, .to = 28 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = 2, .to = 47 }, .y = Range { .from = -22, .to = 22 }, .z = Range { .from = -23, .to = 27 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -27, .to = 23 }, .y = Range { .from = -28, .to = 26 }, .z = Range { .from = -21, .to = 29 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -39, .to = 5 }, .y = Range { .from = -6, .to = 47 }, .z = Range { .from = -3, .to = 44 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -30, .to = 21 }, .y = Range { .from = -8, .to = 43 }, .z = Range { .from = -13, .to = 34 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -22, .to = 26 }, .y = Range { .from = -27, .to = 20 }, .z = Range { .from = -29, .to = 19 } } },
    Step { .state = .off, .block = Block { .x = Range { .from = -48, .to = -32 }, .y = Range { .from = 26, .to = 41 }, .z = Range { .from = -47, .to = -37 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -12, .to = 35 }, .y = Range { .from = 6, .to = 50 }, .z = Range { .from = -50, .to = -2 } } },
    Step { .state = .off, .block = Block { .x = Range { .from = -48, .to = -32 }, .y = Range { .from = -32, .to = -16 }, .z = Range { .from = -15, .to = -5 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -18, .to = 26 }, .y = Range { .from = -33, .to = 15 }, .z = Range { .from = -7, .to = 46 } } },
    Step { .state = .off, .block = Block { .x = Range { .from = -40, .to = -22 }, .y = Range { .from = -38, .to = -28 }, .z = Range { .from = 23, .to = 41 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -16, .to = 35 }, .y = Range { .from = -41, .to = 10 }, .z = Range { .from = -47, .to = 6 } } },
    Step { .state = .off, .block = Block { .x = Range { .from = -32, .to = -23 }, .y = Range { .from = 11, .to = 30 }, .z = Range { .from = -14, .to = 3 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -49, .to = -5 }, .y = Range { .from = -3, .to = 45 }, .z = Range { .from = -29, .to = 18 } } },
    Step { .state = .off, .block = Block { .x = Range { .from = 18, .to = 30 }, .y = Range { .from = -20, .to = -8 }, .z = Range { .from = -3, .to = 13 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -41, .to = 9 }, .y = Range { .from = -7, .to = 43 }, .z = Range { .from = -33, .to = 15 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = -54112, .to = -39298 }, .y = Range { .from = -85059, .to = -49293 }, .z = Range { .from = -27449, .to = 7877 } } },
    Step { .state = .on, .block = Block { .x = Range { .from = 967, .to = 23432 }, .y = Range { .from = 45373, .to = 81175 }, .z = Range { .from = 27513, .to = 53682 } } },
    // Step { .state = .on,  .block = Block { .x = Range { .from = 10, .to = 12 }, .y = Range { .from = 10, .to = 12 }, .z = Range { .from = 10, .to = 12 } } },
    // Step { .state = .on,  .block = Block { .x = Range { .from = 11, .to = 13 }, .y = Range { .from = 11, .to = 13 }, .z = Range { .from = 11, .to = 13 } } },
    // Step { .state = .off, .block = Block { .x = Range { .from =  9, .to = 11 }, .y = Range { .from =  9, .to = 11 }, .z = Range { .from =  9, .to = 11 } } },
    // Step { .state = .on,  .block = Block { .x = Range { .from = 10, .to = 10 }, .y = Range { .from = 10, .to = 10 }, .z = Range { .from = 10, .to = 10 } } },
};
