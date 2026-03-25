package com.bank.api.util;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.MatOfByte;

import java.util.Base64;

public class Base64Utils {

    public static Mat base64ToMat(String base64) {

        byte[] bytes = Base64.getDecoder().decode(base64);
        return Imgcodecs.imdecode(new MatOfByte(bytes),
                Imgcodecs.IMREAD_COLOR);
    }
}
