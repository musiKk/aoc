package com.github.musiKk;

public class Day06 {

    static long time = 56717999L;
    static long record = 334113513502430L;
    // static long time = 71530L;
    // static long record = 940200L;

    public static void main(String[] args) {

        // find lowest
        long left = 0;
        long right = time / 2;
        while (true) {
            long mid = left + (right - left) / 2;

            boolean midBeat = distanceBeat(mid);
            if (midBeat) {
                right = mid;
            } else if (distanceBeat(mid + 1)) {
                break;
            } else {
                left = mid;
            }
        }

        long minValue = left + (right - left) / 2 + 1;
        System.err.println("min value is " + minValue);

        System.err.println(time - 1 - 2 * minValue + 2);

        // // find highest
        // left = time / 2;
        // right = time;
        // while (true) {
        //     long mid = left + (right - left) / 2;

        //     boolean midBeat = distanceBeat(mid);
        //     if (midBeat) {
        //         if (!distanceBeat(mid + 1)) {
        //             break;
        //         } else {
        //             right = mid;
        //         }
        //     } else {
        //         left = mid + 1;
        //     }
        //     // if (midBeat && !distanceBeat(mid + 1)) {
        //     //     break;
        //     // } else if (midBeat) {
        //     //     right = mid;
        //     // } else {
        //     //     left = mid;
        //     // }
        // }
        // long maxValue = left + (right - left) / 2;
        // System.err.println("max value is " + maxValue);
    }

    static boolean distanceBeat(long pressed) {
        return (time - pressed) * pressed > record;
    }

}
