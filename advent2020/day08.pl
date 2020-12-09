#!/usr/bin/env perl

use strict;
use warnings;
use 5.32.0;

my @lines = <>;
map { chomp } @lines;


sub execute {
    my $ip = 0;
    my $acc = 0;
    my %seen = ();
    my @program = @{shift()};
    while (1) {
        return (1, $acc) if $ip > $#program;
        if (exists $seen{$ip}) {
            return (0, $acc);
        } else {
            $seen{$ip} = 1;
        }

        my $instruction = $program[$ip];
        my ($opcode, $arg) = $instruction =~ /(...) (.\d+)/;

        if ($opcode eq "acc") {
            $acc += $arg;
            $ip++;
        } elsif ($opcode eq "jmp") {
            $ip += $arg;
        } else {
            # nop
            $ip++;
        }
    }
}

# part 1
# say execute(\@lines)

for my $i (0..$#lines) {
    my $line = $lines[$i];
    my @lines_copy = @lines;
    if ($line =~ s/^jmp/nop/) {
        $lines_copy[$i] = $line;
    } elsif ($line =~ s/^nop/jmp/) {
        $lines_copy[$i] = $line;
    } else {
        next;
    }
    my @result = execute(\@lines_copy);
    if ($result[0]) {
        say $result[1];
    }
}
