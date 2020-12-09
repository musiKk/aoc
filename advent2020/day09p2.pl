#!/usr/bin/env perl

use 5.32.0;
use List::Util qw/min max/;

my @lines = <>; chomp for @lines;

# my $target = 127;
my $target = 556543474;

my $running_sum = 0;
my @sum_buffer = ();
for my $num (@lines) {
    while ($running_sum > $target) {
        $running_sum -= shift @sum_buffer;
    }

    if ($running_sum == $target) {
        last;
    }

    if ($running_sum < $target) {
        $running_sum += $num;
        push @sum_buffer, $num;
    } else {
        die 'impossible';
    }
}

my ($min, $max) = (min(@sum_buffer), max(@sum_buffer));
say "$min + $max = ", $min+$max;
