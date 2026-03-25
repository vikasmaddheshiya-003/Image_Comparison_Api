/*
 * package com.bank.api.service;
 * 
 * import com.bank.api.util.ScoreUtils;
 * 
 * import java.util.ArrayList; import java.util.List;
 * 
 * import org.opencv.core.*; import org.opencv.imgproc.Imgproc; import
 * org.opencv.features2d.*;
 * 
 * public class ImageCompareService {
 * 
 * public double dimensionScore(Mat m1, Mat m2) {
 * 
 * if (m1.width() == m2.width() && m1.height() == m2.height()) return 100;
 * 
 * return 0; }
 * 
 * 
 * public double colorScore(Mat img1, Mat img2) {
 * 
 * Mat hsv1 = new Mat(); Mat hsv2 = new Mat();
 * 
 * Imgproc.cvtColor(img1, hsv1, Imgproc.COLOR_BGR2HSV); Imgproc.cvtColor(img2,
 * hsv2, Imgproc.COLOR_BGR2HSV);
 * 
 * Scalar mean1 = Core.mean(hsv1); Scalar mean2 = Core.mean(hsv2);
 * 
 * double diffH = Math.abs(mean1.val[0] - mean2.val[0]); double diffS =
 * Math.abs(mean1.val[1] - mean2.val[1]); double diffV = Math.abs(mean1.val[2] -
 * mean2.val[2]);
 * 
 * double totalDiff = (diffH + diffS + diffV) / 3.0;
 * 
 * return Math.max(0, 100 - totalDiff); }
 * 
 * public double ssimScore(Mat img1, Mat img2) {
 * 
 * if (img1.empty() || img2.empty()) return 0;
 * 
 * // Resize if sizes differ if (!img1.size().equals(img2.size())) {
 * Imgproc.resize(img2, img2, img1.size()); }
 * 
 * // Convert to grayscale Mat gray1 = new Mat(); Mat gray2 = new Mat();
 * Imgproc.cvtColor(img1, gray1, Imgproc.COLOR_BGR2GRAY); Imgproc.cvtColor(img2,
 * gray2, Imgproc.COLOR_BGR2GRAY);
 * 
 * // Convert to float gray1.convertTo(gray1, CvType.CV_32F);
 * gray2.convertTo(gray2, CvType.CV_32F);
 * 
 * double C1 = 6.5025, C2 = 58.5225;
 * 
 * // Gaussian blur = local mean Mat mu1 = new Mat(); Mat mu2 = new Mat();
 * Imgproc.GaussianBlur(gray1, mu1, new Size(11, 11), 1.5);
 * Imgproc.GaussianBlur(gray2, mu2, new Size(11, 11), 1.5);
 * 
 * Mat mu1_sq = new Mat(); Mat mu2_sq = new Mat(); Mat mu1_mu2 = new Mat();
 * 
 * Core.multiply(mu1, mu1, mu1_sq); Core.multiply(mu2, mu2, mu2_sq);
 * Core.multiply(mu1, mu2, mu1_mu2);
 * 
 * // Variance Mat sigma1_sq = new Mat(); Mat sigma2_sq = new Mat(); Mat sigma12
 * = new Mat();
 * 
 * Imgproc.GaussianBlur(gray1.mul(gray1), sigma1_sq, new Size(11,11), 1.5);
 * Core.subtract(sigma1_sq, mu1_sq, sigma1_sq);
 * 
 * Imgproc.GaussianBlur(gray2.mul(gray2), sigma2_sq, new Size(11,11), 1.5);
 * Core.subtract(sigma2_sq, mu2_sq, sigma2_sq);
 * 
 * // Covariance Imgproc.GaussianBlur(gray1.mul(gray2), sigma12, new
 * Size(11,11), 1.5); Core.subtract(sigma12, mu1_mu2, sigma12);
 * 
 * // SSIM formula Mat t1 = new Mat(), t2 = new Mat(), t3 = new Mat();
 * 
 * Core.multiply(mu1_mu2, new Scalar(2), t1); Core.add(t1, new Scalar(C1), t1);
 * 
 * Core.multiply(sigma12, new Scalar(2), t2); Core.add(t2, new Scalar(C2), t2);
 * 
 * Core.multiply(t1, t2, t3);
 * 
 * Core.add(mu1_sq, mu2_sq, t1); Core.add(t1, new Scalar(C1), t1);
 * 
 * Core.add(sigma1_sq, sigma2_sq, t2); Core.add(t2, new Scalar(C2), t2);
 * 
 * Core.multiply(t1, t2, t1);
 * 
 * Mat ssim_map = new Mat(); Core.divide(t3, t1, ssim_map);
 * 
 * Scalar mssim = Core.mean(ssim_map);
 * 
 * // Convert to percentage (0–100) return Math.max(0, Math.min(100,
 * mssim.val[0] * 100)); }
 * 
 * public double orbScore(Mat img1, Mat img2) {
 * 
 * if (img1.empty() || img2.empty()) return 0;
 * 
 * ORB orb = ORB.create(1000); // detect up to 1000 features
 * 
 * MatOfKeyPoint kp1 = new MatOfKeyPoint(); MatOfKeyPoint kp2 = new
 * MatOfKeyPoint();
 * 
 * Mat desc1 = new Mat(); Mat desc2 = new Mat();
 * 
 * orb.detectAndCompute(img1, new Mat(), kp1, desc1); orb.detectAndCompute(img2,
 * new Mat(), kp2, desc2);
 * 
 * if (desc1.empty() || desc2.empty()) return 0;
 * 
 * BFMatcher matcher = BFMatcher.create(Core.NORM_HAMMING, false);
 * 
 * List<MatOfDMatch> knnMatches = new ArrayList<>(); matcher.knnMatch(desc1,
 * desc2, knnMatches, 2);
 * 
 * // Lowe's ratio test (removes false matches) int goodMatches = 0; for
 * (MatOfDMatch matOfDMatch : knnMatches) { DMatch[] matches =
 * matOfDMatch.toArray(); if (matches.length >= 2) { if (matches[0].distance <
 * 0.75 * matches[1].distance) { goodMatches++; } } }
 * 
 * int totalKeypoints = Math.max(kp1.toArray().length, kp2.toArray().length);
 * 
 * if (totalKeypoints == 0) return 0;
 * 
 * double similarity = (double) goodMatches / totalKeypoints;
 * 
 * return Math.max(0, Math.min(100, similarity * 100)); }
 * 
 * 
 * public double finalScore( double dim, double color, double ssim, double orb)
 * {
 * 
 * return ScoreUtils.calculateFinal(dim, color, ssim, orb); } }
 */



package com.bank.api.service;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.features2d.*;

import java.util.*;

public class ImageCompareService {

  
    public double dimensionScore(Mat m1, Mat m2) {

        if (m1.empty() || m2.empty()) return 0;

        double wRatio = Math.min(m1.width(), m2.width()) /
                        (double)Math.max(m1.width(), m2.width());

        double hRatio = Math.min(m1.height(), m2.height()) /
                        (double)Math.max(m1.height(), m2.height());

        return ((wRatio + hRatio) / 2.0) * 100;
    }

   
    public double ssimScore(Mat img1, Mat img2) {

        if (img1.empty() || img2.empty()) return 0;

        Mat i1 = preprocessGray(img1);
        Mat i2 = preprocessGray(img2);

        Imgproc.resize(i2, i2, i1.size());

        i1.convertTo(i1, CvType.CV_32F);
        i2.convertTo(i2, CvType.CV_32F);

        double C1 = 6.5025, C2 = 58.5225;

        Mat mu1 = new Mat(), mu2 = new Mat();
        Imgproc.GaussianBlur(i1, mu1, new Size(11, 11), 1.5);
        Imgproc.GaussianBlur(i2, mu2, new Size(11, 11), 1.5);

        Mat mu1Sq = mu1.mul(mu1);
        Mat mu2Sq = mu2.mul(mu2);
        Mat mu1mu2 = mu1.mul(mu2);

        Mat sigma1Sq = new Mat();
        Mat sigma2Sq = new Mat();
        Mat sigma12 = new Mat();

        Imgproc.GaussianBlur(i1.mul(i1), sigma1Sq, new Size(11,11), 1.5);
        Core.subtract(sigma1Sq, mu1Sq, sigma1Sq);

        Imgproc.GaussianBlur(i2.mul(i2), sigma2Sq, new Size(11,11), 1.5);
        Core.subtract(sigma2Sq, mu2Sq, sigma2Sq);

        Imgproc.GaussianBlur(i1.mul(i2), sigma12, new Size(11,11), 1.5);
        Core.subtract(sigma12, mu1mu2, sigma12);

        Mat t1 = new Mat();
        Core.multiply(mu1mu2, new Scalar(2), t1);
        Core.add(t1, new Scalar(C1), t1);

        Mat t2 = new Mat();
        Core.multiply(sigma12, new Scalar(2), t2);
        Core.add(t2, new Scalar(C2), t2);

        Mat numerator = t1.mul(t2);

        Mat t3 = new Mat();
        Core.add(mu1Sq, mu2Sq, t3);
        Core.add(t3, new Scalar(C1), t3);

        Mat t4 = new Mat();
        Core.add(sigma1Sq, sigma2Sq, t4);
        Core.add(t4, new Scalar(C2), t4);

        Mat denominator = t3.mul(t4);

        Mat ssimMap = new Mat();
        Core.divide(numerator, denominator, ssimMap);

        return Math.max(0, Math.min(100, Core.mean(ssimMap).val[0] * 100));
    }

  
    public double orbScore(Mat img1, Mat img2) {

        if (img1.empty() || img2.empty()) return 0;

        Mat g1 = preprocessGray(img1);
        Mat g2 = preprocessGray(img2);
       
        ORB orb = ORB.create(1000);

        MatOfKeyPoint kp1 = new MatOfKeyPoint();
        MatOfKeyPoint kp2 = new MatOfKeyPoint();
        Mat desc1 = new Mat();
        Mat desc2 = new Mat();

        orb.detectAndCompute(g1, new Mat(), kp1, desc1);
        orb.detectAndCompute(g2, new Mat(), kp2, desc2);

        if (desc1.empty() || desc2.empty()) return 0;

        BFMatcher matcher = BFMatcher.create(Core.NORM_HAMMING, false);

        List<MatOfDMatch> knnMatches = new ArrayList<>();
        matcher.knnMatch(desc1, desc2, knnMatches, 2);

       int good = 0;

        for (MatOfDMatch m : knnMatches) {
            DMatch[] d = m.toArray();
            if (d.length >= 2 && d[0].distance < 0.75 * d[1].distance) {
                good++;
            }
        }

        int maxKeypoints =
                Math.max(kp1.toArray().length, kp2.toArray().length);

        return maxKeypoints == 0 ? 0 :
                (good / (double) maxKeypoints) * 100;
    }

   
//    public double histogramScore(Mat img1, Mat img2) {
//
//        if (img1.empty() || img2.empty()) return 0;
//
//        Mat g1 = preprocessGray(img1);
//        Mat g2 = preprocessGray(img2);
//
//        Imgproc.resize(g2, g2, g1.size());
//
//        Mat hist1 = new Mat();
//        Mat hist2 = new Mat();
//
//        Imgproc.calcHist(
//                Arrays.asList(g1),
//                new MatOfInt(0),
//                new Mat(),
//                hist1,
//                new MatOfInt(256),
//                new MatOfFloat(0,256)
//        );
//
//        Imgproc.calcHist(
//                Arrays.asList(g2),
//                new MatOfInt(0),
//                new Mat(),
//                hist2,
//                new MatOfInt(256),
//                new MatOfFloat(0,256)
//        );
//
//        Core.normalize(hist1, hist1);
//        Core.normalize(hist2, hist2);
//
//        double score =
//                Imgproc.compareHist(hist1, hist2,
//                        Imgproc.CV_COMP_CORREL);
//
//        return Math.max(0, score * 100);
//    }

    private static final MatOfInt HIST_SIZE = new MatOfInt(256); // 256 bins
    private static final MatOfFloat HIST_RANGE = new MatOfFloat(0f, 256f);

    public double score(Mat imgA, Mat imgB) {
        if (imgA == null || imgB == null || imgA.empty() || imgB.empty()) {
            return 0.0;
        }

        Mat a = ImageCompareService.toType(imgA, CvType.CV_8U);
        Mat b = ImageCompareService.resizeToMatch(ImageCompareService.toType(imgB, CvType.CV_8U), a);

        Mat histA = new Mat();
        Mat histB = new Mat();
        Imgproc.calcHist(java.util.List.of(a), new MatOfInt(0), new Mat(), histA, HIST_SIZE, HIST_RANGE);
        Imgproc.calcHist(java.util.List.of(b), new MatOfInt(0), new Mat(), histB, HIST_SIZE, HIST_RANGE);

       
        Core.normalize(histA, histA, 1, 0, Core.NORM_L1);
        Core.normalize(histB, histB, 1, 0, Core.NORM_L1);

        double corr = Imgproc.compareHist(histA, histB, Imgproc.CV_COMP_CORREL); // [-1,1]
        if (Double.isNaN(corr) || Double.isInfinite(corr)) {
            return 0.0;
        }
        double score = Math.max(0.0, Math.min(1.0, (corr + 1.0) / 2.0)) * 100.0;
        System.out.println(score);
        return score;
    }

 
    public static Mat toType(Mat src, int cvType) {
        Mat dst = new Mat();
        src.convertTo(dst, cvType);
        return dst;
    }

  
    public static Mat resizeToMatch(Mat src, Mat reference) {
        if (src.rows() == reference.rows() && src.cols() == reference.cols()) {
            return src;
        }
        Mat resized = new Mat();
        Imgproc.resize(src, resized, reference.size(), 0, 0, Imgproc.INTER_LINEAR);
        return resized;
    }

    
    
    public double finalScore(
            double dim,
            double ssim,
            double orb,
            double hist) {

        return (0.15 * dim) +
               (0.35 * ssim) +
               (0.35 * orb) +
               (0.15 * hist);
    }

   
    private Mat preprocessGray(Mat img) {
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }
}