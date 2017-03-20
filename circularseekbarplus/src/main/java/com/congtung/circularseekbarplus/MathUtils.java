package com.congtung.circularseekbarplus;

public class MathUtils {
    public static float distance(float xA, float yA, float xP, float yP) {
        return (float) Math.sqrt((xA - xP) * (xA - xP) + (yA - yP) * (yA - yP));
    }

    public static boolean isANearerPointThanB(float xA, float yA, float xP, float yP, float xB,
                                              float yB) {
        return distance(xA, yA, xP, yP) < distance(xB, yB, xP, yP);
    }
}
