/**
 * @author Pavan Kulkarni ,net-id :pxk142330
 */
package com.ML.Project5b;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KmeansJaccardClient {

    public static void main(String[] args) throws IOException {

        int k = Integer.parseInt(args[0]);
        String initialSeedsFile = args[1];
        String inputFile = args[2];
        String outputFile = args[3];

        KMeansJaccard kmj = new KMeansJaccard(k);
        kmj.makeTweetObjects(inputFile);
        List<Tweets> centroidPoints = kmj.getInitialCentroids(initialSeedsFile);

        Map<Long, List<Double>> distanceMap = new LinkedHashMap<>();
        List<Tweets> tweets = kmj.getTweetList();

        buildDistanceMap(centroidPoints, tweets, kmj, distanceMap);

        for (Long i : distanceMap.keySet()) {
            kmj.assignClusterToTweet(i, distanceMap.get(i));
        }

        List<Tweets> tweetsWithNewClusterId = tweets;

        double cost = calculateCost(tweets, centroidPoints, kmj);

        Map<Integer, List<Tweets>> tweetClusterMapping = getTweetCLusterMapping(tweets);
        for (Integer i : tweetClusterMapping.keySet()) {
            List<Tweets> newCentroids = kmj.getNewCentroids(i, tweetClusterMapping.get(i));
            distanceMap.clear();
            buildDistanceMap(newCentroids, tweets, kmj, distanceMap);

            for (Long id : distanceMap.keySet()) {
                kmj.assignClusterToTweet(id, distanceMap.get(id));
            }

            double newCost = calculateCost(tweets, newCentroids, kmj);
            if (newCost < cost) {
                cost = newCost;
                tweetsWithNewClusterId = kmj.getTweetList();
                centroidPoints = newCentroids;
            }
        }

        double sse = calculateSSE(tweetsWithNewClusterId, centroidPoints, kmj);
        System.out.println("Cost: " + cost);
        System.out.println("SSE: " + sse);
        writeOutputToFile(tweetsWithNewClusterId, outputFile);
        System.out.println("Output contents written to file :" + outputFile);

    }

    private static double calculateSSE(final List<Tweets> tweets, final List<Tweets> centroidPoints, final KMeansJaccard kmj) {
        double sse = 0.0;
        Map<Integer, List<Tweets>> clusterMap = getTweetCLusterMapping(tweets);

        for (Integer clusterId : clusterMap.keySet()) {
            Tweets centroid = centroidPoints.get(clusterId - 1);
            List<Tweets> tweetsInCluster = clusterMap.get(clusterId);

            for (Tweets tweet : tweetsInCluster) {
                double jaccardDistance = kmj.getJaccardDistance(centroid, tweet);
                sse += jaccardDistance * jaccardDistance;
            }
        }
        return sse;
    }

    private static Map<Integer, List<Tweets>> getTweetCLusterMapping(final List<Tweets> tweets) {
        Map<Integer, List<Tweets>> clusterMap = new TreeMap<Integer, List<Tweets>>();

        for (Tweets te : tweets) {
            if (clusterMap.containsKey(te.getClusterId())) {
                List<Tweets> clusterPoints = clusterMap.get(te.getClusterId());
                clusterPoints.add(te);
                clusterMap.put(te.getClusterId(), clusterPoints);
            } else {
                List<Tweets> newClusterPoint = new ArrayList<>();
                newClusterPoint.add(te);
                clusterMap.put(te.getClusterId(), newClusterPoint);
            }
        }
        return clusterMap;
    }

    private static double calculateCost(final List<Tweets> tweets, final List<Tweets> centroidPoints, final KMeansJaccard kmj) {
        double distance = 0.0;
        Map<Integer, List<Tweets>> clusterMap = getTweetCLusterMapping(tweets);

        for (Integer clusterId : clusterMap.keySet()) {
            Tweets centroid = centroidPoints.get(clusterId - 1);
            List<Tweets> tweetsInCluster = clusterMap.get(clusterId);

            for (Tweets tweet : tweetsInCluster) {
                distance += kmj.getJaccardDistance(centroid, tweet);
            }
        }
        return distance;
    }

    private static void writeOutputToFile(final List<Tweets> tweets, final String outputFile) throws IOException {

        Map<Integer, List<Tweets>> finalClusterMap = getTweetCLusterMapping(tweets);
        File file = new File(outputFile);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("<cluster-id>  : <List of tweet ids separated by  comma>" + "\n");
        for (Integer i : finalClusterMap.keySet()) {
            StringBuilder sb = new StringBuilder();
            List<Tweets> tweetClusters = finalClusterMap.get(i);
            for (Tweets te : tweetClusters) {
                sb.append(te.getId() + " ,");
            }
            bw.write(i + ":" + sb.toString() + "\n");
        }
        bw.close();
    }

    private static void buildDistanceMap(final List<Tweets> centroidPoints, final List<Tweets> tweets, final KMeansJaccard kmj,
            Map<Long, List<Double>> distanceMap) {

        for (Tweets t1 : centroidPoints) {
            for (Tweets t2 : tweets) {
                double jaccardDistance = kmj.getJaccardDistance(t1, t2);
                if (distanceMap.containsKey(t2.getId())) {
                    List<Double> disFromCentroids = distanceMap.get(t2.getId());
                    disFromCentroids.add(jaccardDistance);
                } else {
                    List<Double> list = new ArrayList<>();
                    list.add(jaccardDistance);
                    distanceMap.put(t2.getId(), list);
                }
            }
        }
    }

}
