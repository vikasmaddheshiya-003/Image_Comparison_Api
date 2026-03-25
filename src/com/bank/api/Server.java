/*
 * package com.bank.api;
 * 
 * import com.bank.api.model.*; import com.bank.api.service.ImageCompareService;
 * import com.bank.api.util.Base64Utils; import com.google.gson.Gson;
 * 
 * import io.undertow.Undertow; import io.undertow.util.Headers;
 * 
 * import org.opencv.core.Mat; import nu.pattern.OpenCV;
 * 
 * import java.io.InputStream; import java.nio.charset.StandardCharsets;
 * 
 * public class Server {
 * 
 * public static void main(String[] args) {
 * 
 * OpenCV.loadLocally(); // load opencv
 * 
 * Gson gson = new Gson(); ImageCompareService service = new
 * ImageCompareService();
 * 
 * Undertow server = Undertow.builder() .addHttpListener(8547, "0.0.0.0")
 * .setHandler(exchange -> {
 * 
 * if (exchange.getRequestMethod().toString() .equals("POST")) {
 * 
 * exchange.getRequestReceiver() .receiveFullString((ex, message) -> {
 * 
 * CompareRequest request = gson.fromJson(message, CompareRequest.class);
 * 
 * Mat m1 = Base64Utils .base64ToMat(request.getImage1()); Mat m2 = Base64Utils
 * .base64ToMat(request.getImage2());
 * 
 * double dim = service.dimensionScore(m1, m2); double color =
 * service.colorScore(m1, m2); double ssim = service.ssimScore(m1, m2); double
 * orb = service.orbScore(m1, m2);
 * 
 * double finalScore = service.finalScore(dim, color, ssim, orb);
 * 
 * CompareResponse response = new CompareResponse();
 * 
 * response.dimensionScore = dim; response.colorScore = color;
 * response.ssimScore = ssim; response.orbScore = orb; response.finalScore =
 * finalScore;
 * 
 * response.status = finalScore > 80 ? "MATCH" : "NOT_MATCH";
 * 
 * String json = gson.toJson(response);
 * 
 * ex.getResponseHeaders() .put(Headers.CONTENT_TYPE, "application/json");
 * 
 * ex.getResponseSender() .send(json); });
 * 
 * }
 * 
 * }).build();
 * 
 * server.start(); System.out.println("Server started on port 8547"); } }
 */

package com.bank.api;

import com.bank.api.model.*;
import com.bank.api.service.ImageCompareService;
import com.bank.api.util.Base64Utils;
import com.google.gson.Gson;

import io.undertow.Undertow;
import io.undertow.util.Headers;

import org.opencv.core.Mat;
import nu.pattern.OpenCV;

public class Server {

    public static void main(String[] args) {

        
        OpenCV.loadLocally();
    
        Gson gson = new Gson();
        ImageCompareService service = new ImageCompareService();

        Undertow server = Undertow.builder()
                .addHttpListener(8547, "0.0.0.0")
                .setHandler(exchange -> {

                   
                    if (!exchange.getRequestMethod().toString().equalsIgnoreCase("POST")) {
                        exchange.setStatusCode(405);
                        exchange.getResponseSender().send("Method Not Allowed");
                        return;
                    }

                    exchange.getRequestReceiver()
                            .receiveFullString((ex, message) -> {

                                try {

                                   
                                    CompareRequest request =
                                            gson.fromJson(message, CompareRequest.class);

                                    if (request == null ||
                                            request.getImage1() == null ||
                                            request.getImage2() == null) {

                                        ex.setStatusCode(400);
                                        ex.getResponseSender()
                                                .send("Invalid request payload");
                                        return;
                                    }

                                   
                                    Mat img1 = Base64Utils.base64ToMat(request.getImage1());
                                    Mat img2 = Base64Utils.base64ToMat(request.getImage2());

                                    if (img1.empty() || img2.empty()) {
                                        ex.setStatusCode(400);
                                        ex.getResponseSender()
                                                .send("Invalid image data");
                                        return;
                                    }

                                   
                                   
                                    double dim = service.dimensionScore(img1, img2);
                                    double ssim = service.ssimScore(img1, img2);
                                    double orb = service.orbScore(img1, img2);
                                    double hist = service.score(img1, img2);

                                    double finalScore =
                                            service.finalScore(dim, ssim, orb, hist);

                                 

                                    CompareResponse response = new CompareResponse();
                                    response.dimensionScore = round(dim);
                                    response.ssimScore = round(ssim);
                                    response.orbScore = round(orb);
                                    response.histogramScore = round(hist);
                                    response.finalScore = round(finalScore);
                                    response.status =
                                            finalScore >= 75 ? "MATCH" : "NOT_MATCH";

                                    String json = gson.toJson(response);

                                    ex.getResponseHeaders()
                                            .put(Headers.CONTENT_TYPE, "application/json");

                                    ex.getResponseSender().send(json);
                                   
                                } catch (Exception e) {

                                    e.printStackTrace();

                                    ex.setStatusCode(500);
                                    ex.getResponseSender()
                                            .send("Error processing request");
                                }
                            });
                })
                .build();

        server.start();
        System.out.println(" Server started on port 8547");
    }

  
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}