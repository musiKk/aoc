const std = @import("std");

// const INPUT = "D2FE28"; // constant packet
// const INPUT = "38006F45291200"; // operator packet len
// const INPUT = "EE00D40C823060"; // operator packet count
// const INPUT = "8A004A801A8002F478"; // example 1
// const INPUT = "C200B40A82"; // example 2 1
// const INPUT = "04005AC33890"; // example 2 2
// const INPUT = "880086C3E88112"; // example 2 3
// const INPUT = "CE00C43D881120"; // example 2 4
// const INPUT = "D8005AC2A8F0"; // example 2 5
// const INPUT = "F600BC2D8F"; // example 2 6
// const INPUT = "9C005AC2F8F0"; // example 2 7
// const INPUT = "9C0141080250320F1802104A08"; // example 2 8
const INPUT = "C20D7900A012FB9DA43BA00B080310CE3643A0004362BC1B856E0144D234F43590698FF31D249F87B8BF1AD402389D29BA6ED6DCDEE59E6515880258E0040A7136712672454401A84CE65023D004E6A35E914BF744E4026BF006AA0008742985717440188AD0CE334D7700A4012D4D3AE002532F2349469100708010E8AD1020A10021B0623144A20042E18C5D88E6009CF42D972B004A633A6398CE9848039893F0650048D231EFE71E09CB4B4D4A00643E200816507A48D244A2659880C3F602E2080ADA700340099D0023AC400C30038C00C50025C00C6015AD004B95002C400A10038C00A30039C0086002B256294E0124FC47A0FC88ACE953802F2936C965D3005AC01792A2A4AC69C8C8CA49625B92B1D980553EE5287B3C9338D13C74402770803D06216C2A100760944D8200008545C8FB1EC80185945D9868913097CAB90010D382CA00E4739EDF7A2935FEB68802525D1794299199E100647253CE53A8017C9CF6B8573AB24008148804BB8100AA760088803F04E244480004323BC5C88F29C96318A2EA00829319856AD328C5394F599E7612789BC1DB000B90A480371993EA0090A4E35D45F24E35D45E8402E9D87FFE0D9C97ED2AF6C0D281F2CAF22F60014CC9F7B71098DFD025A3059200C8F801F094AB74D72FD870DE616A2E9802F800FACACA68B270A7F01F2B8A6FD6035004E054B1310064F28F1C00F9CFC775E87CF52ADC600AE003E32965D98A52969AF48F9E0C0179C8FE25D40149CC46C4F2FB97BF5A62ECE6008D0066A200D4538D911C401A87304E0B4E321005033A77800AB4EC1227609508A5F188691E3047830053401600043E2044E8AE0008443F84F1CE6B3F133005300101924B924899D1C0804B3B61D9AB479387651209AA7F3BC4A77DA6C519B9F2D75100017E1AB803F257895CBE3E2F3FDE014ABC";

const NUMBER_TYPE = u128;

pub fn main() void {
    var arena = std.heap.ArenaAllocator.init(std.heap.page_allocator);
    defer arena.deinit();

    var bits: [INPUT.len * 4]u1 = undefined;
    initBits(&bits);

    var index: u16 = 0;
    const packet = parsePacket(&bits, &index, arena.allocator());
    // std.debug.print("packet: {} (len = {})\n", .{packet, index});
    dumpPacket(packet, 0);

    const sum = sumVersion(packet);
    std.debug.print("sum of versions: {}\n", .{sum});

    const result = evaluatePacket(packet);
    std.debug.print("eval: {}\n", .{result});
}

fn evaluatePacket(packet: Packet) NUMBER_TYPE {
    return switch (packet) {
        .constant => |p| p.value,
        .operator => |os| switch (os.packetType) {
            0 => blk: {
                var sum: NUMBER_TYPE = 0;
                for (os.operands) |o| {
                    sum += evaluatePacket(o);
                }
                break :blk sum;
            },
            1 => blk: {
                var prod: NUMBER_TYPE = 1;
                for (os.operands) |o| {
                    prod *= evaluatePacket(o);
                }
                break :blk prod;
            },
            2 => blk: {
                var min: NUMBER_TYPE = std.math.maxInt(NUMBER_TYPE);
                for (os.operands) |o| {
                    min = std.math.min(min, evaluatePacket(o));
                }
                break :blk min;
            },
            3 => blk: {
                var max: NUMBER_TYPE = 0;
                for (os.operands) |o| {
                    max = std.math.max(max, evaluatePacket(o));
                }
                break :blk max;
            },
            // compiler bug?
            // 5 => @as(NUMBER_TYPE, if (evaluatePacket(os.operands[0]) > evaluatePacket(os.operands[1])) 1 else 0),
            // 6 => @as(NUMBER_TYPE, if (evaluatePacket(os.operands[0]) < evaluatePacket(os.operands[1])) 1 else 0),
            // 7 => @as(NUMBER_TYPE, if (evaluatePacket(os.operands[0]) == evaluatePacket(os.operands[1])) 1 else 0),
            5 => blk: {
                const r = @as(NUMBER_TYPE, if (evaluatePacket(os.operands[0]) > evaluatePacket(os.operands[1])) 1 else 0);
                break :blk r;
            },
            6 => blk: {
                const r = @as(NUMBER_TYPE, if (evaluatePacket(os.operands[0]) < evaluatePacket(os.operands[1])) 1 else 0);
                break :blk r;
            },
            7 => blk: {
                const r = @as(NUMBER_TYPE, if (evaluatePacket(os.operands[0]) == evaluatePacket(os.operands[1])) 1 else 0);
                break :blk r;
            },
            else => unreachable
        }
    };
}

fn sumVersion(packet: Packet) u32 {
    return switch (packet) {
        .constant => |p| p.version,
        .operator => |p| blk: {
            var sum: u32 = p.version;
            for (p.operands) |o| {
                sum += sumVersion(o);
            }
            break :blk sum;
        }
    };
}

fn dumpPacket(packet: Packet, depth: u16) void {
    const whitespace = " " ** 20;
    const prefix = whitespace[0..depth];
    switch (packet) {
        .constant => |p| std.debug.print("{s} - Constant(version={}, value={})\n", .{prefix, p.version, p.value}),
        .operator => |p| {
            std.debug.print("{s} - Operator(version={}, packetType={}, operands=[\n", .{prefix, p.version, p.packetType});
            for (p.operands) |operand| {
                dumpPacket(operand, depth + 1);
            }
            std.debug.print("{s}  ])\n", .{prefix});
        }
    }
}

const PacketType = enum { constant, operator };
const Packet = union(PacketType) {
    constant: ConstantPacket,
    operator: OperatorPacket,
};

const ConstantPacket = struct {
    version: u3,
    value: NUMBER_TYPE
};
const OperatorPacket = struct {
    version: u3,
    packetType: u3,
    operands: []Packet
};

fn parsePacket(bits: []const u1, index: *u16, alloc: std.mem.Allocator) Packet {
    const version = parseU3(bits[index.*..]);
    index.* += 3;
    const packetType = parseU3(bits[index.*..]);
    index.* += 3;

    return switch (packetType) {
        4 => Packet {.constant = ConstantPacket { .version = version, .value = parseValue(bits[index.*..], index) } },
        else => blk: {
            const isLenInBits = bits[index.*] == 0;
            index.* += 1;
            if (isLenInBits) {
                var lenInBits = readBitsLen(bits[index.*..]);
                index.* += 15;
                const lastIndex = index.* + lenInBits;

                var packets = std.ArrayList(Packet).init(alloc);
                while (index.* < lastIndex) {
                    const packet = parsePacket(bits, index, alloc);
                    packets.append(packet) catch unreachable;
                }
                break :blk Packet {
                    .operator = OperatorPacket {
                        .version = version,
                        .packetType = packetType,
                        .operands = packets.toOwnedSlice()
                    }
                };
            } else {
                // len in packets
                var numberOfPackets = readNumberOfPackets(bits[index.*..]);
                index.* += 11;
                var packets = std.ArrayList(Packet).init(alloc);
                var i: u16 = 0;
                while (i < numberOfPackets) : (i += 1) {
                    const packet = parsePacket(bits, index, alloc);
                    packets.append(packet) catch unreachable;
                }
                break :blk Packet {
                    .operator = OperatorPacket {
                        .version = version,
                        .packetType = packetType,
                        .operands = packets.toOwnedSlice()
                    }
                };
            }
        }
    };
}

fn readNumberOfPackets(bits: []const u1) u16 {
    var v: u16 = 0;
    var i: u5 = 0;
    while (i < 11) : (i += 1) {
        v <<= 1;
        v |= bits[i];
    }
    return v;
}

fn readBitsLen(bits: []const u1) u16 {
    var v: u16 = 0;
    var i: u5 = 0;
    while (i < 15) : (i += 1) {
        v <<= 1;
        v |= bits[i];
    }
    return v;
}

fn parseValue(bits: []const u1, index: *u16) NUMBER_TYPE {
    var n: NUMBER_TYPE = 0;
    var i: u16 = 0;
    while (true) : (i += 1) {
        const last = bits[i * 5] == 0;

        n <<= 1;
        n |= bits[i * 5 + 1];
        n <<= 1;
        n |= bits[i * 5 + 2];
        n <<= 1;
        n |= bits[i * 5 + 3];
        n <<= 1;
        n |= bits[i * 5 + 4];

        index.* += 5;

        if (last) return n;
    }
}

fn parseU3(bits: []const u1) u3 {
    var v: u3 = 0;
    v |= bits[0];
    v <<= 1;
    v |= bits[1];
    v <<= 1;
    v |= bits[2];
    return v;
}

fn initBits(bits: []u1) void {
    var i: u16 = 0;
    while (i < INPUT.len) : (i += 1) {
        var n = std.fmt.parseInt(u4, INPUT[i..i+1], 16) catch unreachable;

        bits[i * 4 + 3] = @intCast(u1, n & 1);
        n = n >> 1;
        bits[i * 4 + 2] = @intCast(u1, n & 1);
        n = n >> 1;
        bits[i * 4 + 1] = @intCast(u1, n & 1);
        n = n >> 1;
        bits[i * 4 + 0] = @intCast(u1, n & 1);
    }
}
