/**
 * @author Pavan Kulkarni ,net-id :pxk142330
 */
package com.ML.Project5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KMeansClient {

    public static void main(String[] args) throws IOException {

        int k = Integer.parseInt(args[0]);
        String inputFile = args[1];
        String outputFile = args[2];

        KMeansEucledian kmeans = new KMeansEucledian(k);
        File file = new File(inputFile);

        kmeans.createPointObjects(file);
        Map<Integer, List<Double>> distanceMap = new HashMap<>();
        int size = kmeans.getNoOfPointsInDataSet();
        List<Point> centroidPoints = kmeans.getInitialCentroids(size);

        List<Point> dataPoints = kmeans.getDataPoints();

        int count = 0;
        while (true) {
            count++;

            buildDistanceMap(centroidPoints, dataPoints, kmeans, distanceMap);

            for (Integer i : distanceMap.keySet()) {
                kmeans.assignClusterToPoint(i, distanceMap.get(i));
            }

            List<Point> newCentroids = kmeans.getNewCentroids();

            boolean areCentroidsEqual = checkCentroidsEquality(newCentroids, centroidPoints, k);
            if (areCentroidsEqual || count == 25) {
                calculateSumSquaredError(newCentroids, dataPoints, kmeans);
                System.out.println("Converged after  " + count + " iterations");
                break;
            }
            centroidPoints = newCentroids;
        }
        writeOutputToFile(dataPoints, outputFile);
        System.out.println("Output contents written to file :" + outputFile);

    }

    private static void calculateSumSquaredError(final List<Point> newCentroids, final List<Point> dataPoints, final KMeansEucledian kmeans) {

        Map<Integer, List<Point>> finalClusterMap = new TreeMap<Integer, List<Point>>();
        double sumSquaredError = 0.0;

        for (Point p : dataPoints) {
            if (finalClusterMap.containsKey(p.getCluster())) {
                List<Point> clusterPoints = finalClusterMap.get(p.getCluster());
                clusterPoints.add(p);
                finalClusterMap.put(p.getCluster(), clusterPoints);
            } else {
                List<Point> newClusterPoint = new ArrayList<>();
                newClusterPoint.add(p);
                finalClusterMap.put(p.getCluster(), newClusterPoint);
            }
        }

        for (Integer i : finalClusterMap.keySet()) {
            List<Point> pointsInCluster = finalClusterMap.get(i);
            Point clusterCentroid = newCentroids.get(i - 1);
            for (Point point : pointsInCluster) {
                double distance = kmeans.getEucledianDistance(clusterCentroid, point);
                sumSquaredError += (distance * distance);
            }
        }
        System.out.println("SSE: " + sumSquaredError);
    }

    private static boolean checkCentroidsEquality(final List<Point> newCentroids, final List<Point> centroidPoints, final int k) {

        for (int i = 0; i < k; i++) {
            if (newCentroids.get(i).getX() == centroidPoints.get(i).getX() && newCentroids.get(i).getY() == centroidPoints.get(i).getY()) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private static void writeOutputToFile(final List<Point> dataPoints, final String outputFile) throws IOException {
        Map<Integer, List<Point>> finalClusterMap = new TreeMap<Integer, List<Point>>();
        File file = new File(outputFile);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("<cluster-id> :  <List  of points  ids separated by comma>" + "\n");
        for (Point p : dataPoints) {
            if (finalClusterMap.containsKey(p.getCluster())) {
                List<Point> clusterPoints = finalClusterMap.get(p.getCluster());
                clusterPoints.add(p);
                finalClusterMap.put(p.getCluster(), clusterPoints);
            } else {
                List<Point> newClusterPoint = new ArrayList<>();
                newClusterPoint.add(p);
                finalClusterMap.put(p.getCluster(), newClusterPoint);
            }
        }

        for (Integer i : finalClusterMap.keySet()) {
            StringBuilder sb = new StringBuilder();
            List<Point> clusterPoints = finalClusterMap.get(i);
            for (Point p : clusterPoints) {
                sb.append(p.getId() + " ,");
            }
            bw.write(i + " : " + sb.toString() + "\n");
        }

        bw.close();
    }

    private static void buildDistanceMap(final List<Point> centroidPoints, final List<Point> dataPoints, final KMeansEucledian kmeans,
            Map<Integer, List<Double>> distanceMap) {
        distanceMap.clear();
        for (Point p1 : centroidPoints) {
            for (Point p2 : kmeans.getDataPoints()) {
                Double distance = kmeans.getEucledianDistance(p1, p2);
                if (distanceMap.containsKey(p2.getId())) {
                    List<Double> disFromCentroids = distanceMap.get(p2.getId());
                    disFromCentroids.add(distance);
                    distanceMap.put(p2.getId(), disFromCentroids);
                } else {
                    List<Double> list = new ArrayList<>();
                    list.add(distance);
                    distanceMap.put(p2.getId(), list);
                }
            }
        }

    }

}
