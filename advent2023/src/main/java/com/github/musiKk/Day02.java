package com.github.musiKk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day02 {

    public static void main(String[] args) throws Throwable {
        Games games = Games.read(Path.of("input02.txt"));

        day01(games);
        day02(games);
    }

    static void day02(Games games) {
        int sum = games.games()
                .stream()
                .map(Game::getPower)
                .peek(System.out::println)
                .mapToInt(Integer::intValue)
                .sum();
        System.err.println(sum);
    }

    static void day01(Games games) {
        int requiredRed = 12;
        int requiredGreen = 13;
        int requiredBlue = 14;
        int result = 0;
        gameLoop: for (Game game : games.games()) {
            for (Reveal reveal : game.reveals()) {
                if (reveal.red() > requiredRed || reveal.blue() > requiredBlue || reveal.green() > requiredGreen) {
                    continue gameLoop;
                }
            }
            result += game.id();
        }
        System.err.println(result);
    }

    record Games(List<Game> games) {
        static Games read(Path path) throws IOException{
            List<Game> games;
            try (Stream<String> lines = Files.lines(path)) {
                games = lines.map(Game::of).toList();
            }
            return new Games(games);
        }
    }
    record Game(int id, List<Reveal> reveals) {
        static Pattern linePattern = Pattern.compile("^Game (\\d+): (.*)$");
        static Game of(String line) {
            Matcher lineMatcher = linePattern.matcher(line);
            lineMatcher.matches();
            int id = Integer.parseInt(lineMatcher.group(1));

            String[] revealStrings = lineMatcher.group(2).split("; ");

            return new Game(id, Arrays.stream(revealStrings).map(Reveal::of).toList());
        }
        public int getPower() {
            Reveal r = reveals.stream().reduce(new Reveal(0, 0, 0), (o, n) -> {
                return new Reveal(
                    Math.max(o.red, n.red),
                    Math.max(o.green, n.green),
                    Math.max(o.blue, n.blue));
            });
            return r.red * r.green * r.blue;
        }
    }
    record Reveal(int red, int green, int blue) {
        static Pattern redPattern = Pattern.compile("(\\d+) red");
        static Pattern greenPattern = Pattern.compile("(\\d+) green");
        static Pattern bluePattern = Pattern.compile("(\\d+) blue");
        static Reveal of(String input) {
            Matcher redMatcher = redPattern.matcher(input);
            Matcher greenMatcher = greenPattern.matcher(input);
            Matcher blueMatcher = bluePattern.matcher(input);
            return new Reveal(
                redMatcher.find() ? Integer.parseInt(redMatcher.group(1)) : 0,
                greenMatcher.find() ? Integer.parseInt(greenMatcher.group(1)) : 0,
                blueMatcher.find() ? Integer.parseInt(blueMatcher.group(1)) : 0
            );
        }
    }

}
