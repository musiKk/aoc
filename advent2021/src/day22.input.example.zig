pub const State = enum { on, off };
pub const Range = struct { from: i32, to: i32 };
pub const Step = struct { state: State, block: Block };
pub const Block = struct { x: Range, y: Range, z: Range };

pub const steps = [_]Step {
    Step { .state = .on,  .block = Block { .x = Range { .from = 10, .to = 12 }, .y = Range { .from = 10, .to = 12 }, .z = Range { .from = 10, .to = 12 } } },
    Step { .state = .on,  .block = Block { .x = Range { .from = 11, .to = 13 }, .y = Range { .from = 11, .to = 13 }, .z = Range { .from = 11, .to = 13 } } },
    Step { .state = .off, .block = Block { .x = Range { .from =  9, .to = 11 }, .y = Range { .from =  9, .to = 11 }, .z = Range { .from =  9, .to = 11 } } },
    Step { .state = .on,  .block = Block { .x = Range { .from = 10, .to = 10 }, .y = Range { .from = 10, .to = 10 }, .z = Range { .from = 10, .to = 10 } } },
};
