pub const Coordinate = struct { x: u16, y: u16 };
pub const coords = [_]Coordinate {
    Coordinate { .x = 6, .y = 10 },
    Coordinate { .x = 0, .y = 14 },
    Coordinate { .x = 9, .y = 10 },
    Coordinate { .x = 0, .y = 3 },
    Coordinate { .x = 10, .y = 4 },
    Coordinate { .x = 4, .y = 11 },
    Coordinate { .x = 6, .y = 0 },
    Coordinate { .x = 6, .y = 12 },
    Coordinate { .x = 4, .y = 1 },
    Coordinate { .x = 0, .y = 13 },
    Coordinate { .x = 10, .y = 12 },
    Coordinate { .x = 3, .y = 4 },
    Coordinate { .x = 3, .y = 0 },
    Coordinate { .x = 8, .y = 4 },
    Coordinate { .x = 1, .y = 10 },
    Coordinate { .x = 2, .y = 14 },
    Coordinate { .x = 8, .y = 10 },
    Coordinate { .x = 9, .y = 0 },
};
pub const FoldType = enum { x, y };
pub const Fold = union(FoldType) { x: u16, y: u16 };
pub const folds = [_]Fold {
    Fold { .y = 7 },
    Fold { .x = 5 }
};
