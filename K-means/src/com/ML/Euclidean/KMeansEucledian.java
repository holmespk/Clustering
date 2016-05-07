/**
 * @author Pavan Kulkarni ,net-id :pxk142330
 */
package com.ML.Project5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class KMeansEucledian {

    private int k;
    private List<Point> dataPoints;
    private List<Point> centroids;

    public List<Point> getCentroids() {
        return centroids;
    }

    public void setCentroids(final List<Point> centroids) {
        this.centroids = centroids;
    }

    public KMeansEucledian(final int k) {
        this.k = k;
        dataPoints = new ArrayList<>();
        centroids = new ArrayList<>();
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public List<Point> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(final List<Point> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public int getNoOfPointsInDataSet() {
        return dataPoints.size();
    }

    public double getEucledianDistance(final Point p1, final Point p2) {
        double xCordinate = p2.getX() - p1.getX();
        double yCordinate = p2.getY() - p1.getY();
        double distance = Math.sqrt((xCordinate * xCordinate) + (yCordinate * yCordinate));

        return distance;
    }

    public List<Point> getInitialCentroids(final int noOfDataPoints) {
        List<Point> initialCentroids = new ArrayList<>();
        Set<Integer> checkSet = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            int rand = random.nextInt(noOfDataPoints);
            boolean isNewRand;
            while (true) {
                isNewRand = checkSet.add(rand);
                if (!isNewRand) {
                    rand = random.nextInt(noOfDataPoints);
                } else {
                    break;
                }
            }

            initialCentroids.add(dataPoints.get(rand));
        }
        return initialCentroids;
    }

    public void createPointObjects(final File file) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            br.readLine();
            String s = null;
            while ((s = br.readLine()) != null) {
                String[] pointAttributes = s.split("\\s");
                int id = Integer.parseInt(pointAttributes[0]);
                double x = Double.parseDouble(pointAttributes[1]);
                double y = Double.parseDouble(pointAttributes[2]);

                dataPoints.add(new Point(id, x, y));
            }
        } catch (FileNotFoundException e) {
            System.err.println("No file like " + file + ",exists");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
    }

    public void assignClusterToPoint(final int pointId, final List<Double> distanceList) {
        double min = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < distanceList.size(); i++) {
            if (min > distanceList.get(i)) {
                index = i;
                min = distanceList.get(i);
            }
        }
        dataPoints.get(pointId - 1).setCluster(index + 1);

    }

    public List<Point> getNewCentroids() {
        Map<Integer, List<Point>> clusterPointMapping = new TreeMap<Integer, List<Point>>();

        List<Point> centroids = new ArrayList<>();
        for (Point p : dataPoints) {
            if (clusterPointMapping.containsKey(p.getCluster())) {
                List<Point> clusterPoints = clusterPointMapping.get(p.getCluster());
                clusterPoints.add(p);
                clusterPointMapping.put(p.getCluster(), clusterPoints);
            } else {
                List<Point> newClusterPoint = new ArrayList<>();
                newClusterPoint.add(p);
                clusterPointMapping.put(p.getCluster(), newClusterPoint);
            }
        }

        for (Integer i : clusterPointMapping.keySet()) {
            List<Point> list = clusterPointMapping.get(i);
            double sumx = 0.0, sumy = 0.0;
            int count = 0;
            for (Point p : list) {
                sumx += p.getX();
                sumy += p.getY();
                count++;
            }
            centroids.add(new Point(sumx / count, sumy / count));
        }
        return centroids;
    }

}
