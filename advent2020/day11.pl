#!/usr/bin/env perl

use v5.32.0;
use Data::Dumper;

# my @board = ("LL", "..");
my @board = <>; map { chomp } @board;
my ($rows, $cols) = (scalar(@board), (length $board[0]));
@board = map { my @r = split //; \@r } @board;

say "rows: $rows, cols: $cols";

sub clone_board {
    my $board_ref = shift;
    my @board_clone = ();
    for my $row_ref (@$board_ref) {
        push @board_clone, [@$row_ref];
    }
    \@board_clone;
}

sub count_occupied {
    my ($board, $r, $c) = @_;
    # say " -> count_occupied for $r $c";

    my $count = 0;
    for my $rd (-1..1) {
        for my $cd (-1..1) {
            if ($rd == 0 && $cd == 0) {
                next;
            }
            my ($check_r, $check_c) = ($r + $rd, $c + $cd);
            # say " -> checking $check_r $check_c";
            if ($check_r < 0 || $check_r > ($rows - 1) || $check_c < 0 || $check_c > ($cols - 1)) {
                next;
            }
            my $tile = $board->[$check_r][$check_c];
            # say "   -> checking tile $tile";
            if ($tile eq "#") {
                $count++;
            }
        }
    }
    # say " -> count result $count";
    $count
}

sub count_occupied2 {
    my ($board, $r, $c) = @_;
    my $count = 0;

    # say " -> checking $r $c";

    # left
    for my $check_c (0..$c-1) {
        my $tile = $board->[$r][$c - 1 - $check_c];
        # say "   -l-> looking at $r ", $c - 1 - $check_c, " $tile";
        if ($tile eq '#') {
            $count++;
            last;
        } elsif ($tile eq 'L') {
            last;
        }
    }
    # right
    for my $check_c ($c+1..$cols-1) {
        my $tile = $board->[$r][$check_c];
        # say "   -r-> looking at $r $check_c $tile";
        if ($tile eq '#') {
            $count++;
            last;
        } elsif ($tile eq 'L') {
            last;
        }
    }
    # top
    for my $check_r (0..$r-1) {
        my $tile = $board->[$r - 1 - $check_r][$c];
        # say "   -t-> looking at ", $r - 1 - $check_r, " $c $tile";
        if ($tile eq '#') {
            $count++;
            last;
        } elsif ($tile eq 'L') {
            last;
        }
    }
    # bottom
    for my $check_r ($r+1..$rows-1) {
        my $tile = $board->[$check_r][$c];
        # say "   -b-> looking at $check_r $c $tile";
        if ($tile eq '#') {
            $count++;
            last;
        } elsif ($tile eq 'L') {
            last;
        }
    }

    # top left
    for (my $check_r=$r-1, my $check_c=$c-1; $check_r >= 0 && $check_c >= 0; $check_r--, $check_c--) {
        my $tile = $board->[$check_r][$check_c];
        # say "   -tl> looking at $check_r $check_c $tile";
        if ($tile eq '#') {
            $count++;
            last;
        } elsif ($tile eq 'L') {
            last;
        }
    }
    # top right
    for (my $check_r=$r-1, my $check_c=$c+1; $check_r >= 0 && $check_c < $cols; $check_r--, $check_c++) {
        my $tile = $board->[$check_r][$check_c];
        # say "   -tr> looking at $check_r $check_c $tile";
        if ($tile eq '#') {
            $count++;
            last;
        } elsif ($tile eq 'L') {
            last;
        }
    }
    # bottom left
    for (my $check_r=$r+1, my $check_c=$c-1; $check_r < $rows && $check_c >= 0; $check_r++, $check_c--) {
        my $tile = $board->[$check_r][$check_c];
        # say "   -bl> looking at $check_r $check_c $tile";
        if ($tile eq '#') {
            $count++;
            last;
        } elsif ($tile eq 'L') {
            last;
        }
    }
    # bottom right
    for (my $check_r=$r+1, my $check_c=$c+1; $check_r < $rows && $check_c < $cols; $check_r++, $check_c++) {
        my $tile = $board->[$check_r][$check_c];
        # say "   -br> looking at $check_r $check_c $tile";
        if ($tile eq '#') {
            $count++;
            last;
        } elsif ($tile eq 'L') {
            last;
        }
    }

    # say " -> checking $r $c => $count";
    # say " -> $count";

    $count
}

sub evolute_board {
    my $board_ref = shift;
    my $count_limit = shift;
    my $new_board = clone_board($board_ref);

    for (my $r=0; $r<$rows; $r++) {
        for (my $c=0; $c<$cols; $c++) {
            my $tile = $board_ref->[$r][$c];
            # say "checking $r $c -> $tile";
            if ($tile eq '.') {
                # floor
                $new_board->[$r][$c] = '.';
            } else {
                my $occupied = count_occupied2($board_ref, $r, $c);
                # say " -> $occupied neighbors";
                if ($tile eq 'L' && $occupied == 0) {
                    $new_board->[$r][$c] = '#';
                } elsif ($tile eq '#' && $occupied >= $count_limit) {
                    $new_board->[$r][$c] = 'L';
                } else {
                    $new_board->[$r][$c] = $tile;
                }
            }
            # print_board($new_board);
            # say;
        }
    }
    $new_board
}

sub cmp_board {
    my ($board_a, $board_b) = @_;
    for (my $r; $r < $rows; $r++) {
        for (my $c; $c < $cols; $c++) {
            my $tile_a = $board_a->[$r][$c];
            my $tile_b = $board_b->[$r][$c];

            # say "cmp $tile_a - $tile_b";

            return 0 unless $tile_a eq $tile_b;

            # say " -> same";
        }
    }
    1
}

sub print_board {
    my $board = shift;
    for my $row (@$board) {
        say @$row;
    }
}

my $cur_board = \@board;
print_board($cur_board);
say;

while (1) {
    my $new_board = evolute_board($cur_board, 5);
    print_board($new_board);
    say;
    if (cmp_board($cur_board, $new_board) == 1) {
        last;
    }
    $cur_board = $new_board;
    # sleep 1;
}


say "final board:";
print_board($cur_board);
