package com.vobis.discordrpg.util;

public class MathUtil {

    public static float sqrt(float x) {
        return (float) Math.sqrt(x);
    }

    public static float clamp(float x, float min, float max) {
        if(x < min) {
            return min;
        } else if(x > max) {
            return max;
        }

        return x;
    }

    public static float pow(float x, float y) {
        return (float) Math.pow(x, y);
    }

    public static int floor(float x) {
        return (int) Math.floor(x);
    }
}
