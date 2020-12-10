#!/usr/bin/env perl

use v5.32.0;
use Data::Dumper;
use bignum;

my @lines = <>; chomp for @lines;

@lines = sort { $a <=> $b } @lines;

my %diffs = ();
my $last = 0;
my $current_1_run = 0;
my @one_runs = ();
for my $num (@lines) {
    my $diff = $num - $last;
    $diffs{$diff} = 0 unless exists $diffs{$diff};
    $diffs{$diff}++;
    if ($diff == 1) {
        $current_1_run++;
    } elsif ($current_1_run > 0) {
        push @one_runs, $current_1_run if $current_1_run > 1;
        $current_1_run = 0;
    }
    $last = $num;
}
push @one_runs, $current_1_run if $current_1_run > 1;
$diffs{'3'}++;


my $total = 1;
for my $one_run (@one_runs) {
    if ($one_run == 2) {
        $total *= 2;
    } elsif ($one_run == 3) {
        $total *= 4;
    } elsif ($one_run == 4) {
        $total *= 7;
    } else {
        die "unaccounted run of $one_run"
    }
}
say $total;
