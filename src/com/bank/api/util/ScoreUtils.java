package com.bank.api.util;

public class ScoreUtils {

    public static double calculateFinal(
            double dim,
            double color,
            double ssim,
            double orb) {

        return (dim * 0.20)
                + (color * 0.20)
                + (ssim * 0.35)
                + (orb * 0.25);
    }
}
