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
    # say " - executing program ", (join ', ', @program);
    while (1) {
        # say "executing $ip";
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
            # say " -> adding $arg => $acc";
            $ip++;
        } elsif ($opcode eq "jmp") {
            $ip += $arg;
            # say " -> jumping to $ip";
        } else {
            # nop
            $ip++;
        }
    }
}

# say execute(\@lines)

for my $i (0..$#lines) {
    my $line = $lines[$i];
    # say "checking to replace $line";
    my @lines_copy = @lines;
    if ($line =~ s/^jmp/nop/) {
        # say " - jmp -> nop (new is $line)";
        $lines_copy[$i] = $line;
    } elsif ($line =~ s/^nop/jmp/) {
        # say " - nop -> jmp (new is $line)";
        $lines_copy[$i] = $line;
    } else {
        # say " - no replacing";
        next;
    }
    # local $, = ", ";
    my @result = execute(\@lines_copy);
    if ($result[0]) {
        say $result[1];
    }
}
