package com.ML.Project5;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KMeansEucledianTest {

    @Test
    public void checkEucledianDistance() {
        double actualdistance = 2.0;
        Point p1 = new Point(4.0, 2.0);
        Point p2 = new Point(2.0, 2.0);

        KMeansEucledian kmeans = new KMeansEucledian(5);

        double calculatedDistance = kmeans.getEucledianDistance(p1, p2);

        assertEquals("Unexpected Distances", actualdistance, calculatedDistance, 0.0);

    }

}
