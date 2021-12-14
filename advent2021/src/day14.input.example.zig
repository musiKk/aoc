pub const polymer = "NNCB";
pub const Rule = struct { pair: []const u8, insert: u8 };
pub const rules = [_]Rule {
    Rule { .pair = "CH", .insert = 'B' },
    Rule { .pair = "HH", .insert = 'N' },
    Rule { .pair = "CB", .insert = 'H' },
    Rule { .pair = "NH", .insert = 'C' },
    Rule { .pair = "HB", .insert = 'C' },
    Rule { .pair = "HC", .insert = 'B' },
    Rule { .pair = "HN", .insert = 'C' },
    Rule { .pair = "NN", .insert = 'C' },
    Rule { .pair = "BH", .insert = 'H' },
    Rule { .pair = "NC", .insert = 'B' },
    Rule { .pair = "NB", .insert = 'B' },
    Rule { .pair = "BN", .insert = 'B' },
    Rule { .pair = "BB", .insert = 'N' },
    Rule { .pair = "BC", .insert = 'B' },
    Rule { .pair = "CC", .insert = 'N' },
    Rule { .pair = "CN", .insert = 'C' },
};
