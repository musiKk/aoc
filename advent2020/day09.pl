#!/usr/bin/env perl

use 5.32.0;

my $PREAMBLE_LEN = 25;

my @lines = <>; chomp for @lines;

my @buffer = ();

sub check_sum_in_buf {
    my ($sum_to_search, $buf_ref) = @_;

    my %nums = map { $_ => 1 } @$buf_ref;

    for $a (@$buf_ref) {
        my $desired_b = $sum_to_search - $a;
        return 1 if exists $nums{$desired_b};
    }
    return 0;
}

for my $num (@lines) {
    if (@buffer < $PREAMBLE_LEN) {
        push @buffer, $num;
        next;
    }

    if (!check_sum_in_buf($num, \@buffer)) {
        say $num;
        last;
    }

    push @buffer, $num;
    shift @buffer;
}
