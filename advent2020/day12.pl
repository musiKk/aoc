#!/usr/bin/env perl

use v5.32.0;

my @lines = <>; chomp for @lines;

my ($x, $y) = (0, 0);
my $angle = 90;
for my $instruction (@lines) {
    my ($action, $amount) = $instruction =~ /(.)(\d+)/;

    if ($action eq "N") {
        $y += $amount;
    } elsif ($action eq "E") {
        $x += $amount;
    } elsif ($action eq "S") {
        $y -= $amount;
    } elsif ($action eq "W") {
        $x -= $amount;
    } elsif ($action eq "L") {
        $angle -= $amount;
        $angle += 360 while $angle < 0;
    } elsif ($action eq "R") {
        $angle += $amount;
        $angle -= 360 while $angle >= 360;
    } elsif ($action eq "F") {
        if ($angle == 0) {
            $y += $amount;
        } elsif ($angle == 90) {
            $x += $amount;
        } elsif ($angle == 180) {
            $y -= $amount;
        } elsif ($angle == 270) {
            $x -= $amount;
        } else {
            die "unknown angle $angle";
        }
    } else {
        die "unknown instruction $action $amount"
    }
}

say "x: $x, y: $y";

my ($x_ship, $y_ship, $x_wp_diff, $y_wp_diff) = (0, 0, 10, 1);
for my $instruction (@lines) {
    my ($action, $amount) = $instruction =~ /(.)(\d+)/;

    if ($action eq "N") {
        $y_wp_diff += $amount;
    } elsif ($action eq "E") {
        $x_wp_diff += $amount;
    } elsif ($action eq "S") {
        $y_wp_diff -= $amount;
    } elsif ($action eq "W") {
        $x_wp_diff -= $amount;
    } elsif ($action eq "L") {
        while ($amount > 0) {
            ($x_wp_diff, $y_wp_diff) = (-$y_wp_diff, $x_wp_diff);
            $amount -= 90;
        }
    } elsif ($action eq "R") {
        while ($amount > 0) {
            ($x_wp_diff, $y_wp_diff) = ($y_wp_diff, -$x_wp_diff);
            $amount -= 90;
        }
    } elsif ($action eq "F") {
        $x_ship += $amount * $x_wp_diff;
        $y_ship += $amount * $y_wp_diff;
    } else {
        die "unknown instruction $action $amount"
    }
}

say "x: $x_ship, y: $y_ship";
